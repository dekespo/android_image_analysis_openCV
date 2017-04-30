package com.sample.dekespo.imageanalysisopencv;

import org.opencv.imgproc.Imgproc;

public enum ThresholdTypeModes {
    THRESH_BINARY(Imgproc.THRESH_BINARY),
    THRESH_BINARY_INV(Imgproc.THRESH_BINARY_INV),
    THRESH_TRUNC(Imgproc.THRESH_TRUNC), // TODO Gray output?
    THRESH_TOZERO(Imgproc.THRESH_TOZERO), // TODO Gray output?
    THRESH_TOZERO_INV(Imgproc.THRESH_TOZERO_INV), // TODO Gray output?
//    THRESH_MASK(Imgproc.THRESH_MASK), // TODO there is some error
    THRESH_OTSU(Imgproc.THRESH_OTSU),
    THRESH_TRIANGLE(Imgproc.THRESH_TRIANGLE);

    private int numVal;

    ThresholdTypeModes(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
