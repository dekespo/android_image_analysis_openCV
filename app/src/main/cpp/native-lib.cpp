#include <jni.h>
#include "functions.h"

extern "C"
{
    JNIEXPORT void JNICALL Java_com_sample_dekespo_imageanalysisopencv_MainActivity_salt(JNIEnv *env, jobject instance, jlong matAddrGray, jint nbrElem) {
        cv::Mat &mGr = *(cv::Mat *) matAddrGray;
        for (int k = 0; k < nbrElem; k++) {
            int i = rand() % mGr.cols;
            int j = rand() % mGr.rows;
            mGr.at<uchar>(j, i) = 255;
        }
    }

    JNIEXPORT void JNICALL Java_com_sample_dekespo_imageanalysisopencv_MainActivity_histEq(JNIEnv *env, jobject instance, jlong matAddrGray)
    {
        cv::Mat &mGr = *(cv::Mat *) matAddrGray;
        equalizeHist(mGr, mGr);
    }

    JNIEXPORT void JNICALL Java_com_sample_dekespo_imageanalysisopencv_MainActivity_invert(JNIEnv *env, jobject instance, jlong matAddrGray)
    {
        cv::Mat &mGr = *(cv::Mat *) matAddrGray;
        bitwise_not(mGr, mGr);
    }

    JNIEXPORT void JNICALL Java_com_sample_dekespo_imageanalysisopencv_MainActivity_binary(JNIEnv *env, jobject instance, jlong matAddrGray)
    {
        functions::convertGreyMatToBinary(matAddrGray);
    }

    JNIEXPORT void JNICALL Java_com_sample_dekespo_imageanalysisopencv_MainActivity_getShapes(JNIEnv *env, jobject instance, jlong matAddrGray)
    {
        //functions::convertGreyMatToBinary(matAddrGray);
        functions::findCircles(matAddrGray);
        functions::findRectangles(matAddrGray);
    }
}
