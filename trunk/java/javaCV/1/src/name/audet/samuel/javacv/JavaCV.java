/*
 * Copyright (C) 2009 Samuel Audet
 *
 * This file is part of JavaCV.
 *
 * JavaCV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * JavaCV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaCV.  If not, see <http://www.gnu.org/licenses/>.
 */

package name.audet.samuel.javacv;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import name.audet.samuel.javacv.Parallel.Looper;
import name.audet.samuel.javacv.jna.cv;
import name.audet.samuel.javacv.jna.cxcore;
import name.audet.samuel.javacv.jna.cxcore.CvArr;
import name.audet.samuel.javacv.jna.cxcore.CvMat;
import name.audet.samuel.javacv.jna.cxcore.CvScalar;
import name.audet.samuel.javacv.jna.cxcore.IplImage;

/**
 *
 * @author Samuel Audet
 */
public class JavaCV {
    // this is basically cvGetPerspectiveTransform() using CV_LU instead of
    // CV_SVD, because the latter gives inaccurate results...
    public static CvMat getPerspectiveTransform(double[] src, double[] dst, CvMat map_matrix) {
        // creating and releasing matrices via NIO here in this function...
        // this can easily become a bottleneck
        CvMat A = CvMat.create(8, 8);
        CvMat b = CvMat.create(8, 1);
        CvMat x = CvMat.create(8, 1);

        for(int i = 0; i < 4; ++i ) {
            A.put(i*8+0, src[i*2]);   A.put((i+4)*8+3, src[i*2]);
            A.put(i*8+1, src[i*2+1]); A.put((i+4)*8+4, src[i*2+1]);
            A.put(i*8+2, 1);          A.put((i+4)*8+5, 1);
            A.put(i*8+3, 0);          A.put(i*8+4, 0); A.put(i*8+5, 0);
            A.put((i+4)*8+0, 0);  A.put((i+4)*8+1, 0); A.put((i+4)*8+2, 0);

            A.put(i*8+6,     -src[i*2]  *dst[i*2]);
            A.put(i*8+7,     -src[i*2+1]*dst[i*2]);
            A.put((i+4)*8+6, -src[i*2]  *dst[i*2+1]);
            A.put((i+4)*8+7, -src[i*2+1]*dst[i*2+1]);

            b.put(i,   dst[i*2]);
            b.put(i+4, dst[i*2+1]);
        }
        cxcore.cvSolve(A, b, x, cxcore.CV_LU);
        map_matrix.put(x);
        map_matrix.put(8, 1);

        return map_matrix;
    }

    // applies a projective warp using supersampling...
    // superSrc and superDst are work images and must have the same size, type, etc.
    public static void superWarp(CvArr src, CvArr dst, CvMat map_matrix, int flags,
            CvScalar.ByValue fillval, CvArr superSrc, CvArr superDst) {
        cv.cvResize(src, superSrc, cv.CV_INTER_NN);
        cv.cvWarpPerspective(superSrc, superDst, map_matrix, cv.CV_INTER_NN | flags, fillval);
        cv.cvResize(superDst, dst, cv.CV_INTER_AREA);
    }

