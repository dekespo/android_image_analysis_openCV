#ifndef IMAGEANALYSISOPENCV_CAMERAANALYSISCONTROL_H
#define IMAGEANALYSISOPENCV_CAMERAANALYSISCONTROL_H

#include <opencv2/core/mat.hpp>
#include <opencv2/imgproc/types_c.h>
#include <opencv2/imgproc.hpp>
#include <jni.h>

class CameraAnalysisControl
{
private:
    cv::Mat& m_MatCamera;
    int m_width, m_height;

    cv::ColorConversionCodes m_colourType;
    cv::ThresholdTypes m_thresholdType;
    int m_thresholdValue;
    int m_thresholdMaxValue;

    void m_updateMatColour();
    void m_updateMatGray();
    void m_updateMatGray(cv::Mat &);
    void m_updateMatBinary();
    void m_updateMatHistoEq();
    void m_updateMatInvert();
    void m_updateMatSalt();
    void m_updateMatGetShapes();

    void findCircles();
    void findRectangles();

    enum MODESTATE
    {
        COLOUR = 0,
        GRAY = 1,
        BINARY = 2,
        HISTOEQ = 3,
        INVERT = 4,
        SALT = 5,
        GET_SHAPES = 6
    };

public:
    CameraAnalysisControl(long int, int, int, int, int, int);
    ~CameraAnalysisControl();
};

#endif //IMAGEANALYSISOPENCV_CAMERAANALYSISCONTROL_H
