package com.sample.dekespo.imageanalysisopencv;

public enum SegmentationTypes {
    CIRCLES(0),
    RECTANGLES(1),
    TRIANGLES(2),
    ALL(3),
    LARGEST_OBJECT(4);

    private int numVal;

    SegmentationTypes(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