    public static double HnToRt(CvMat H, CvMat n, CvMat R, CvMat t) {
        CvMat R1 = CvMat.create(3, 3);
        CvMat t1 = CvMat.create(3, 1);
        CvMat n1 = CvMat.create(3, 1);
        CvMat R2 = CvMat.create(3, 3);
        CvMat t2 = CvMat.create(3, 1);
        CvMat n2 = CvMat.create(3, 1);
        CvMat H1 = CvMat.create(3, 3);
        CvMat H2 = CvMat.create(3, 3);

        double zeta = JavaCV.homogToRt(H, R1, t1, n1, R2, t2, n2);

        // H = R^-1 * H
        cxcore.cvGEMM(R1, H, 1, null, 0, H1, cxcore.CV_GEMM_A_T);
        cxcore.cvGEMM(R2, H, 1, null, 0, H2, cxcore.CV_GEMM_A_T);

        // H = normalize(H) - I
        double k1 = (H1.get(0) + H1.get(4))/2;
        double k2 = (H2.get(0) + H2.get(4))/2;
        cxcore.cvConvertScale(H1, H1, 1/k1, 0);
        cxcore.cvConvertScale(H2, H2, 1/k2, 0);
        H1.put(0, H1.get(0)-1); H1.put(4, H1.get(4)-1); H1.put(8, H1.get(8)-1);
        H2.put(0, H2.get(0)-1); H2.put(4, H2.get(4)-1); H2.put(8, H2.get(8)-1);

        // Now H should ~= -tn^T, so extract "average" t
        double d1 = cxcore.cvNorm(n, null, cxcore.CV_L1);
        double s[] = { -Math.signum(n.get(0)), -Math.signum(n.get(1)), -Math.signum(n.get(2)) };
        cxcore.cvSetZero(t1);
        cxcore.cvSetZero(t2);
        for (int i = 0; i < 3; i++) {
            t1.put(0, t1.get(0) + s[i]*H1.get(i)  /d1);
            t1.put(1, t1.get(1) + s[i]*H1.get(i+3)/d1);
            t1.put(2, t1.get(2) + s[i]*H1.get(i+6)/d1);

            t2.put(0, t2.get(0) + s[i]*H2.get(i)  /d1);
            t2.put(1, t2.get(1) + s[i]*H2.get(i+3)/d1);
            t2.put(2, t2.get(2) + s[i]*H2.get(i+6)/d1);
        }

        // H = H + tn^T
        cxcore.cvGEMM(t1, n, 1, H1, 1, H1, cxcore.CV_GEMM_B_T);
        cxcore.cvGEMM(t2, n, 1, H2, 1, H2, cxcore.CV_GEMM_B_T);

        // take what's left as the error of the model,
        // this either indicates inaccurate camera matrix K or normal vector n
        double err1 = cxcore.cvNorm(H1);
        double err2 = cxcore.cvNorm(H2);

        double err;
        if (err1 < err2) {
            if (R != null) {
                R.put(R1);
            }
            if (t != null) {
                t.put(t1);
            }
            err = err1;
        } else {
            if (R != null) {
                R.put(R2);
            }
            if (t != null) {
                t.put(t2);
            }
            err = err2;
        }

        return err;
    }

    // Ported to Java/OpenCV from
    // Bill Triggs. Autocalibration from Planar Scenes. In 5th European Conference
    // on Computer Vision (ECCV ’98), volume I, pages 89–105. Springer-Verlag, 1998.
    public static double homogToRt(CvMat H,
            CvMat R1, CvMat t1, CvMat n1,
            CvMat R2, CvMat t2, CvMat n2) {
        CvMat S = CvMat.create(3, 3);
        CvMat U = CvMat.create(3, 3);
        CvMat V = CvMat.create(3, 3);

        cxcore.cvSVD(H, S, U, V, 0);
        double s1 = S.get(0)/S.get(4);
        double s3 = S.get(8)/S.get(4);
        double zeta = s1-s3;
        double a1 = Math.sqrt(1 - s3*s3);
        double b1 = Math.sqrt(s1*s1 - 1);
        double[] ab = unitize(a1, b1);
        double[] cd = unitize(1+s1*s3, a1*b1);
        double[] ef = unitize(-ab[1]/s1, -ab[0]/s3);

        S.put(cd[0],0,cd[1], 0,1,0, -cd[1],0,cd[0]);
        cxcore.cvGEMM(U , S, 1, null, 0, R1, 0);
        cxcore.cvGEMM(R1, V, 1, null, 0, R1, cxcore.CV_GEMM_B_T);

        S.put(cd[0],0,-cd[1], 0,1,0, cd[1],0,cd[0]);
        cxcore.cvGEMM(U , S, 1, null, 0, R2, 0);
        cxcore.cvGEMM(R2, V, 1, null, 0, R2, cxcore.CV_GEMM_B_T);

        double[] v1 = { V.get(0), V.get(3), V.get(6) };
        double[] v3 = { V.get(2), V.get(5), V.get(8) };
        double sign1 = 1, sign2 = 1;
        for (int i = 2; i >= 0; i--) {
            n1.put(i, sign1*(ab[1]*v1[i] - ab[0]*v3[i]));
            n2.put(i, sign2*(ab[1]*v1[i] + ab[0]*v3[i]));
            t1.put(i, sign1*(ef[0]*v1[i] + ef[1]*v3[i]));
            t2.put(i, sign2*(ef[0]*v1[i] - ef[1]*v3[i]));
            if (i == 2) {
                if (n1.get(2) < 0) {
                    n1.put(2, -n1.get(2));
                    t1.put(2, -t1.get(2));
                    sign1 = -1;
                }
                if (n2.get(2) < 0) {
                    n2.put(2, -n2.get(2));
                    t2.put(2, -t2.get(2));
                    sign2 = -1;
                }
            }
        }

        return zeta;
    }

