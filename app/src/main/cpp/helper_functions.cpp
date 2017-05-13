#include "helper_functions.h"

namespace helper_functions
{
    bool compareArea(std::vector<cv::Point> a, std::vector<cv::Point> b)
    {
        return cv::contourArea(a) < cv::contourArea(b);
    }
}
