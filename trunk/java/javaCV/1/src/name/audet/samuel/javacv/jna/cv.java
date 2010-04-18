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
 *
 *
 * This file is based on information found in cvtypes.h and cv.h of 
 * OpenCV 1.1pre1, which are covered by the following copyright notice:
 *
 *                        Intel License Agreement
 *                For Open Source Computer Vision Library
 *
 * Copyright (C) 2000, Intel Corporation, all rights reserved.
 * Third party copyrights are property of their respective owners.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * Redistribution's of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   * Redistribution's in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   * The name of Intel Corporation may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * This software is provided by the copyright holders and contributors "as is" and
 * any express or implied warranties, including, but not limited to, the implied
 * warranties of merchantability and fitness for a particular purpose are disclaimed.
 * In no event shall the Intel Corporation or contributors be liable for any direct,
 * indirect, incidental, special, exemplary, or consequential damages
 * (including, but not limited to, procurement of substitute goods or services;
 * loss of use, data, or profits; or business interruption) however caused
 * and on any theory of liability, whether in contract, strict liability,
 * or tort (including negligence or otherwise) arising in any way out of
 * the use of this software, even if advised of the possibility of such damage.
 *
 */

package name.audet.samuel.javacv.jna;

import com.sun.jna.Callback;
import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import name.audet.samuel.javacv.jna.cxcore.CvArr;
import name.audet.samuel.javacv.jna.cxcore.CvBox2D;
import name.audet.samuel.javacv.jna.cxcore.CvGraph;
import name.audet.samuel.javacv.jna.cxcore.CvMat;
import name.audet.samuel.javacv.jna.cxcore.CvMatND;
import name.audet.samuel.javacv.jna.cxcore.CvMemStorage;
import name.audet.samuel.javacv.jna.cxcore.CvPoint;
import name.audet.samuel.javacv.jna.cxcore.CvPoint2D32f;
import name.audet.samuel.javacv.jna.cxcore.CvPoint2D64f;
import name.audet.samuel.javacv.jna.cxcore.CvPoint3D32f;
import name.audet.samuel.javacv.jna.cxcore.CvRect;
import name.audet.samuel.javacv.jna.cxcore.CvScalar;
import name.audet.samuel.javacv.jna.cxcore.CvSeq;
import name.audet.samuel.javacv.jna.cxcore.CvSeqBlock;
import name.audet.samuel.javacv.jna.cxcore.CvSeqReader;
import name.audet.samuel.javacv.jna.cxcore.CvSize;
import name.audet.samuel.javacv.jna.cxcore.CvSlice;
import name.audet.samuel.javacv.jna.cxcore.CvSparseMat;
import name.audet.samuel.javacv.jna.cxcore.CvTermCriteria;
import name.audet.samuel.javacv.jna.cxcore.IplImage;

/**
 *
 * @author Samuel Audet
 */
public class cv {
    // OpenCV sometimes does not install itself in the PATH :(
    public static final String[] paths = { "C:/Program Files/OpenCV/bin/",
                                           "C:/Program Files (x86)/OpenCV/bin/",
                                           "/usr/local/lib/",
                                           "/usr/local/lib64/" };
    public static final String[] libnames = { "cv110", "cv110_64", "cv" };
    public static final String libname;
    static {
        int i = 0;
        for (; i < libnames.length; i++) {
            for (int j = 0; j < paths.length; j++) {
                NativeLibrary.addSearchPath(libnames[i], paths[j]);
            }
            try {
                Native.register(libnames[i]);
                break;
            } catch (LinkageError e) {
                if (i >= libnames.length-1)
                    throw e;
            }
        }
        libname = libnames[i];
    }

    public static final int
            CV_SCHARR          = -1,
            CV_MAX_SOBEL_KSIZE = 7;
    public static native void cvSobel(CvArr src, CvArr dst, int xorder, int yorder, int aperture_size/*=3*/);
    public static native void cvLaplace(CvArr src, CvArr dst, int aperture_size/*=3*/);
    public static final int CV_CANNY_L2_GRADIENT = (1 << 31);
    public static native void cvCanny(CvArr image, CvArr edges, double threshold1, double threshold2,
            int aperture_size/*=3*/);
    public static native void cvPreCornerDetect(CvArr image, CvArr corners, int aperture_size/*=3*/);
    public static native void cvCornerEigenValsAndVecs(CvArr image, CvArr eigenvv,
            int block_size, int aperture_size /*=3*/);
    public static native void cvCornerMinEigenVal(CvArr image, CvArr eigenval,
            int block_size, int aperture_size /*=3*/);
    public static native void cvCornerHarris(CvArr image, CvArr harris_responce,
            int block_size, int aperture_size/*=3*/, double k /*=0.04*/);
    public static native void cvFindCornerSubPix(CvArr image, CvPoint2D32f corners,
            int count, CvSize.ByValue win, CvSize.ByValue zero_zone,
            CvTermCriteria.ByValue criteria);
    public static void cvFindCornerSubPix(CvArr image, CvPoint2D32f[] corners,
            int count, CvSize.ByValue win, CvSize.ByValue zero_zone,
            CvTermCriteria.ByValue criteria) {
        cvFindCornerSubPix(image, corners[0], count, win, zero_zone, criteria);
    }
    public static native void cvGoodFeaturesToTrack(CvArr image, CvArr eig_image,
            CvArr temp_image, CvPoint2D32f corners,
            IntByReference corner_count, double quality_level,
            double  min_distance, CvArr mask/*=null*/,
            int block_size/*=3*/, int use_harris/*=0*/, double k/*=0.04*/);
    public static void cvGoodFeaturesToTrack(CvArr image, CvArr eig_image,
            CvArr temp_image, CvPoint2D32f[] corners,
            IntByReference corner_count, double quality_level,
            double  min_distance, CvArr mask/*=null*/,
            int block_size/*=3*/, int use_harris/*=0*/, double k/*=0.04*/) {
        cvGoodFeaturesToTrack(image, eig_image, temp_image, corners[0],
                corner_count, quality_level, min_distance, mask, block_size, use_harris, k);
    }

    public static class CvSURFPoint extends Structure {
        public CvSURFPoint(CvPoint2D32f pt, int laplacian, int size) {
            this(pt, laplacian, size, 0, 0);
        }
        public CvSURFPoint(CvPoint2D32f pt, int laplacian, int size,
                float dir, float hessian) {
            this.pt = pt;
            this.laplacian = laplacian;
            this.size = size;
            this.dir = dir;
            this.hessian = hessian;
        }

        public CvPoint2D32f pt;
        public int laplacian;
        public int size;
        public float dir;
        public float hessian;
    }
    public static class CvSURFParams extends Structure {
        public int extended;
        public double hessianThreshold;
        public int nOctaves;
        public int nOctaveLayers;

        public static class ByValue extends CvSURFParams implements Structure.ByValue {
            public ByValue() { }
            public ByValue(CvSURFParams o) {
                this.extended = o.extended;
                this.hessianThreshold = o.hessianThreshold;
                this.nOctaves = o.nOctaves;
                this.nOctaveLayers = o.nOctaveLayers;
            }
        }
        public ByValue byValue() {
            return new ByValue(this);
        }
    }
    public static native CvSURFParams.ByValue cvSURFParams(double hessianThreshold, int extended/*=0*/);
    public static native void cvExtractSURF(CvArr img, CvArr mask, CvSeq.PointerByReference keypoints,
            CvSeq.PointerByReference descriptors, CvMemStorage storage, CvSURFParams.ByValue params);


    public static native int cvSampleLine(CvArr image, CvPoint.ByValue pt1, CvPoint.ByValue pt2,
            Pointer buffer, int connectivity/*=8*/);
    public static native void cvGetRectSubPix(CvArr src, CvArr dst, CvPoint2D32f.ByValue center);
    public static native void cvGetQuadrangleSubPix(CvArr src, CvArr dst, CvMat map_matrix);
    public static final int
            CV_INTER_NN      = 0,
            CV_INTER_LINEAR  = 1,
            CV_INTER_CUBIC   = 2,
            CV_INTER_AREA    = 3,

