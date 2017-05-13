#ifndef IMAGEANALYSISOPENCV_HELPER_FUNCTIONS_H
#define IMAGEANALYSISOPENCV_HELPER_FUNCTIONS_H

#include <opencv2/core/types.hpp>
#include <opencv2/imgproc.hpp>

namespace helper_functions
{
    bool compareArea(std::vector<cv::Point>,std::vector<cv::Point>);
}

#endif //IMAGEANALYSISOPENCV_HELPER_FUNCTIONS_H
