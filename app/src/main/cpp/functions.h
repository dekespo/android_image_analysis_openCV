#ifndef IMAGEANALYSISOPENCV_FUNCTIONS_H
#define IMAGEANALYSISOPENCV_FUNCTIONS_H

#include <opencv2/core/mat.hpp>
#include <opencv2/imgproc/types_c.h>
#include <opencv2/imgproc.hpp>
#include <vector>

namespace functions
{
    void convertGreyMatToBinary(long int);
    void findGeometricShapes(long int);
    void findCircles(long int);
    void findRectangles(long int);
}

#endif //IMAGEANALYSISOPENCV_FUNCTIONS_H