    public static double[] unitize(double a, double b) {
        double norm = Math.sqrt(a*a + b*b);
        if (norm > Float.MIN_VALUE) {
            a = a / norm;
            b = b / norm;
        }
        return new double[] { a, b };
    }

    public static void adaptiveBinarization(final IplImage src, final IplImage sumimage, 
            final IplImage sqsumimage, final IplImage dst, final boolean invert,
            final int minwindow, final int maxwindow, final double varmultiplier, final double k) {
        final int w = src.width;
        final int h = src.height;
        final IplImage graysrc;
        if (src.nChannels > 1) {
            cv.cvCvtColor(src, dst, cv.CV_BGR2GRAY);
            graysrc = dst;
        } else {
            graysrc = src;
        }

        // compute integral images
        cv.cvIntegral(src, sumimage, sqsumimage, null);
        final DoubleBuffer sumbuf = sumimage.getByteBuffer().asDoubleBuffer();
        final DoubleBuffer sqsumbuf = sqsumimage.getByteBuffer().asDoubleBuffer();
        final ByteBuffer srcbuf = src.getByteBuffer();
        final ByteBuffer dstbuf = dst.getByteBuffer();

        // try to detect a reasonable maximum and minimum intensity
        // for thresholds instead of simply 0 and 255...
        double totalmean = sumbuf.get((h-1)*sumimage.widthStep/8 + (w-1)) -
                           sumbuf.get((h-1)*sumimage.widthStep/8) -
                           sumbuf.get(w-1) + sumbuf.get(0);
        totalmean /= w*h;
        double totalsqmean = sqsumbuf.get((h-1)*sumimage.widthStep/8 + (w-1)) -
                             sqsumbuf.get((h-1)*sumimage.widthStep/8) -
                             sqsumbuf.get(w-1) + sqsumbuf.get(0);
        totalsqmean /= w*h;
        double totalvar = totalsqmean - totalmean*totalmean;
//double totaldev = Math.sqrt(totalvar);
//System.out.println(totaldev);
        final double targetvar = totalvar*varmultiplier;

        //for (int y = 0; y < h; y++) {
        Parallel.loop(0, h, new Looper() {
        public void loop(int from, int to, int looperId) {
            for (int y = from; y < to; y++) {
                for (int x = 0; x < w; x++) {
                    double var = 0, mean = 0, sqmean = 0;
                    int upperlimit = maxwindow;
                    int lowerlimit = minwindow;
                    int window = upperlimit; // start with maxwindow
                    while (upperlimit - lowerlimit > 2) {
                        int x1 = Math.max(x-window/2, 0);
                        int x2 = Math.min(x+window/2+1, w);

                        int y1 = Math.max(y-window/2, 0);
                        int y2 = Math.min(y+window/2+1, h);

                        mean = sumbuf.get(y2*sumimage.widthStep/8 + x2) -
                               sumbuf.get(y2*sumimage.widthStep/8 + x1) -
                               sumbuf.get(y1*sumimage.widthStep/8 + x2) +
                               sumbuf.get(y1*sumimage.widthStep/8 + x1);
                        mean /= window*window;
                        sqmean = sqsumbuf.get(y2*sqsumimage.widthStep/8 + x2) -
                                           sqsumbuf.get(y2*sqsumimage.widthStep/8 + x1) -
                                           sqsumbuf.get(y1*sqsumimage.widthStep/8 + x2) +
                                           sqsumbuf.get(y1*sqsumimage.widthStep/8 + x1);
                        sqmean /= window*window;
                        var = sqmean - mean*mean;

                        // if we're at maximum window size, but variance is
                        // too low anyway, let's break out immediately
                        if (window == upperlimit && var < targetvar) {
                            break;
                        }

                        // otherwise, start binary search
                        if (var > targetvar) {
                            upperlimit = window;
                        } else {
                            lowerlimit = window;
                        }

                        window = lowerlimit   + (upperlimit-lowerlimit)/2;
                        window = (window/2)*2 + 1;
                    }

                    double value = 0;
                    if (graysrc.depth == cxcore.IPL_DEPTH_8U) {
                        value = srcbuf.get(y*graysrc.widthStep       + x) & 0xFF;
                    } else if (graysrc.depth == cxcore.IPL_DEPTH_32F) {
                        value = srcbuf.getFloat(y*graysrc.widthStep  + 4*x);
                    } else if (graysrc.depth == cxcore.IPL_DEPTH_64F) {
                        value = srcbuf.getDouble(y*graysrc.widthStep + 8*x);
                    } else {
                        //cvIntegral() does not support other image types, so we
                        //should not be able to get here...
                        assert(false);
                    }
                    if (invert) {
                        //double threshold = 255 - (255 - mean) * (1 + 0.1*(Math.sqrt(var)/128 - 1));
                        double threshold = 255 - (255 - mean) * k;
                        dstbuf.put(y*dst.widthStep + x, (value < threshold ? (byte)0xFF : (byte)0x00));
                    } else {
                        //double threshold = mean * (1 + k*(Math.sqrt(var)/128 - 1));
                        double threshold = mean * k;
                        dstbuf.put(y*dst.widthStep + x, (value > threshold ? (byte)0xFF : (byte)0x00));
                    }
                }
            }
        }});
    }

