package com.sample.dekespo.imageanalysisopencv;

import org.opencv.imgproc.Imgproc;

public enum ColourTypeModes
{
    COLOR_RGB2RGBA(Imgproc.COLOR_RGB2RGBA),
    COLOR_RGB2GRAY(Imgproc.COLOR_RGB2GRAY),
    COLOR_RGB2YCrCb(Imgproc.COLOR_RGB2YCrCb),
    COLOR_RGB2Lab(Imgproc.COLOR_RGB2Lab),
    COLOR_RGB2HLS(Imgproc.COLOR_RGB2HLS),
    COLOR_RGB2YUV(Imgproc.COLOR_RGB2YUV),
    COLOR_RGB2HSV(Imgproc.COLOR_RGB2HSV);

    private int numVal;

    ColourTypeModes(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