            CV_WARP_FILL_OUTLIERS = 8,
            CV_WARP_INVERSE_MAP   = 16;
    public static native void cvResize(CvArr src, CvArr dst, int interpolation/*=CV_INTER_LINEAR*/);
    public static native void cvWarpAffine(CvArr src, CvArr dst, CvMat map_matrix,
            int flags/*=CV_INTER_LINEAR+CV_WARP_FILL_OUTLIERS*/,
            CvScalar.ByValue fillval/*=cvScalarAll(0)*/);
    public static native CvMat cvGetAffineTransform(CvPoint2D32f src, CvPoint2D32f dst,
            CvMat map_matrix);
    public static CvMat cvGetAffineTransform(CvPoint2D32f[] src, CvPoint2D32f[] dst,
            CvMat map_matrix) {
        return cvGetAffineTransform(src[0], dst[0], map_matrix);
    }
    public static native CvMat cv2DRotationMatrix(CvPoint2D32f.ByValue center, double angle,
            double scale, CvMat map_matrix);
    public static native void cvWarpPerspective(CvArr src, CvArr dst, CvMat map_matrix,
            int flags/*=CV_INTER_LINEAR+CV_WARP_FILL_OUTLIERS*/,
            CvScalar.ByValue fillval/*=cvScalarAll(0)*/);
    public static native CvMat cvGetPerspectiveTransform(CvPoint2D32f src, CvPoint2D32f dst,
            CvMat map_matrix);
    public static CvMat cvGetPerspectiveTransform(CvPoint2D32f[] src, CvPoint2D32f[] dst,
            CvMat map_matrix) {
        return cvGetPerspectiveTransform(src[0], dst[0], map_matrix);
    }
    public static native void cvRemap(CvArr src, CvArr dst, CvArr mapx, CvArr mapy,
            int flags/*=CV_INTER_LINEAR+CV_WARP_FILL_OUTLIERS*/,
            CvScalar.ByValue fillval/*=cvScalarAll(0)*/);
    public static native void cvLogPolar(CvArr src, CvArr dst, CvPoint2D32f.ByValue center,
            double M, int flags/*=CV_INTER_LINEAR+CV_WARP_FILL_OUTLIERS*/);


    public static class IplConvKernel extends Structure {
        public IplConvKernel() { cvcreated = false; }
        public IplConvKernel(Pointer m) { useMemory(m); read(); cvcreated = true; }

        public static IplConvKernel create(int cols, int rows,
                int anchor_x, int anchor_y, int shape, int[] values/*=null*/) {
            IplConvKernel k = cv.cvCreateStructuringElementEx(cols, rows,
                    anchor_x, anchor_y, shape, values);
            if (k != null) {
                k.cvcreated = true;
            }
            return k;
        }
        public void release() {
            cvcreated = false;
            cv.cvReleaseStructuringElement(pointerByReference());
        }
        @Override protected void finalize() {
            if (cvcreated) {
                release();
            }
        }
        private boolean cvcreated = false;

        public int  nCols;
        public int  nRows;
        public int  anchorX;
        public int  anchorY;
        public IntByReference values;
        public int  nShiftR;

