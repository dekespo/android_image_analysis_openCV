package com.sample.dekespo.imageanalysisopencv;

import org.opencv.imgproc.Imgproc;

public enum ColourTypeModes
{
    COLOR_RGB2GRAY(Imgproc.COLOR_RGB2GRAY),
    COLOR_RGBA2GRAY(Imgproc.COLOR_RGBA2GRAY); // TODO, they seem the same

    private int numVal;

    ColourTypeModes(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
