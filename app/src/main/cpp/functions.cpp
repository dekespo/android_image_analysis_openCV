#include "functions.h"

namespace functions
{
    void convertGreyMatToBinary(long int addressToPoint)
    {
        cv::Mat &mGr = *(cv::Mat *) addressToPoint;
        cv::threshold(mGr, mGr, 100, 255, CV_THRESH_OTSU);
    }

    void findGeometricShapes(long int addressToPoint)
    {
        cv::Mat &mGr = *(cv::Mat *) addressToPoint;
    }

    void findCircles(long int addressToPoint)
    {
        cv::Mat &mGr = *(cv::Mat *) addressToPoint;
        cv::Mat hiddenImg;
        cv::medianBlur(mGr, hiddenImg, 5);

        std::vector<cv::Vec3f>  circles;

        cv::HoughCircles(hiddenImg, circles, CV_HOUGH_GRADIENT, 1, mGr.rows / 16, 100, 30, 1 ,30);

        for(cv::Vec3i const& circle: circles)
        {
            cv::circle(mGr, cv::Point(circle[0], circle[1]), circle[2], cv::Scalar(0, 0, 255), 3, cv::LINE_AA);
        }
    }

    void findRectangles(long int addressToPoint)
    {
        cv::Mat &mGr = *(cv::Mat *) addressToPoint;
        cv::Mat hiddenImg;
        cv::threshold(mGr, hiddenImg, 160, 255 , 1);

        std::vector<std::vector<cv::Point>> contours;
        cv::findContours(hiddenImg, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);

        for(int i = 0; i < contours.size(); i++)
        {

            if(cv::contourArea(contours[i]) > 3000)
            {
                cv::RotatedRect rotatedRect = cv::minAreaRect(contours[i]);
                cv::Point2f rectPoints[4];
                rotatedRect.points(rectPoints);

                for(unsigned int j = 0; j < 4; j++)
                    cv::line(mGr, rectPoints[j], rectPoints[(j+1)%4], cv::Scalar(0, 255, 0), 3);
            }
        }
    }
}
