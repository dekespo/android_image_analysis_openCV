#include "CameraAnalysisControl.h"
#include "helper_functions.h"

CameraAnalysisControl::~CameraAnalysisControl() {}

CameraAnalysisControl::CameraAnalysisControl(long int _addressofMat, int modeState, int _colourType, int _thresholdType, int _thresholdValue, int thresholdMaxValue, int segmentationType) : m_MatCamera(*(cv::Mat *) _addressofMat)
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
            m_autoUpdateMatColour();
            break;
//        case MODESTATE::GRAY:
//            m_updateMatGray();
//            break;
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
        case MODESTATE::SEGMENTATION:
            m_updateMatSegmentations(segmentationType);
            break;
        default:
            // ERROR
            break;
    }
}

void CameraAnalysisControl::m_autoUpdateMatColour()
{
    cv::cvtColor(m_MatCamera, m_MatCamera, m_colourType);
}

void CameraAnalysisControl::m_updateMatToGray(cv::Mat &output)
{
    cv::cvtColor(m_MatCamera, output, cv::ColorConversionCodes::COLOR_RGB2GRAY);
}

void CameraAnalysisControl::m_updateMatToColour(cv::Mat &output)
{
    cv::cvtColor(m_MatCamera, output, m_colourType);
}

void CameraAnalysisControl::m_updateMatBinary()
{
    m_updateMatToColour(m_MatCamera);
    cv::threshold(m_MatCamera, m_MatCamera, m_thresholdValue, m_thresholdMaxValue, m_thresholdType);
}

void CameraAnalysisControl::m_updateMatHistoEq()
{
    m_updateMatToGray(m_MatCamera);
    cv::equalizeHist(m_MatCamera, m_MatCamera);
}

void CameraAnalysisControl::m_updateMatInvert()
{
    m_updateMatToColour(m_MatCamera);
    cv::bitwise_not(m_MatCamera, m_MatCamera);
}

void CameraAnalysisControl::m_updateMatSalt()
{
    m_updateMatToColour(m_MatCamera);
    for (int k = 0; k < m_thresholdMaxValue / 3; k++)
    {
        int i = rand() % m_height;
        int j = rand() % m_width;
        m_MatCamera.at<uchar>(j, i) = 255;
    }
}

void CameraAnalysisControl::m_updateMatSegmentations(int segmentationType){
    switch(segmentationType)
    {
        case SEGMENTATIONTYPE::CIRCLES:
            findCircles();
            break;
        case SEGMENTATIONTYPE::RECTANGLES:
            findRectangles();
            break;
        case SEGMENTATIONTYPE::TRIANGLES:
            findTriangles();
            break;
        case SEGMENTATIONTYPE::ALL:
            findCircles();
            findRectangles();
            findTriangles();
            break;
        case SEGMENTATIONTYPE::LARGEST_OBJECT:
            findLargestObject();
            break;
        default:
            // ERROR
            break;
    }
}

void CameraAnalysisControl::findCircles()
{
    cv::Mat hiddenMat;
    m_updateMatToGray(hiddenMat);
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
    m_updateMatToGray(hiddenMat);
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

void CameraAnalysisControl::findTriangles()
{
    cv::Mat hiddenMat;
    m_updateMatToGray(hiddenMat);
    cv::threshold(hiddenMat, hiddenMat, m_thresholdValue, m_thresholdMaxValue, m_thresholdType);
//    cv::Canny(hiddenMat, hiddenMat, 0, 50, 5); // TODO: this method is also useful

    std::vector<std::vector<cv::Point> > contours;
    cv::findContours(hiddenMat.clone(), contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);

    std::vector<cv::Point2i> approx;

    double minimumArea = m_width * m_height / 100;

    for (int i = 0; i < contours.size(); i++)
    {
        cv::approxPolyDP(cv::Mat(contours[i]), approx, cv::arcLength(cv::Mat(contours[i]), true)*0.02, true);

        if (std::fabs(cv::contourArea(contours[i])) < minimumArea || !cv::isContourConvex(approx))
            continue;

        if (approx.size() == 3)
        {
            for(unsigned int j = 0; j < 3; j++)
                cv::line(m_MatCamera, approx[j], approx[(j+1)%3], cv::Scalar(255, 0, 0), 3);
        }
    }
}

void CameraAnalysisControl::findLargestObject()
{
    cv::Mat hiddenMat;
    m_updateMatToGray(hiddenMat);
    cv::threshold(hiddenMat, hiddenMat, m_thresholdValue, m_thresholdMaxValue, m_thresholdType);

    std::vector<std::vector<cv::Point>> contours;
    cv::findContours(hiddenMat.clone(), contours, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE);

    std::vector<cv::Point> maxContour;
    if(contours.size() > 1)
        maxContour = *std::max_element(contours.begin(), contours.end(), helper_functions::compareArea);
    else if(contours.size() == 1)
        maxContour = contours[0];

    /// Draw the largest contour
    int maxContourSize = maxContour.size();
    for(unsigned int j = 0; j < maxContourSize; j++)
        cv::line(m_MatCamera, maxContour[j], maxContour[(j+1)%maxContourSize], cv::Scalar(255, 0, 0), 3);

//    std::vector<int> hullsIndices;
//    std::vector<cv::Vec4i> defects;
//    cv::convexHull(maxContour, hullsIndices);
//    cv::convexityDefects(maxContour, hullsIndices, defects);
//    /// Draw Hulls
//
//
//
//    /// Draw convexityDefects
//    for(const cv::Vec4i& defVec : defects)
//    {
//        float depth = defVec[3] / 256;
//        if (depth > 10) //  filter defects by depth, e.g more than 10
//        {
//            int startidx = defVec[0]; cv::Point ptStart(maxContour[startidx]);
//            int endidx = defVec[1]; cv::Point ptEnd(maxContour[endidx]);
//            int faridx = defVec[2]; cv::Point ptFar(maxContour[faridx]);
//
//            line(m_MatCamera, ptStart, ptEnd, cv::Scalar(0, 255, 0), 2);
//            line(m_MatCamera, ptStart, ptFar, cv::Scalar(0, 255, 0), 2);
//            line(m_MatCamera, ptEnd, ptFar, cv::Scalar(0, 255, 0), 2);
//            circle(m_MatCamera, ptFar, 4, cv::Scalar(0, 255, 0), 3);
//        }
//    }

}