        public static class ByReference extends IplConvKernel implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(IplConvKernel p) {
                setStructure(p);
            }
            public IplConvKernel getStructure() {
                return new IplConvKernel(getValue());
            }
            public void getStructure(IplConvKernel p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(IplConvKernel p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }
    public static final int
            CV_SHAPE_RECT     = 0,
            CV_SHAPE_CROSS    = 1,
            CV_SHAPE_ELLIPSE  = 2,
            CV_SHAPE_CUSTOM   = 100;
    public static native IplConvKernel cvCreateStructuringElementEx(int cols, int rows,
            int anchor_x, int anchor_y, int shape, int[] values/*=null*/);
    public static native void cvReleaseStructuringElement(
            IplConvKernel.PointerByReference element);
    public static native void cvErode(CvArr src, CvArr dst,
            IplConvKernel element/*=null*/, int iterations/*=1*/);
    public static native void cvDilate(CvArr src, CvArr dst,
            IplConvKernel element/*=null*/, int iterations/*=1*/);
    public static final int
            CV_MOP_OPEN        = 2,
            CV_MOP_CLOSE       = 3,
            CV_MOP_GRADIENT    = 4,
            CV_MOP_TOPHAT      = 5,
            CV_MOP_BLACKHAT    = 6;
    public static native void cvMorphologyEx(CvArr src, CvArr dst, CvArr temp,
            IplConvKernel element, int operation, int iterations/*=1*/);


    public static final int
            CV_BLUR_NO_SCALE = 0,
            CV_BLUR = 1,
            CV_GAUSSIAN = 2,
            CV_MEDIAN = 3,
            CV_BILATERAL = 4;
    public static native void cvSmooth(CvArr src, CvArr dst, int smoothtype/*=CV_GAUSSIAN*/,
            int size1/*=3*/, int size2/*=0*/, double sigma1/*=0*/, double sigma2/*=0*/);
    public static native void cvFilter2D(CvArr src, CvArr dst,
            CvMat kernel, CvPoint.ByValue anchor/*=cvPoint(-1,-1)*/);
    public static final int
            IPL_BORDER_CONSTANT  = 0,
            IPL_BORDER_REPLICATE = 1,
            IPL_BORDER_REFLECT   = 2,
            IPL_BORDER_WRAP      = 3;
    public static native void cvCopyMakeBorder(CvArr src, CvArr dst, CvPoint.ByValue offset,
            int bordertype, CvScalar.ByValue value/*=cvScalarAll(0)*/);
    public static native void cvIntegral(CvArr image, CvArr sum,
            CvArr sqsum/*=null*/, CvArr tilted_sum/*=null*/);

    public static final int
        CV_BGR2BGRA  = 0,
        CV_RGB2RGBA  = CV_BGR2BGRA,

        CV_BGRA2BGR  = 1,
        CV_RGBA2RGB  = CV_BGRA2BGR,

        CV_BGR2RGBA  = 2,
        CV_RGB2BGRA  = CV_BGR2RGBA,

        CV_RGBA2BGR  = 3,
        CV_BGRA2RGB  = CV_RGBA2BGR,

        CV_BGR2RGB   = 4,
        CV_RGB2BGR   = CV_BGR2RGB,

        CV_BGRA2RGBA = 5,
        CV_RGBA2BGRA = CV_BGRA2RGBA,

        CV_BGR2GRAY  = 6,
        CV_RGB2GRAY  = 7,
        CV_GRAY2BGR  = 8,
        CV_GRAY2RGB  = CV_GRAY2BGR,
        CV_GRAY2BGRA = 9,
        CV_GRAY2RGBA = CV_GRAY2BGRA,
        CV_BGRA2GRAY = 10,
        CV_RGBA2GRAY = 11,

        CV_BGR2BGR565 =12,
        CV_RGB2BGR565 =13,
        CV_BGR5652BGR =14,
        CV_BGR5652RGB =15,
        CV_BGRA2BGR565=16,
        CV_RGBA2BGR565=17,
        CV_BGR5652BGRA=18,
        CV_BGR5652RGBA=19,

        CV_GRAY2BGR565=20,
        CV_BGR5652GRAY=21,

        CV_BGR2BGR555 =22,
        CV_RGB2BGR555 =23,
        CV_BGR5552BGR =24,
        CV_BGR5552RGB =25,
        CV_BGRA2BGR555=26,
        CV_RGBA2BGR555=27,
        CV_BGR5552BGRA=28,
        CV_BGR5552RGBA=29,

        CV_GRAY2BGR555=30,
        CV_BGR5552GRAY=31,

        CV_BGR2XYZ   = 32,
        CV_RGB2XYZ   = 33,
        CV_XYZ2BGR   = 34,
        CV_XYZ2RGB   = 35,

        CV_BGR2YCrCb = 36,
        CV_RGB2YCrCb = 37,
        CV_YCrCb2BGR = 38,
        CV_YCrCb2RGB = 39,

        CV_BGR2HSV   = 40,
        CV_RGB2HSV   = 41,

        CV_BGR2Lab   = 44,
        CV_RGB2Lab   = 45,

        CV_BayerBG2BGR=46,
        CV_BayerGB2BGR=47,
        CV_BayerRG2BGR=48,
        CV_BayerGR2BGR=49,

        CV_BayerBG2RGB=CV_BayerRG2BGR,
        CV_BayerGB2RGB=CV_BayerGR2BGR,
        CV_BayerRG2RGB=CV_BayerBG2BGR,
        CV_BayerGR2RGB=CV_BayerGB2BGR,

        CV_BGR2Luv   = 50,
        CV_RGB2Luv   = 51,
        CV_BGR2HLS   = 52,
        CV_RGB2HLS   = 53,

        CV_HSV2BGR   = 54,
        CV_HSV2RGB   = 55,

        CV_Lab2BGR   = 56,
        CV_Lab2RGB   = 57,
        CV_Luv2BGR   = 58,
        CV_Luv2RGB   = 59,
        CV_HLS2BGR   = 60,
        CV_HLS2RGB   = 61,

        CV_COLORCVT_MAX = 100;
    public static native void cvCvtColor(CvArr src, CvArr dst, int code);

    public static final int
            CV_THRESH_BINARY     = 0,
            CV_THRESH_BINARY_INV = 1,
            CV_THRESH_TRUNC      = 2,
            CV_THRESH_TOZERO     = 3,
            CV_THRESH_TOZERO_INV = 4,
            CV_THRESH_MASK       = 7,

            CV_THRESH_OTSU       = 8;
    public static native void cvThreshold(CvArr src, CvArr dst, double threshold,
            double max_value, int threshold_type);

    public static final int
            CV_ADAPTIVE_THRESH_MEAN_C     = 0,
            CV_ADAPTIVE_THRESH_GAUSSIAN_C = 1;
    public static native void cvAdaptiveThreshold(CvArr src, CvArr dst, double max_value,
            int adaptive_method/*=CV_ADAPTIVE_THRESH_MEAN_C*/,
            int threshold_type/*=CV_THRESH_BINARY*/,
            int block_size/*=3*/, double param1/*=5*/);


    public static final int CV_GAUSSIAN_5x5 = 7;
    public static native void cvPyrDown(CvArr src, CvArr dst, int filter/*=CV_GAUSSIAN_5x5*/);
    public static native void cvPyrUp(CvArr src, CvArr dst, int filter/*=CV_GAUSSIAN_5x5*/);
    public static native CvMat.PointerByReference cvCreatePyramid(CvArr img, int extra_layers,
            double rate, CvSize layer_sizes, CvArr bufarr, int calc, int filter);
    public static native void cvReleasePyramid(Pointer /* CvMat*** */ pyramid, int extra_layers);


    public static class CvConnectedComp extends Structure {
        public double area;
        public CvScalar value;
        public CvRect rect;
        public CvSeq.ByReference contour;
    }

    public static final int
            CV_FLOODFILL_FIXED_RANGE = (1 << 16),
            CV_FLOODFILL_MASK_ONLY   = (1 << 17);
    public static native void cvFloodFill(CvArr image, CvPoint.ByValue seed_point, CvScalar.ByValue new_val,
            CvScalar.ByValue lo_diff/*=cvScalarAll(0)*/, CvScalar.ByValue up_diff/*=cvScalarAll(0)*/,
            CvConnectedComp comp/*=null*/, int flags/*=4*/, CvArr mask/*=null*/);

    public static class CvContourScanner extends PointerType {
        public CvContourScanner() { }
        public CvContourScanner(Pointer p) { super(p); }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvContourScanner p) {
                setStructure(p);
            }
            public CvContourScanner getStructure() {
                return new CvContourScanner(getValue());
            }
            public void getStructure(CvContourScanner p) {
                p.setPointer(getValue());
            }
            public void setStructure(CvContourScanner p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static final int
            CV_RETR_EXTERNAL = 0,
            CV_RETR_LIST     = 1,
            CV_RETR_CCOMP    = 2,
            CV_RETR_TREE     = 3,

            CV_CHAIN_CODE              = 0,
            CV_CHAIN_APPROX_NONE       = 1,
            CV_CHAIN_APPROX_SIMPLE     = 2,
            CV_CHAIN_APPROX_TC89_L1    = 3,
            CV_CHAIN_APPROX_TC89_KCOS  = 4,
            CV_LINK_RUNS               = 5;

    public static class CvChainPtReader extends CvSeqReader {
        public char      code;
        public CvPoint   pt;
        //public byte[][]    deltas = new byte[8][2];
        public byte[]    deltas = new byte[8*2];
    }

    public static class CvContourTree extends CvSeq {
        public CvPoint p1;
        public CvPoint p2;
    }

    public static class CvChain extends CvSeq {
        public CvPoint origin;
    }

    public static class CvContour extends CvSeq {
        public CvRect rect;
        public int color;
        public int reserved[] = new int[3];

        public static class ByValue extends CvContour implements Structure.ByValue { }
    }

    public static class CvPoint2DSeq extends CvContour { }

    public static native int cvFindContours(CvArr image, CvMemStorage storage, CvSeq.PointerByReference first_contour,
            int header_size/*=sizeof(CvContour)*/, int mode/*=CV_RETR_LIST*/,
            int method/*=CV_CHAIN_APPROX_SIMPLE*/, CvPoint.ByValue offset/*=cvPoint(0,0)*/);
    public static native CvContourScanner cvStartFindContours(CvArr image, CvMemStorage storage,
            int header_size/*=sizeof(CvContour)*/, int mode/*=CV_RETR_LIST*/,
            int method/*=CV_CHAIN_APPROX_SIMPLE*/, CvPoint.ByValue offset/*=cvPoint(0,0)*/);
    public static native CvSeq cvFindNextContour(CvContourScanner scanner);
    public static native void cvSubstituteContour(CvContourScanner scanner, CvSeq new_contour);
    public static native CvSeq cvEndFindContours(CvContourScanner.PointerByReference scanner);

    public static native void cvPyrSegmentation(IplImage src, IplImage dst, CvMemStorage storage,
            CvSeq.PointerByReference comp, int level, double threshold1, double threshold2);
    public static native void cvPyrMeanShiftFiltering(CvArr src, CvArr dst, double sp, double sr,
            int max_level/*=1*/,  CvTermCriteria termcrit
            /*=cvTermCriteria(CV_TERMCRIT_ITER+CV_TERMCRIT_EPS,5,1)*/);
    public static native void cvWatershed(CvArr image, CvArr markers);


    public static class CvMoments extends Structure {
        public double m00, m10, m01, m20, m11, m02, m30, m21, m12, m03; 
        public double mu20, mu11, mu02, mu30, mu21, mu12, mu03; 
        public double inv_sqrt_m00; 
    }

    public static class CvHuMoments extends Structure {
        public double hu1, hu2, hu3, hu4, hu5, hu6, hu7;
    }

    public static native void cvMoments(CvArr arr, CvMoments moments, int binary/*=0*/);
    public static native double cvGetSpatialMoment(CvMoments moments, int x_order, int y_order);
    public static native double cvGetCentralMoment(CvMoments moments, int x_order, int y_order);
    public static native double cvGetNormalizedCentralMoment(CvMoments moments, int x_order, int y_order);
    public static native void cvGetHuMoments(CvMoments moments, CvHuMoments hu_moments);


    public static final int
            CV_HOUGH_STANDARD = 0,
            CV_HOUGH_PROBABILISTIC = 1,
            CV_HOUGH_MULTI_SCALE = 2,
            CV_HOUGH_GRADIENT = 3;
    public static native CvSeq cvHoughLines2(CvArr image, Pointer line_storage, int method,
            double rho, double theta, int threshold,
            double param1/*=0*/, double param2/*=0*/);
    public static native CvSeq cvHoughCircles(CvArr image, Pointer circle_storage, int method,
            double dp, double min_dist, double param1/*=100*/,
            double param2/*=100*/, int min_radius/*=0*/, int max_radius/*=0*/);

    public static final int
            CV_DIST_USER   = -1,
            CV_DIST_L1     = 1,
            CV_DIST_L2     = 2,
            CV_DIST_C      = 3,
            CV_DIST_L12    = 4,
            CV_DIST_FAIR   = 5,
            CV_DIST_WELSCH = 6,
            CV_DIST_HUBER  = 7,

            CV_DIST_MASK_3 =  3,
            CV_DIST_MASK_5 =  5,
            CV_DIST_MASK_PRECISE = 0;
    public static native void cvDistTransform(CvArr src, CvArr dst, int distance_type/*=CV_DIST_L2*/,
            int mask_size/*=3*/, FloatByReference mask/*=null*/, CvArr labels/*=null*/);

    public static final int
            CV_INPAINT_NS      = 0,
            CV_INPAINT_TELEA   = 1;
    public static native void cvInpaint(CvArr src, CvArr mask, CvArr dst, double inpaintRange, int flags);


    public static final int
        CV_HIST_MAGIC_VAL     = 0x42450000,
        CV_HIST_UNIFORM_FLAG  = (1 << 10),

        CV_HIST_RANGES_FLAG   = (1 << 11),

        CV_HIST_ARRAY         = 0,
        CV_HIST_SPARSE        = 1,
        CV_HIST_TREE          = CV_HIST_SPARSE,

        CV_HIST_UNIFORM       = 1;

    public static class CvHistogram extends Structure {
        public CvHistogram() { cvcreated = false; }
        public CvHistogram(Pointer m) { useMemory(m); read(); cvcreated = true; }

        public static CvHistogram create(int dims, int[] sizes, int type,
                FloatByReference[] ranges/*=null*/, int uniform/*=1*/) {
            CvHistogram h = cv.cvCreateHist(dims, sizes, type, ranges, uniform);
            if (h != null) {
                h.cvcreated = true;
            }
            return h;
        }
        public void release() {
            cvcreated = false;
            cv.cvReleaseHist(pointerByReference());
        }
        @Override protected void finalize() {
            if (cvcreated) {
                release();
            }
        }
        private boolean cvcreated = false;

        public int /* CvHistType */             type;
        public CvArr.ByReference                bins;
        public float[][]                        thresh = new float[cxcore.CV_MAX_DIM][2];
        public FloatByReference /* float** */ thresh2;
        public CvMatND mat;

        public boolean CV_IS_HIST() {
            return (type & cxcore.CV_MAGIC_MASK) == CV_HIST_MAGIC_VAL &&
                    bins != null;
        }

        public boolean CV_IS_UNIFORM_HIST() {
            return (type & CV_HIST_UNIFORM_FLAG) != 0;
        }

        public boolean CV_IS_SPARSE_HIST(CvHistogram hist) {
            return new CvSparseMat(bins.getPointer()).CV_IS_SPARSE_MAT();
        }

        public boolean CV_HIST_HAS_RANGES() {
            return (type & CV_HIST_RANGES_FLAG) != 0;
        }

        public float cvQueryHistValue_1D(int idx0) {
            return (float)cxcore.cvGetReal1D(bins, idx0);
        }
        public float cvQueryHistValue_2D(int idx0, int idx1) {
            return (float)cxcore.cvGetReal2D(bins, idx0, idx1);
        }
        public float cvQueryHistValue_3D(int idx0, int idx1, int idx2) {
            return (float)cxcore.cvGetReal3D(bins, idx0, idx1, idx2);
        }
        public float cvQueryHistValue_nD(int idx0, int[] idx) {
            return (float)cxcore.cvGetRealND(bins, idx);
        }

        public Pointer cvGetHistValue_1D(int idx0) {
            return cxcore.cvPtr1D(bins, idx0, null);
        }
        public Pointer cvGetHistValue_2D(int idx0, int idx1) {
            return cxcore.cvPtr2D(bins, idx0, idx1, null);
        }
        public Pointer cvGetHistValue_3D(int idx0, int idx1, int idx2) {
            return cxcore.cvPtr3D(bins, idx0, idx1, idx2, null);
        }
        public Pointer cvGetHistValue_nD(int idx0, int[] idx) {
            return cxcore.cvPtrND(bins, idx, null, 1, null);
        }

        public static class ByReference extends CvHistogram implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvHistogram p) {
                setStructure(p);
            }
            public CvHistogram getStructure() {
                return new CvHistogram(getValue());
            }
            public void getStructure(CvHistogram p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvHistogram p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static native CvHistogram cvCreateHist(int dims, int[] sizes, int type,
            FloatByReference ranges/*=null*/, int uniform/*=1*/);
    public static CvHistogram cvCreateHist(int dims, int[] sizes, int type,
            FloatByReference[] ranges/*=null*/, int uniform/*=1*/) {
        return cvCreateHist(dims, sizes, type, ranges[0], uniform);
    }
    public static native void cvSetHistBinRanges(CvHistogram hist,
            FloatByReference ranges, int uniform/*=1*/);
    public static void cvSetHistBinRanges(CvHistogram hist,
            FloatByReference[] ranges, int uniform/*=1*/) {
        cvSetHistBinRanges(hist, ranges[0], uniform);
    }
    public static native void cvReleaseHist(CvHistogram.PointerByReference hist);
    public static native void cvClearHist(CvHistogram hist);
    public static native CvHistogram cvMakeHistHeaderForArray(int dims, int[] sizes, CvHistogram hist,
            float[] data, FloatByReference ranges/*=null*/, int uniform/*=1*/);
    public static CvHistogram cvMakeHistHeaderForArray(int dims, int[] sizes, CvHistogram hist,
            float[] data, FloatByReference[] ranges/*=null*/, int uniform/*=1*/) {
        return cvMakeHistHeaderForArray(dims, sizes, hist, data, ranges[0], uniform);
    }
    public static native void cvGetMinMaxHistValue(CvHistogram hist,
            FloatByReference min_value, FloatByReference max_value,
            IntByReference min_idx/*=null*/, IntByReference max_idx/*=null*/);
    public static native void cvNormalizeHist(CvHistogram hist, double factor);
    public static native void cvThreshHist(CvHistogram hist, double threshold);
    public static final int
            CV_COMP_CORREL        = 0,
            CV_COMP_CHISQR        = 1,
            CV_COMP_INTERSECT     = 2,
            CV_COMP_BHATTACHARYYA = 3;
    public static native double cvCompareHist(CvHistogram hist1, CvHistogram hist2, int method);
    public static native void cvCopyHist(CvHistogram src, CvHistogram.PointerByReference dst);
    public static native void cvCalcBayesianProb(CvHistogram src, int number, CvHistogram dst);
    public static void cvCalcBayesianProb(CvHistogram[] src, int number, CvHistogram[] dst) {
        cvCalcBayesianProb(src[0], number, dst[0]);
    }
    public static native void cvCalcArrHist(CvArr.PointerByReference arr, CvHistogram hist,
            int accumulate/*=0*/, CvArr mask/*=null*/);
    public static void cvCalcArrHist(CvArr.PointerByReference[] arr, CvHistogram hist,
            int accumulate/*=0*/, CvArr mask/*=null*/) {
        cvCalcArrHist(arr[0], hist, accumulate, mask);
    }
    public static void cvCalcHist(CvArr.PointerByReference[] arr, CvHistogram hist,
            int accumulate/*=0*/, CvArr mask/*=null*/) {
        cvCalcArrHist(arr[0], hist, accumulate, mask);
    }
    public static void cvCalcHist(CvArr.PointerByReference[] arr, CvHistogram hist) {
        cvCalcArrHist(arr[0], hist, 0, null);
    }
    public static native void cvCalcArrBackProject(CvArr image, CvArr dst, CvHistogram hist);
    public static void cvCalcArrBackProject(CvArr[] image, CvArr dst, CvHistogram hist) {
        cvCalcArrBackProject(image[0], dst, hist);
    }
    public static void cvCalcBackProject(CvArr[] image, CvArr dst, CvHistogram hist) {
        cvCalcArrBackProject(image[0], dst, hist);
    }
    public static native void cvCalcArrBackProjectPatch(CvArr image, CvArr dst, CvSize.ByValue range,
            CvHistogram hist, int method, double factor);
    public static void cvCalcArrBackProjectPatch(CvArr[] image, CvArr dst, CvSize.ByValue range,
            CvHistogram hist, int method, double factor) {
        cvCalcArrBackProjectPatch(image[0], dst, range, hist, method, factor);
    }
    public static void cvCalcBackProjectPatch(CvArr[] image, CvArr dst, CvSize.ByValue range,
            CvHistogram hist, int method, double factor) {
        cvCalcArrBackProjectPatch(image[0], dst, range, hist, method, factor);
    }
    public static native void cvCalcProbDensity(CvHistogram hist1, CvHistogram hist2,
            CvHistogram dst_hist, double scale/*=255*/);
    public static native void cvEqualizeHist(CvArr src, CvArr dst);


    public static final int
            CV_TM_SQDIFF        = 0,
            CV_TM_SQDIFF_NORMED = 1,
            CV_TM_CCORR         = 2,
            CV_TM_CCORR_NORMED  = 3,
            CV_TM_CCOEFF        = 4,
            CV_TM_CCOEFF_NORMED = 5;
    public static native void cvMatchTemplate(CvArr image, CvArr templ, CvArr result, int method);

    public static final int
            CV_CONTOURS_MATCH_I1 = 1,
            CV_CONTOURS_MATCH_I2 = 2,
            CV_CONTOURS_MATCH_I3 = 3;
    public static native double cvMatchShapes(Pointer object1, Pointer object2, int method, double parameter/*=0*/);

    public static interface CvDistanceFunction extends Callback {
        float callback(FloatByReference a, FloatByReference b, Pointer user_param);
    }
    public static native float cvCalcEMD2(CvArr signature1, CvArr signature2, int distance_type,
            CvDistanceFunction distance_func/*=null*/, CvArr cost_matrix/*=null*/,
            CvArr flow/*=null*/, FloatByReference lower_bound/*=null*/, Pointer userdata/*=null*/);
    public static native float cvCalcEMD2(CvArr signature1, CvArr signature2, int distance_type,
            Function distance_func/*=null*/, CvArr cost_matrix/*=null*/,
            CvArr flow/*=null*/, FloatByReference lower_bound/*=null*/, Pointer userdata/*=null*/);


    public static native CvSeq cvApproxChains(CvSeq src_seq, CvMemStorage storage, int method/*=CV_CHAIN_APPROX_SIMPLE*/,
            double parameter/*=0*/, int minimal_perimeter/*=0*/, int recursive/*=0*/);
    public static native void cvStartReadChainPoints(CvChain chain, CvChainPtReader reader);
    public static native CvPoint.ByValue cvReadChainPoint(CvChainPtReader reader);
    public static final int CV_POLY_APPROX_DP = 0;
    public static native CvSeq cvApproxPoly(Pointer src_seq, int header_size, CvMemStorage storage,
            int method/*=CV_POLY_APPROX_DP*/, double parameter, int parameter2/*=0*/);
    public static final int CV_DOMINANT_IPAN = 1;
    public static native CvSeq cvFindDominantPoints(CvSeq contour, CvMemStorage storage,
            int method/*=CV_DOMINANT_IPAN*/, double parameter1/*=0*/,
            double parameter2/*=0*/, double parameter3/*=0*/, double parameter4/*=0*/);
    public static native CvRect.ByValue cvBoundingRect(CvArr points, int update/*=0*/);
    public static native double cvContourArea(CvArr contour, CvSlice.ByValue slice/*=CV_WHOLE_SEQ*/);
    public static native double cvArcLength(Pointer curve, CvSlice.ByValue slice/*=CV_WHOLE_SEQ*/,
            int is_closed/*=-1*/);
    public static double cvContourPerimeter(Pointer contour) {
        return cvArcLength(contour, cxcore.CV_WHOLE_SEQ, 1);
    }
    public static native CvContourTree cvCreateContourTree(CvSeq contour, CvMemStorage storage, double threshold);
    public static native CvSeq cvContourFromContourTree(CvContourTree tree,
            CvMemStorage storage, CvTermCriteria.ByValue criteria);
    public static final int CV_CONTOUR_TREES_MATCH_I1 = 1;
    public static native double cvMatchContourTrees(CvContourTree tree1, CvContourTree tree2,
            int method/*=CV_CONTOUR_TREES_MATCH_I1*/, double threshold);


    public static native CvRect.ByValue cvMaxRect(CvRect rect1, CvRect rect2);
    public static native CvSeq cvPointSeqFromMat(int seq_kind, CvArr mat, CvContour contour_header, CvSeqBlock block);
    public static native void cvBoxPoints(CvBox2D.ByValue box, CvPoint2D32f pt/*[4]*/);
    public static void cvBoxPoints(CvBox2D.ByValue box, CvPoint2D32f[] pt/*[4]*/) {
        cvBoxPoints(box, pt[0]);
    }
    public static native CvBox2D.ByValue cvFitEllipse2(CvArr points);
    public static native void cvFitLine(CvArr points, int dist_type, double param,
            double reps, double aeps, float[] line);
    public static final int
            CV_CLOCKWISE         = 1,
            CV_COUNTER_CLOCKWISE = 2;
    public static native CvSeq cvConvexHull2(CvArr input, Pointer hull_storage/*=null*/,
            int orientation/*=CV_CLOCKWISE*/, int return_points/*=0*/);
    public static native int cvCheckContourConvexity(CvArr contour);
    public static class CvConvexityDefect extends Structure {
        public CvPoint.ByReference start;
        public CvPoint.ByReference end;
        public CvPoint.ByReference depth_point;
        public float depth;
    }
    public static native CvSeq cvConvexityDefects(CvArr contour, CvArr convexhull, CvMemStorage storage/*=null*/);
    public static native double cvPointPolygonTest(CvArr contour, CvPoint2D32f.ByValue pt, int measure_dist);
    public static native CvBox2D.ByValue cvMinAreaRect2(CvArr points, CvMemStorage storage/*=null*/);
    public static native int cvMinEnclosingCircle(CvArr points, CvPoint2D32f center, FloatByReference radius);
    public static native void cvCalcPGH(CvSeq contour, CvHistogram hist);


    //typedef size_t CvSubdiv2DEdge;
    public static class CvSubdiv2DEdge extends NativeLong {
        public CvSubdiv2DEdge() { super(); }
        public CvSubdiv2DEdge(long value) {
            super(value);
        }
    }

    public static final int CV_SUBDIV2D_VIRTUAL_POINT_FLAG = (1 << 30);

    public static class CvQuadEdge2D extends Structure {
        public int flags;
        public CvSubdiv2DPoint.ByReference[] pt = new CvSubdiv2DPoint.ByReference[4];
        public CvSubdiv2DEdge[] next = new CvSubdiv2DEdge[4];

        public CvSubdiv2DEdge CV_SUBDIV2D_NEXT_EDGE(CvSubdiv2DEdge edge) {
            int i = edge.intValue();
            return next[i&3];
        }
        public CvSubdiv2DEdge cvSubdiv2DNextEdge(CvSubdiv2DEdge edge) {
            return CV_SUBDIV2D_NEXT_EDGE(edge);
        }
        public CvSubdiv2DEdge cvSubdiv2DGetEdge(CvSubdiv2DEdge edge, int /* CvNextEdgeType */ type) {
            int i = next[(edge.intValue() + type) & 3].intValue();
            return new CvSubdiv2DEdge((i & ~3) + ((i + (type >> 4)) & 3));
        }
        public static CvSubdiv2DEdge  cvSubdiv2DRotateEdge(CvSubdiv2DEdge edge, int rotate) {
            int i = edge.intValue();
            return new CvSubdiv2DEdge((i & ~3) + ((i + rotate) & 3));
        }
        public CvSubdiv2DPoint.ByReference cvSubdiv2DEdgeOrg(CvSubdiv2DEdge edge) {
            int i = edge.intValue();
            return pt[i & 3];
        }
        public CvSubdiv2DPoint.ByReference cvSubdiv2DEdgeDst(CvSubdiv2DEdge edge) {
            int i = edge.intValue();
            return pt[(i + 2) & 3];
        }
        public static CvSubdiv2DEdge cvSubdiv2DSymEdge(CvSubdiv2DEdge edge) {
            int i = edge.intValue();
            return new CvSubdiv2DEdge(i ^ 2);
        }
    }

    public static class CvSubdiv2DPoint extends Structure {
        public CvSubdiv2DPoint() { }
        public CvSubdiv2DPoint(Pointer m) { useMemory(m); read(); }

        public int            flags;
        public CvSubdiv2DEdge first;
        public CvPoint2D32f   pt;

        public static class ByReference extends CvSubdiv2DPoint implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvSubdiv2DPoint p) {
                setStructure(p);
            }
            public CvSubdiv2DPoint getStructure() {
                return new CvSubdiv2DPoint(getValue());
            }
            public void getStructure(CvSubdiv2DPoint p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvSubdiv2DPoint p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class CvSubdiv2D extends CvGraph {
        public int  quad_edges;
        public int  is_geometry_valid;
        public CvSubdiv2DEdge recent_edge;
        public CvPoint2D32f  topleft;
        public CvPoint2D32f  bottomright;

        public static class ByReference extends CvSubdiv2D implements Structure.ByReference { }
    }

    //enum CvSubdiv2DPointLocation
    final int
        CV_PTLOC_ERROR = -2,
        CV_PTLOC_OUTSIDE_RECT = -1,
        CV_PTLOC_INSIDE = 0,
        CV_PTLOC_VERTEX = 1,
        CV_PTLOC_ON_EDGE = 2;

    //enum CvNextEdgeType
    final int
        CV_NEXT_AROUND_ORG   = 0x00,
        CV_NEXT_AROUND_DST   = 0x22,
        CV_PREV_AROUND_ORG   = 0x11,
        CV_PREV_AROUND_DST   = 0x33,
        CV_NEXT_AROUND_LEFT  = 0x13,
        CV_NEXT_AROUND_RIGHT = 0x31,
        CV_PREV_AROUND_LEFT  = 0x20,
        CV_PREV_AROUND_RIGHT = 0x02;

    public static double cvTriangleArea(CvPoint2D32f a, CvPoint2D32f b, CvPoint2D32f c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    public static CvSubdiv2D cvCreateSubdivDelaunay2D(CvRect.ByValue rect, CvMemStorage storage)  {
        CvSubdiv2D subdiv = cv.cvCreateSubdiv2D(cxcore.CV_SEQ_KIND_SUBDIV2D, new CvSubdiv2D().size(),
                             new CvSubdiv2DPoint().size(), new CvQuadEdge2D().size(), storage);
        cvInitSubdivDelaunay2D(subdiv, rect);
        return subdiv;
    }
    public static native void cvInitSubdivDelaunay2D(CvSubdiv2D subdiv, CvRect.ByValue rect);
    public static native CvSubdiv2D cvCreateSubdiv2D(int subdiv_type, int header_size,
            int vtx_size, int quadedge_size, CvMemStorage storage);
    public static native CvSubdiv2DPoint cvSubdivDelaunay2DInsert(CvSubdiv2D subdiv, CvPoint2D32f.ByValue pt);
    public static native int /* CvSubdiv2DPointLocation */ cvSubdiv2DLocate(CvSubdiv2D subdiv, 
            CvPoint2D32f.ByValue pt, CvSubdiv2DEdge edge, CvSubdiv2DPoint.PointerByReference vertex/*=null*/);
    public static native CvSubdiv2DPoint cvFindNearestPoint2D(CvSubdiv2D subdiv, CvPoint2D32f.ByValue pt);
    public static native void cvCalcSubdivVoronoi2D(CvSubdiv2D subdiv);
    public static native void cvClearSubdivVoronoi2D(CvSubdiv2D subdiv);


    public static native void cvAcc(CvArr image, CvArr sum, CvArr mask/*=null*/);
    public static native void cvSquareAcc(CvArr image, CvArr sqsum, CvArr mask/*=null*/);
    public static native void cvMultiplyAcc(CvArr image1, CvArr image2, CvArr acc, CvArr mask/*=null*/);
    public static native void cvRunningAvg(CvArr image, CvArr acc, double alpha, CvArr mask/*=null*/);


    public static native void cvUpdateMotionHistory(CvArr silhouette, CvArr mhi, double timestamp, double duration);
    public static native void cvCalcMotionGradient(CvArr mhi, CvArr mask, CvArr orientation,
            double delta1, double delta2, int aperture_size/*=3*/);
    public static native double cvCalcGlobalOrientation(CvArr orientation, CvArr mask, CvArr mhi,
            double timestamp, double duration);
    public static native CvSeq cvSegmentMotion(CvArr mhi, CvArr seg_mask, CvMemStorage storage,
            double timestamp, double seg_thresh);


    public static native int cvMeanShift(CvArr prob_image, CvRect.ByValue window,
            CvTermCriteria.ByValue criteria, CvConnectedComp comp);
    public static native int cvCamShift(CvArr prob_image, CvRect.ByValue window,
            CvTermCriteria.ByValue criteria, CvConnectedComp comp, CvBox2D box/*=null*/);
    public static final int
            CV_VALUE = 1,
            CV_ARRAY = 2;
    public static native void cvSnakeImage(IplImage image, CvPoint points, int length, float[] alpha,
            float[] beta, float[] gamma, int coeff_usage, CvSize.ByValue win,
            CvTermCriteria.ByValue criteria, int calc_gradient/*=1*/);
    public static void cvSnakeImage(IplImage image, CvPoint[] points, int length, float[] alpha,
            float[] beta, float[] gamma, int coeff_usage, CvSize.ByValue win,
            CvTermCriteria.ByValue criteria, int calc_gradient/*=1*/) {
        cvSnakeImage(image, points[0], length, alpha,
                beta, gamma, coeff_usage, win, criteria, calc_gradient);
    }


    public static native void cvCalcOpticalFlowHS(CvArr prev, CvArr curr, int use_previous, CvArr velx,
            CvArr vely, double lambda, CvTermCriteria.ByValue criteria);
    public static native void cvCalcOpticalFlowLK(CvArr prev, CvArr curr, CvSize.ByValue win_size, CvArr velx, CvArr vely);
    public static native void cvCalcOpticalFlowBM(CvArr prev, CvArr curr, CvSize.ByValue block_size, CvSize.ByValue shift_size,
            CvSize.ByValue max_range, int use_previous, CvArr velx, CvArr vely);
    public static final int
            CV_LKFLOW_PYR_A_READY       = 1,
            CV_LKFLOW_PYR_B_READY       = 2,
            CV_LKFLOW_INITIAL_GUESSES   = 4,
            CV_LKFLOW_GET_MIN_EIGENVALS = 8;
    public static native void cvCalcOpticalFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
            CvArr curr_pyr, CvPoint2D32f prev_features, CvPoint2D32f curr_features,
            int count, CvSize.ByValue win_size, int level, byte[] status,
            float[] track_error, CvTermCriteria.ByValue criteria, int flags);
    public static void cvCalcOpticalFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
            CvArr curr_pyr,  CvPoint2D32f[] prev_features, CvPoint2D32f[] curr_features,
            int count, CvSize.ByValue win_size, int level, byte[] status,
            float[] track_error, CvTermCriteria.ByValue criteria, int flags) {
        cvCalcOpticalFlowPyrLK(prev, curr, prev_pyr, curr_pyr, prev_features[0],
                curr_features[0], count, win_size, level, status,
                track_error, criteria, flags);
    }
    public static native void cvCalcAffineFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
            CvArr curr_pyr, CvPoint2D32f prev_features, CvPoint2D32f curr_features,
            float[] matrices, int count, CvSize.ByValue win_size, int level,
            byte[] status, float[] track_error, CvTermCriteria.ByValue criteria, int flags);
    public static void cvCalcAffineFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
            CvArr curr_pyr, CvPoint2D32f[] prev_features, CvPoint2D32f[] curr_features,
            float[] matrices, int count, CvSize.ByValue win_size, int level,
            byte[] status, float[] track_error, CvTermCriteria.ByValue criteria, int flags) {
        cvCalcAffineFlowPyrLK(prev, curr, prev_pyr, curr_pyr,
                prev_features[0], curr_features[0], matrices, count, win_size, level,
                status, track_error, criteria, flags);
    }
    public static native int cvEstimateRigidTransform(CvArr A, CvArr B, CvMat M, int full_affine);


    public static class CvFeatureTree extends PointerType { };

    public static native CvFeatureTree cvCreateFeatureTree(CvMat desc);
    public static native void cvReleaseFeatureTree(CvFeatureTree tr);
    public static native void cvFindFeatures(CvFeatureTree tr, CvMat desc, CvMat results, CvMat dist,
            int k/*=2*/, int emax/*=20*/);
    public static native int cvFindFeaturesBoxed(CvFeatureTree tr, CvMat bounds_min,
            CvMat bounds_max, CvMat results);


    public static class CvKalman extends Structure {
        public CvKalman() { cvcreated = false; }
        public CvKalman(Pointer m) { useMemory(m); read(); cvcreated = true; }

        public static CvKalman create(int dynam_params, int measure_params,
                int control_params/*=0*/) {
            CvKalman k = cv.cvCreateKalman(dynam_params, measure_params, control_params);
            if (k != null) {
                k.cvcreated = true;
            }
            return k;
        }
        public void release() {
            cvcreated = false;
            cv.cvReleaseKalman(pointerByReference());
        }
        @Override protected void finalize() {
            if (cvcreated) {
                release();
            }
        }
        private boolean cvcreated = false;

        public int MP;
        public int DP;
        public int CP;

        public FloatByReference PosterState;
        public FloatByReference PriorState;
        public FloatByReference DynamMatr;
        public FloatByReference MeasurementMatr;
        public FloatByReference MNCovariance;
        public FloatByReference PNCovariance;
        public FloatByReference KalmGainMatr;
        public FloatByReference PriorErrorCovariance;
        public FloatByReference PosterErrorCovariance;
        public FloatByReference Temp1;
        public FloatByReference Temp2;

        public CvMat.ByReference state_pre;
        public CvMat.ByReference state_post;
        public CvMat.ByReference transition_matrix;
        public CvMat.ByReference control_matrix;
        public CvMat.ByReference measurement_matrix;
        public CvMat.ByReference process_noise_cov;
        public CvMat.ByReference measurement_noise_cov;
        public CvMat.ByReference error_cov_pre;
        public CvMat.ByReference gain;
        public CvMat.ByReference error_cov_post;

        public CvMat.ByReference temp1;
        public CvMat.ByReference temp2;
        public CvMat.ByReference temp3;
        public CvMat.ByReference temp4;
        public CvMat.ByReference temp5;

        public static class ByReference extends CvKalman implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvKalman p) {
                setStructure(p);
            }
            public CvKalman getStructure() {
                return new CvKalman(getValue());
            }
            public void getStructure(CvKalman p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvKalman p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }
    public static native CvKalman cvCreateKalman(int dynam_params, int measure_params, int control_params/*=0*/);
    public static native void cvReleaseKalman(CvKalman.PointerByReference kalman);
    public static native CvMat cvKalmanPredict(CvKalman kalman, CvMat control/*=null*/);
    public static native CvMat cvKalmanCorrect(CvKalman kalman, CvMat measurement);

    public static class CvRandState extends Structure {
        long /* CvRNG */ state;
        int              disttype;
        CvScalar[]       param = new CvScalar[2];

        public static class ByReference extends CvRandState implements Structure.ByReference { }
    }

    public static class CvConDensation extends Structure {
        public CvConDensation() { cvcreated = false; }
        public CvConDensation(Pointer m) { useMemory(m); read(); cvcreated = true; }

        public static CvConDensation create(int dynam_params, int measure_params,
                int sample_count) {
            CvConDensation c = cv.cvCreateConDensation(dynam_params, measure_params, sample_count);
            if (c != null) {
                c.cvcreated = true;
            }
            return c;
        }
        public void release() {
            cvcreated = false;
            cv.cvReleaseConDensation(pointerByReference());
        }
        @Override protected void finalize() {
            if (cvcreated) {
                release();
            }
        }
        private boolean cvcreated = false;

        public int MP;
        public int DP;
        public FloatByReference DynamMatr;
        public FloatByReference State;
        public int SamplesNum;
        public FloatByReference /* float** */ flSamples;
        public FloatByReference /* float** */ flNewSamples;
        public FloatByReference flConfidence;
        public FloatByReference flCumulative;
        public FloatByReference Temp;
        public FloatByReference RandomSample;
        public CvRandState.ByReference RandS;

        public static class ByReference extends CvConDensation implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvConDensation p) {
                setStructure(p);
            }
            public CvConDensation getStructure() {
                return new CvConDensation(getValue());
            }
            public void getStructure(CvConDensation p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvConDensation p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }
    public static native CvConDensation cvCreateConDensation(int dynam_params, int measure_params, int sample_count);
    public static native void cvReleaseConDensation(CvConDensation.PointerByReference condens);
    public static native void cvConDensInitSampleSet(CvConDensation condens, CvMat lower_bound, CvMat upper_bound);
    public static native void cvConDensUpdateByTime(CvConDensation condens);


    public static final int CV_HAAR_MAGIC_VAL    = 0x42500000;
    public static final String CV_TYPE_NAME_HAAR = "opencv-haar-classifier";

    public static final int CV_HAAR_FEATURE_MAX = 3;

    public static class CvHaarFeature extends Structure {
        public int tilted;
        public static class Rect extends Structure {
            CvRect r;
            float weight;
        }
        public Rect[] rect = new Rect[CV_HAAR_FEATURE_MAX];

        public static class ByReference extends CvHaarFeature implements Structure.ByReference { }
    }

    public static class CvHaarClassifier extends Structure {
        public int count;
        public CvHaarFeature.ByReference haar_feature;
        public FloatByReference threshold;
        public IntByReference left;
        public IntByReference right;
        public FloatByReference alpha;

        public static class ByReference extends CvHaarClassifier implements Structure.ByReference { }
    }

    public static class CvHaarStageClassifier extends Structure {
        public int  count;
        public float threshold;
        public CvHaarClassifier.ByReference classifier;

        public int next;
        public int child;
        public int parent;

        public static class ByReference extends CvHaarStageClassifier implements Structure.ByReference { }
    }

    public static class CvHidHaarClassifierCascade extends PointerType { }

    public static class CvHaarClassifierCascade extends Structure {
        public CvHaarClassifierCascade() { cvcreated = false; }
        public CvHaarClassifierCascade(Pointer m) { useMemory(m); read(); cvcreated = true; }

        public static CvHaarClassifierCascade load(String directory,
                CvSize.ByValue orig_window_size) {
            CvHaarClassifierCascade h = cv.cvLoadHaarClassifierCascade(directory,
                    orig_window_size);
            if (h != null) {
                h.cvcreated = true;
            }
            return h;
        }
        public void release() {
            cvcreated = false;
            cv.cvReleaseHaarClassifierCascade(pointerByReference());
        }
        @Override protected void finalize() {
            if (cvcreated) {
                release();
            }
        }
        private boolean cvcreated = false;

        public int  flags;
        public int  count;
        public CvSize orig_window_size;
        public CvSize real_window_size;
        public double scale;
        public CvHaarStageClassifier.ByReference stage_classifier;
        public CvHidHaarClassifierCascade hid_cascade;

        public boolean CV_IS_HAAR_CLASSIFIER() {
            return (flags & cxcore.CV_MAGIC_MASK)==CV_HAAR_MAGIC_VAL;
        }

        public static class ByReference extends CvHaarClassifierCascade implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvHaarClassifierCascade p) {
                setStructure(p);
            }
            public CvHaarClassifierCascade getStructure() {
                return new CvHaarClassifierCascade(getValue());
            }
            public void getStructure(CvHaarClassifierCascade p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvHaarClassifierCascade p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class CvAvgComp extends Structure {
        public CvRect rect;
        public int neighbors;
    }

    public static native CvHaarClassifierCascade cvLoadHaarClassifierCascade(String directory, CvSize.ByValue orig_window_size);
    public static native void cvReleaseHaarClassifierCascade(CvHaarClassifierCascade.PointerByReference cascade);
    public static final int
            CV_HAAR_DO_CANNY_PRUNING    = 1,
            CV_HAAR_SCALE_IMAGE         = 2,
            CV_HAAR_FIND_BIGGEST_OBJECT = 4,
            CV_HAAR_DO_ROUGH_SEARCH     = 8;
    public static native CvSeq cvHaarDetectObjects(CvArr image, CvHaarClassifierCascade cascade,
            CvMemStorage storage, double scale_factor/*=1.1*/, int min_neighbors/*=3*/,
            int flags/*=0*/, CvSize.ByValue min_size/*=cvSize(0,0)*/);
    public static native void cvSetImagesForHaarClassifierCascade(CvHaarClassifierCascade cascade,
            CvArr sum, CvArr sqsum, CvArr tilted_sum, double scale);
    public static native int cvRunHaarClassifierCascade(CvHaarClassifierCascade cascade,
            CvPoint.ByValue pt, int start_stage/*=0*/);


    public static native void cvProjectPoints2(CvMat object_points, CvMat rotation_vector,
            CvMat translation_vector, CvMat intrinsic_matrix,
            CvMat distortion_coeffs, CvMat image_points,
            CvMat dpdrot/*=null*/, CvMat dpdt/*=null*/, CvMat dpd/*=null*/,
            CvMat dpdc/*=null*/, CvMat dpddist/*=null*/, double aspect_ratio/*=0*/);
    public static void cvProjectPoints2(CvMat object_points, CvMat rotation_vector,
            CvMat translation_vector, CvMat intrinsic_matrix,
            CvMat distortion_coeffs, CvMat image_points) {
        cvProjectPoints2(object_points, rotation_vector,translation_vector,
                intrinsic_matrix, distortion_coeffs, image_points,
                null, null, null, null, null, 0);
    }
    public static final int
            CV_LMEDS = 4,
            CV_RANSAC = 8;
    public static native void cvFindHomography(CvMat src_points, CvMat dst_points, CvMat homography,
            int method/*=0*/, double ransacReprojThreshold/*=0*/, CvMat mask/*=null*/);
    public static void cvFindHomography(CvMat src_points, CvMat dst_points, CvMat homography) {
        cvFindHomography(src_points, dst_points, homography, 0, 0, null);
    }

    public static final int
            CV_CALIB_USE_INTRINSIC_GUESS = 1,
            CV_CALIB_FIX_ASPECT_RATIO    = 2,
            CV_CALIB_FIX_PRINCIPAL_POINT = 4,
            CV_CALIB_ZERO_TANGENT_DIST   = 8,
            CV_CALIB_FIX_FOCAL_LENGTH    = 16,
            CV_CALIB_FIX_K1              = 32,
            CV_CALIB_FIX_K2              = 64,
            CV_CALIB_FIX_K3              = 128;
    public static native void cvCalibrateCamera2(CvMat object_points, CvMat image_points,
            CvMat point_counts, CvSize.ByValue image_size,
            CvMat intrinsic_matrix, CvMat distortion_coeffs,
            CvMat rotation_vectors/*=null*/, CvMat translation_vectors/*=null*/, int flags/*=0*/);
    public static native void cvCalibrationMatrixValues(CvMat camera_matrix, CvSize image_size,
            double aperture_width/*=0*/, double aperture_height/*=0*/,
            DoubleByReference fovx/*=null*/, DoubleByReference fovy/*=null*/,
            DoubleByReference focal_length/*=null*/, CvPoint2D64f principal_point/*=null*/,
            DoubleByReference pixel_aspect_ratio/*=null*/);
    public static native void cvFindExtrinsicCameraParams2(CvMat object_points, CvMat image_points,
            CvMat intrinsic_matrix, CvMat distortion_coeffs,
            CvMat rotation_vector, CvMat translation_vector);
    public static final int
            CV_CALIB_FIX_INTRINSIC     = 256,
            CV_CALIB_SAME_FOCAL_LENGTH = 512;
    public static native void cvStereoCalibrate(CvMat object_points, CvMat image_points1,
            CvMat image_points2, CvMat point_counts,
            CvMat camera_matrix1, CvMat dist_coeffs1,
            CvMat camera_matrix2, CvMat dist_coeffs2,
            CvSize.ByValue image_size, CvMat R, CvMat T, CvMat E/*=null*/,
            CvMat F/*=null*/, CvTermCriteria.ByValue term_crit /*=cvTermCriteria(
            CV_TERMCRIT_ITER+CV_TERMCRIT_EPS,30,1e-6)*/, int flags/*=CV_CALIB_FIX_INTRINSIC*/);
    public static final int CV_CALIB_ZERO_DISPARITY = 1024;
    public static native void cvStereoRectify(CvMat camera_matrix1, CvMat camera_matrix2,
            CvMat dist_coeffs1, CvMat dist_coeffs2,
            CvSize.ByValue image_size, CvMat R, CvMat T,
            CvMat R1, CvMat R2, CvMat P1, CvMat P2, CvMat Q/*=null*/,
            int flags/*=CV_CALIB_ZERO_DISPARITY*/);
    public static native int cvStereoRectifyUncalibrated(CvMat points1, CvMat points2,
            CvMat F, CvSize.ByValue img_size, CvMat H1, CvMat H2, double threshold/*=5*/);
    public static native int cvRodrigues2(CvMat src, CvMat dst, CvMat jacobian/*=null*/);
    public static native void cvUndistort2(CvArr src, CvArr dst, CvMat intrinsic_matrix, CvMat distortion_coeffs);
    public static native void cvInitUndistortMap(CvMat intrinsic_matrix, CvMat distortion_coeffs,
            CvArr mapx, CvArr mapy);
    public static native void cvInitUndistortRectifyMap(CvMat camera_matrix, CvMat dist_coeffs,
            CvMat R, CvMat new_camera_matrix, CvArr mapx, CvArr mapy);
    public static native void cvUndistortPoints(CvMat src, CvMat dst, CvMat camera_matrix,
            CvMat dist_coeffs, CvMat R/*=null*/, CvMat P/*=null*/);

    public static final int
            CV_CALIB_CB_ADAPTIVE_THRESH = 1,
            CV_CALIB_CB_NORMALIZE_IMAGE = 2,
            CV_CALIB_CB_FILTER_QUADS    = 4;
    public static native int cvFindChessboardCorners(CvArr image, CvSize.ByValue pattern_size,
            CvPoint2D32f corners, IntByReference corner_count/*=null*/,
            int flags/*=CV_CALIB_CB_ADAPTIVE_THRESH */);
    public static int cvFindChessboardCorners(CvArr image, CvSize.ByValue pattern_size,
            CvPoint2D32f[] corners, IntByReference corner_count/*=null*/,
            int flags/*=CV_CALIB_CB_ADAPTIVE_THRESH */) {
        return cvFindChessboardCorners(image, pattern_size, corners[0], corner_count, flags);
    }
    public static native void cvDrawChessboardCorners(CvArr image, CvSize.ByValue pattern_size,
            CvPoint2D32f corners, int count, int pattern_was_found);
    public static void cvDrawChessboardCorners(CvArr image, CvSize.ByValue pattern_size,
            CvPoint2D32f[] corners, int count, int pattern_was_found) {
        cvDrawChessboardCorners(image, pattern_size, corners[0], count, pattern_was_found);
    }


    public static class CvPOSITObject extends PointerType {
        public CvPOSITObject() { cvcreated = false; }
        public CvPOSITObject(Pointer p) { super(p); cvcreated = true; }

        public static CvPOSITObject create(CvPoint3D32f[] points) {
            CvPOSITObject p = cv.cvCreatePOSITObject(points, points.length);
            if (p != null) {
                p.cvcreated = true;
            }
            return p;
        }

        public void release() {
            cvcreated = false;
            cv.cvReleasePOSITObject(pointerByReference());
        }
        @Override protected void finalize() {
            if (cvcreated) {
                release();
            }
        }
        private boolean cvcreated = false;

        public static class ByReference extends CvPOSITObject implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvPOSITObject p) {
                setStructure(p);
            }
            public CvPOSITObject getStructure() {
                return new CvPOSITObject(getValue());
            }
            public void getStructure(CvPOSITObject p) {
                p.setPointer(getValue());
            }
            public void setStructure(CvPOSITObject p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }
    public static native CvPOSITObject cvCreatePOSITObject(CvPoint3D32f points, int point_count);
    public static CvPOSITObject cvCreatePOSITObject(CvPoint3D32f[] points, int point_count) {
        return cvCreatePOSITObject(points[0], point_count);
    }
    public static native void cvPOSIT(CvPOSITObject posit_object, CvPoint2D32f image_points,
            double focal_length, CvTermCriteria.ByValue criteria,
            float[] /*CvMatr32f*/ rotation_matrix, float[] /*CvVect32f*/ translation_vector);
    public static void cvPOSIT(CvPOSITObject posit_object, CvPoint2D32f[] image_points,
            double focal_length, CvTermCriteria.ByValue criteria,
            float[] /*CvMatr32f*/ rotation_matrix, float[] /*CvVect32f*/ translation_vector) {
        cvPOSIT(posit_object, image_points[0],
                focal_length, criteria, rotation_matrix, translation_vector);
    }
    public static native void cvReleasePOSITObject(CvPOSITObject.PointerByReference posit_object);
    public static native void cvCalcImageHomography(float[] line, CvPoint3D32f center,
            float[] intrinsic, float[] homography);


    public static final int
            CV_FM_7POINT = 1,
            CV_FM_8POINT = 2,
            CV_FM_LMEDS_ONLY = CV_LMEDS,
            CV_FM_RANSAC_ONLY = CV_RANSAC,
            CV_FM_LMEDS = CV_LMEDS,
            CV_FM_RANSAC = CV_RANSAC;
    public static native int cvFindFundamentalMat(CvMat points1, CvMat points2, CvMat fundamental_matrix,
            int method/*=CV_FM_RANSAC*/, double param1/*=3*/,
            double param2/*=0.99*/, CvMat status/*=null*/);
    public static native void cvComputeCorrespondEpilines(CvMat points, int which_image,
            CvMat fundamental_matrix, CvMat correspondent_lines);
    public static native void cvConvertPointsHomogeneous(CvMat src, CvMat dst);
    public static native int cvRANSACUpdateNumIters(double p, double err_prob, int model_points, int max_iters);

    public static final int CV_STEREO_BM_NORMALIZED_RESPONSE = 0;
    public static class CvStereoBMState extends Structure {
        public CvStereoBMState() { cvcreated = false; }
        public CvStereoBMState(Pointer m) { useMemory(m); read(); cvcreated = true; }

        public static CvStereoBMState create(int preset, int numberOfDisparities) {
            CvStereoBMState m = cv.cvCreateStereoBMState(preset, numberOfDisparities);
            if (m != null) {
                m.cvcreated = true;
            }
            return m;
        }
        public void release() {
            cvcreated = false;
            cv.cvReleaseStereoBMState(pointerByReference());
        }
        @Override protected void finalize() {
            if (cvcreated) {
                release();
            }
        }
        private boolean cvcreated = false;

        public int preFilterType = CV_STEREO_BM_NORMALIZED_RESPONSE;
        public int preFilterSize;
        public int preFilterCap;

        public int SADWindowSize; 
        public int minDisparity;
        public int numberOfDisparities;

        public int textureThreshold;
        public int uniquenessRatio;
        public int speckleWindowSize;
        public int speckleRange;

        public CvMat.ByReference preFilteredImg0;
        public CvMat.ByReference preFilteredImg1;
        public CvMat.ByReference slidingSumBuf;

        public static class ByReference extends CvStereoBMState implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvStereoBMState p) {
                setStructure(p);
            }
            public CvStereoBMState getStructure() {
                return new CvStereoBMState(getValue());
            }
            public void getStructure(CvStereoBMState p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvStereoBMState p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }
    public static final int
            CV_STEREO_BM_BASIC = 0,
            CV_STEREO_BM_FISH_EYE = 1,
            CV_STEREO_BM_NARROW = 2;
    public static native CvStereoBMState cvCreateStereoBMState(int preset/*=CV_STEREO_BM_BASIC*/,
            int numberOfDisparities/*=0*/);
    public static native void cvReleaseStereoBMState(CvStereoBMState.PointerByReference state);
    public static native void cvFindStereoCorrespondenceBM(CvArr left, CvArr right,
            CvArr disparity, CvStereoBMState state);

    public static final int CV_STEREO_GC_OCCLUDED = Short.MAX_VALUE;
    public static class CvStereoGCState extends Structure {
        public CvStereoGCState() { cvcreated = false; }
        public CvStereoGCState(Pointer m) { useMemory(m); read(); cvcreated = true; }

        public static CvStereoGCState create(int numberOfDisparities, int maxIters) {
            CvStereoGCState m = cv.cvCreateStereoGCState(numberOfDisparities, maxIters);
            if (m != null) {
                m.cvcreated = true;
            }
            return m;
        }
        public void release() {
            cvcreated = false;
            cv.cvReleaseStereoGCState(pointerByReference());
        }
        @Override protected void finalize() {
            if (cvcreated) {
                release();
            }
        }
        private boolean cvcreated = false;

        public int Ithreshold;
        public int interactionRadius;
        public float K, lambda, lambda1, lambda2;
        public int occlusionCost;
        public int minDisparity;
        public int numberOfDisparities;
        public int maxIters;

        public CvMat.ByReference left;
        public CvMat.ByReference right;
        public CvMat.ByReference dispLeft;
        public CvMat.ByReference dispRight;
        public CvMat.ByReference ptrLeft;
        public CvMat.ByReference ptrRight;
        public CvMat.ByReference vtxBuf;
        public CvMat.ByReference edgeBuf;

        public static class ByReference extends CvStereoGCState implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvStereoGCState p) {
                setStructure(p);
            }
            public CvStereoGCState getStructure() {
                return new CvStereoGCState(getValue());
            }
            public void getStructure(CvStereoGCState p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvStereoGCState p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }
    public static native CvStereoGCState cvCreateStereoGCState(int numberOfDisparities, int maxIters);
    public static native void cvReleaseStereoGCState(CvStereoGCState.PointerByReference state);
    public static native void cvFindStereoCorrespondenceGC(CvArr left, CvArr right,
            CvArr disparityLeft, CvArr disparityRight,
            CvStereoGCState state, int useDisparityGuess/*=0*/);
    public static native void cvReprojectImageTo3D(CvArr disparityImage, CvArr _3dImage, CvMat Q);
}