    // clamps image intensities between min and max...
    public static void minMaxS(IplImage src, double min, double max, IplImage dst) {

        switch (src.depth) {
            case cxcore.IPL_DEPTH_8U: {
                ByteBuffer sb = src.getByteBuffer();
                ByteBuffer db = dst.getByteBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (byte)Math.max(Math.min(sb.get(i) & 0xFF,max),min));
                }
                break; }
            case cxcore.IPL_DEPTH_16U: {
                ShortBuffer sb = src.getByteBuffer().asShortBuffer();
                ShortBuffer db = dst.getByteBuffer().asShortBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (short)Math.max(Math.min(sb.get(i) & 0xFFFF,max),min));
                }
                break; }
            case cxcore.IPL_DEPTH_32F: {
                FloatBuffer sb = src.getByteBuffer().asFloatBuffer();
                FloatBuffer db = dst.getByteBuffer().asFloatBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (float)Math.max(Math.min(sb.get(i),max),min));
                }
                break; }
            case cxcore.IPL_DEPTH_8S: {
                ByteBuffer sb = src.getByteBuffer();
                ByteBuffer db = dst.getByteBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (byte)Math.max(Math.min(sb.get(i),max),min));
                }
                break; }
            case cxcore.IPL_DEPTH_16S: {
                ShortBuffer sb = src.getByteBuffer().asShortBuffer();
                ShortBuffer db = dst.getByteBuffer().asShortBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (short)Math.max(Math.min(sb.get(i),max),min));
                }
                break; }
            case cxcore.IPL_DEPTH_32S: {
                IntBuffer sb = src.getByteBuffer().asIntBuffer();
                IntBuffer db = dst.getByteBuffer().asIntBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (int)Math.max(Math.min(sb.get(i),max),min));
                }
                break; }
            case cxcore.IPL_DEPTH_64F: {
                DoubleBuffer sb = src.getByteBuffer().asDoubleBuffer();
                DoubleBuffer db = dst.getByteBuffer().asDoubleBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, Math.max(Math.min(sb.get(i),max),min));
                }
                break; }
            default: assert(false);
        }

    }

    // induced norm
    public static double norm(CvMat A, double p) {
        double norm = -1;

        if (p == 1.0) {
            for (int j = 0; j < A.cols; j++) {
                double n = 0;
                for (int i = 0; i < A.rows; i++) {
                    n += Math.abs(A.get(i, j));
                }
                norm = Math.max(n, norm);
            }
        } else if (p == 2.0) {
            CvMat W = CvMat.create(Math.min(A.rows, A.cols), 1);
            cxcore.cvSVD(A, W, null, null, 0);
            norm = W.get(0); // largest singular value
        } else if (p == Double.POSITIVE_INFINITY) {
            for (int i = 0; i < A.rows; i++) {
                double n = 0;
                for (int j = 0; j < A.cols; j++) {
                    n += Math.abs(A.get(i, j));
                }
                norm = Math.max(n, norm);
            }
        } else {
            assert(false);
        }
        return norm;
    }

    public static double cond(CvMat A, double p) {
        double cond = -1;

        if (p == 2.0) {
            CvMat W = CvMat.create(Math.min(A.rows, A.cols), 1);
            cxcore.cvSVD(A, W, null, null, 0);
            cond = W.get(0)/W.get(W.getLength()-1); // largest/smallest singular value
        } else {
            // should put something faster here if we're really serious
            // about using something other than the 2-norm
            CvMat Ainv = CvMat.create(A.rows, A.cols);
            cxcore.cvInvert(A, Ainv);
            return norm(A, p)*norm(Ainv, p);
        }
        return cond;
    }

}
