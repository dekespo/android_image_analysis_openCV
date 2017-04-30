package com.sample.dekespo.imageanalysisopencv;

public enum ModeStatus {
    COLOUR(0),
    GRAY(1),
    BINARY(2),
    HISTOEQ(3),
    INVERT(4),
    SALT(5),
    GET_SHAPES(6);

    private int numVal;

    ModeStatus(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}

