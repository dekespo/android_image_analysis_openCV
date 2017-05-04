#include "CameraAnalysisControl.h"

CameraAnalysisControl::~CameraAnalysisControl() {}

CameraAnalysisControl::CameraAnalysisControl(long int _addressofMat, int modeState, int _colourType, int _thresholdType, int _thresholdValue, int thresholdMaxValue) : m_MatCamera(*(cv::Mat *) _addressofMat)
{

    m_width = m_MatCamera.rows;
    m_height = m_MatCamera.cols;

    m_colourType = (cv::ColorConversionCodes)_colourType;
    m_thresholdType = (cv::ThresholdTypes)_thresholdType;
    m_thresholdValue = _thresholdValue;
    m_thresholdMaxValue = thresholdMaxValue;

    switch (modeState)
    {
        case MODESTATE::COLOUR:
            m_updateMatColour();
            break;
        case MODESTATE::GRAY:
            m_updateMatGray();
            break;
        case MODESTATE::BINARY:
            m_updateMatBinary();
            break;
        case MODESTATE::HISTOEQ:
            m_updateMatHistoEq();
            break;
        case MODESTATE::INVERT:
            m_updateMatInvert();
            break;
        case MODESTATE::SALT:
            m_updateMatSalt();
            break;
        case MODESTATE::GET_SHAPES:
            m_updateMatGetShapes();
            break;
        default:
            // ERROR
            break;
    }
}

void CameraAnalysisControl::m_updateMatColour()
{
    cv::cvtColor(m_MatCamera, m_MatCamera, m_colourType);
}

void CameraAnalysisControl::m_updateMatGray() // TODO: remove it completely?
{
    cv::cvtColor(m_MatCamera, m_MatCamera, m_colourType);
}

void CameraAnalysisControl::m_updateMatGray(cv::Mat &output)
{
    cv::cvtColor(m_MatCamera, output, m_colourType);
}

void CameraAnalysisControl::m_updateMatBinary()
{
    m_updateMatGray();
    cv::threshold(m_MatCamera, m_MatCamera, m_thresholdValue, m_thresholdMaxValue, m_thresholdType);
}

void CameraAnalysisControl::m_updateMatHistoEq()
{
    m_updateMatGray();
    cv::equalizeHist(m_MatCamera, m_MatCamera);
}

void CameraAnalysisControl::m_updateMatInvert()
{
    m_updateMatGray();
    cv::bitwise_not(m_MatCamera, m_MatCamera);
}

void CameraAnalysisControl::m_updateMatSalt()
{
    m_updateMatGray();
    for (int k = 0; k < m_thresholdMaxValue / 3; k++)
    {
        int i = rand() % m_height;
        int j = rand() % m_width;
        m_MatCamera.at<uchar>(j, i) = 255;
    }
}

void CameraAnalysisControl::m_updateMatGetShapes()
{
    // TODO Check CameraUIController.java for different cases
        findCircles();
        findRectangles();
}

void CameraAnalysisControl::findCircles()
{
    cv::Mat hiddenMat;
    m_updateMatGray(hiddenMat);
    cv::medianBlur(hiddenMat, hiddenMat, 5);

    std::vector<cv::Vec3f>  circles;

    cv::HoughCircles(hiddenMat, circles, CV_HOUGH_GRADIENT, 1, m_MatCamera.rows / 16, 100, 30, 1 ,30); // TODO: new parameters

    for(cv::Vec3i const& circle: circles)
    {
        cv::circle(m_MatCamera, cv::Point(circle[0], circle[1]), circle[2], cv::Scalar(0, 0, 255), 3, cv::LINE_AA); // TODO: new parameters
    }
}

void CameraAnalysisControl::findRectangles()
{
    cv::Mat hiddenMat;
    m_updateMatGray(hiddenMat);
    cv::threshold(hiddenMat, hiddenMat, m_thresholdValue, m_thresholdMaxValue, m_thresholdType);

    std::vector<std::vector<cv::Point>> contours;
    cv::findContours(hiddenMat, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE); // TODO: new parameters
    double minimumAreaToShow = m_width * m_height / 20;
    double maximumAreaToShow = m_width * m_height / 4;

    for(int i = 0; i < contours.size(); i++)
    {
        double currentContourArea = cv::contourArea(contours[i]);

        if(minimumAreaToShow <  currentContourArea && currentContourArea < maximumAreaToShow)
        {
            cv::RotatedRect rotatedRect = cv::minAreaRect(contours[i]);
            cv::Point2f rectPoints[4];
            rotatedRect.points(rectPoints);

            for(unsigned int j = 0; j < 4; j++)
                cv::line(m_MatCamera, rectPoints[j], rectPoints[(j+1)%4], cv::Scalar(0, 255, 0), 3);
        }
    }

}
