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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import name.audet.samuel.javacv.FrameGrabber.ColorMode;
import name.audet.samuel.javacv.jna.cxcore.CvFileStorage;

/**
 *
 * @author Samuel Audet
 */
public class CameraDevice extends ProjectiveDevice {
    public CameraDevice(String name, String filename) {
        super(name, filename);
        this.settings = new Settings(super.getSettings());
        settings.setImageWidth(imageWidth);
        settings.setImageHeight(imageHeight);
    }
    public CameraDevice(String name, CvFileStorage fs) {
        super(name, fs);
        this.settings = new Settings(super.getSettings());
        settings.setImageWidth(imageWidth);
        settings.setImageHeight(imageHeight);
    }
    public CameraDevice(Settings settings) {
        super(settings);
        setSettings(settings);
    }

    public static class Settings extends ProjectiveDevice.Settings {
        public Settings() { }
        public Settings(ProjectiveDevice.Settings settings) {
            super(settings);
            if (settings instanceof Settings) {
                Settings s = (Settings)settings;
                this.deviceNumber = s.deviceNumber;
                this.frameGrabber = s.frameGrabber;
                this.imageWidth = s.imageWidth;
                this.imageHeight = s.imageHeight;
                this.frameRate = s.frameRate;
                this.triggerMode = s.triggerMode;
                this.bpp = s.bpp;
                this.colorMode = s.colorMode;
                this.timeout = s.timeout;
                this.numBuffers = s.numBuffers;
            }
        }

        int deviceNumber = 0;
        Class<? extends FrameGrabber> frameGrabber;

        public int getDeviceNumber() {
            return deviceNumber;
        }
        public void setDeviceNumber(int deviceNumber) {
            String oldDescription = getDescription();
            pcs.firePropertyChange("deviceNumber", deviceNumber, this.deviceNumber = deviceNumber);
            pcs.firePropertyChange("description", oldDescription, getDescription());
        }

        public Class<? extends FrameGrabber> getFrameGrabber() {
            return frameGrabber;
        }
        public void setFrameGrabber(Class<? extends FrameGrabber> frameGrabber) {
            String oldDescription = getDescription();
            pcs.firePropertyChange("frameGrabber", frameGrabber, this.frameGrabber = frameGrabber);
            pcs.firePropertyChange("description", oldDescription, getDescription());
        }

        public String getDescription() {
            String[] descriptions = null;
            try {
                Method m = frameGrabber.getMethod("getDeviceDescriptions");
                descriptions = (String[])m.invoke(null);
            } catch (Exception ex) { }

            if (descriptions != null && deviceNumber < descriptions.length) {
                return descriptions[deviceNumber];
            } else {
                return "";
            }
        }

        int imageWidth = 0, imageHeight = 0;
        double frameRate = 0;
        boolean triggerMode = false;
        int bpp = 0;
        ColorMode colorMode = ColorMode.RAW;
        int timeout = 10000;
        int numBuffers = 4;

        public int getImageWidth() {
            return imageWidth;
        }
        public void setImageWidth(int imageWidth) {
            this.imageWidth = imageWidth;
        }

        public int getImageHeight() {
            return imageHeight;
        }
        public void setImageHeight(int imageHeight) {
            this.imageHeight = imageHeight;
        }

        public double getFrameRate() {
            return frameRate;
        }
        public void setFrameRate(double frameRate) {
            this.frameRate = frameRate;
        }

        public boolean isTriggerMode() {
            return triggerMode;
        }
        public void setTriggerMode(boolean triggerMode) {
            this.triggerMode = triggerMode;
        }

        public int getBitsPerPixel() {
            return bpp;
        }
        public void setBitsPerPixel(int bitsPerPixel) {
            this.bpp = bitsPerPixel;
        }

        public ColorMode getColorMode() {
            return colorMode;
        }
        public void setColorMode(ColorMode colorMode) {
            this.colorMode = colorMode;
        }

        public int getTimeout() {
            return timeout;
        }
        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getNumBuffers() {
            return numBuffers;
        }
        public void setNumBuffers(int numBuffers) {
            this.numBuffers = numBuffers;
        }

    }

    private Settings settings;
    @Override public Settings getSettings() {
        return settings;
    }
    @Override public void setSettings(ProjectiveDevice.Settings settings) {
        super.setSettings(settings);
        this.settings = new Settings(settings);
        if (settings.name == null || settings.name.length() == 0) {
            settings.name = "Camera " + String.format("%2d", this.settings.deviceNumber);
        }
    }

    public FrameGrabber createFrameGrabber() throws Exception {
        try {
            settings.frameGrabber.getMethod("tryLoad").invoke(null);
            FrameGrabber f = settings.frameGrabber.getConstructor(int.class).newInstance(settings.deviceNumber);
            f.setImageWidth(settings.getImageWidth());
            f.setImageHeight(settings.getImageHeight());
            f.setFrameRate(settings.getFrameRate());
            f.setTriggerMode(settings.isTriggerMode());
            f.setBitsPerPixel(settings.getBitsPerPixel());
            f.setColorMode(settings.getColorMode());
            f.setTimeout(f.getTimeout());
            f.setNumBuffers(settings.getNumBuffers());
            return f;
        } catch (InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            if (t instanceof Exception) {
                throw (Exception)t;
            } else {
                throw new Exception(t);
            }
        }
    }

}
