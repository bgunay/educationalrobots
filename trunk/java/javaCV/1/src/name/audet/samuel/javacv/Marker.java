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

import java.util.Arrays;
import name.audet.samuel.javacv.jna.ARToolKitPlus;
import name.audet.samuel.javacv.jna.cxcore;
import name.audet.samuel.javacv.jna.cxcore.CvMat;
import name.audet.samuel.javacv.jna.cxcore.IplImage;

/**
 *
 * @author Samuel Audet
 */
public class Marker {
    public Marker(int id, double[] corners, double confidence) {
        this.id = id;
        this.corners = corners;
        this.confidence = confidence;
    }
    @Override public Object clone() {
        return new Marker(id, corners.clone(), confidence);
    }
    public int id;
    public double[] corners;
    public double confidence;

    @Override public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.id;
        hash = 37 * hash + (this.corners != null ? this.corners.hashCode() : 0);
        return hash;
    }
    @Override public boolean equals(Object o) {
        if (o instanceof Marker) {
            Marker m = (Marker)o;
            return m.id == id && Arrays.equals(m.corners, corners);
        }
        return false;
    }

    public double[] getCenter() {
        double x = 0, y = 0;
        for (int i = 0; i < 4; i++) {
            x += corners[2*i  ];
            y += corners[2*i+1];
        }
        x /= 4;
        y /= 4;
        return new double[] { x, y };
    }

    public IplImage getImage() {
        return getMarkerImage(id);
    }

    private static IplImage markerImageCache[] = new IplImage[4096];
    public static IplImage getMarkerImage(int id) {
        if (markerImageCache[id] == null) {
            markerImageCache[id] = IplImage.create(8, 8, cxcore.IPL_DEPTH_8U, 1);
            ARToolKitPlus.createImagePatternBCH(id, markerImageCache[id].getByteBuffer());
        }
        return markerImageCache[id];
    }


    public static class ArraySettings extends BaseSettings {
        int rows = 8, columns = 12;
        double size = 200, spacing = 300;
        boolean checkered = true;

        public int getRows() {
            return rows;
        }
        public void setRows(int rows) {
            pcs.firePropertyChange("rows", this.rows, this.rows = rows);
        }

        public int getColumns() {
            return columns;
        }
        public void setColumns(int columns) {
            pcs.firePropertyChange("columns", this.columns, this.columns = columns);
        }

        public double getSize() {
            return size;
        }
        public void setSize(double size) {
            pcs.firePropertyChange("size", this.size, this.size = size);
        }

        public double getSpacing() {
            return spacing;
        }
        public void setSpacing(double spacing) {
            pcs.firePropertyChange("spacing", this.spacing, this.spacing = spacing);
        }

        public boolean isCheckered() {
            return checkered;
        }
        public void setCheckered(boolean checkered) {
            pcs.firePropertyChange("checkered", this.checkered, this.checkered = checkered);
        }
    }
    public static Marker[][] createArray(ArraySettings settings) {
        return createArray(settings, 0, 0);
    }
    public static Marker[][] createArray(ArraySettings settings, double marginx, double marginy) {
        Marker[] markers = new Marker[settings.rows*settings.columns];
        int id = 0;
        for (int y = 0; y < settings.rows; y++) {
            for (int x = 0; x < settings.columns; x++) {
                double s = settings.size/2;
                double cx = x*settings.spacing + s + marginx;
                double cy = y*settings.spacing + s + marginy;
                markers[id] = new Marker(id, new double[] {
                    cx-s, cy-s,  cx+s, cy-s,  cx+s, cy+s,  cx-s, cy+s }, 1);
                id++;
            }
        }
        if (!settings.checkered) {
            return new Marker[][] { markers };
        } else {
            Marker[] markers1 = new Marker[markers.length/2];
            Marker[] markers2 = new Marker[markers.length/2];
            for (int i = 0; i < markers.length; i++) {
                int x = i%settings.columns;
                int y = i/settings.columns;
                if (x%2==0 ^ y%2==0) {
                    markers2[i/2] = markers[i];
                } else {
                    markers1[i/2] = markers[i];
                }
            }
            return new Marker[][] { markers2, markers1 };
        }
    }
    public static Marker[][] createArray(int rows, int columns, double size,
            double spacing, boolean checkered, double marginx, double marginy) {

        ArraySettings s = new ArraySettings();
        s.rows = rows;
        s.columns = columns;
        s.size = size;
        s.spacing = spacing;
        s.checkered = checkered;

        return createArray(s, marginx, marginy);
    }

    public static void applyWarp(Marker[] markers, CvMat warp) {
        CvMat pts = CvMat.create(4, 1, cxcore.CV_64F, 2);

        for (Marker m : markers) {
            pts.put(m.corners);
            cxcore.cvPerspectiveTransform(pts, pts, warp);
            pts.get(m.corners);
        }
    }
}
