#include <jni.h>
#include <string>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>

extern "C"
{
    void JNICALL Java_com_sample_dekespo_imageanalysisopencv_MainActivity_salt(JNIEnv *env, jobject instance, jlong matAddrGray, jint nbrElem) {
        cv::Mat &mGr = *(cv::Mat *) matAddrGray;
        for (int k = 0; k < nbrElem; k++) {
            int i = rand() % mGr.cols;
            int j = rand() % mGr.rows;
            mGr.at<uchar>(j, i) = 255;
        }
    }

    void JNICALL Java_com_sample_dekespo_imageanalysisopencv_MainActivity_histEq(JNIEnv *env, jobject instance, jlong matAddrGray)
    {
        cv::Mat &mGr = *(cv::Mat *) matAddrGray;
        equalizeHist(mGr, mGr);
    }

    void JNICALL Java_com_sample_dekespo_imageanalysisopencv_MainActivity_invert(JNIEnv *env, jobject instance, jlong matAddrGray)
    {
        cv::Mat &mGr = *(cv::Mat *) matAddrGray;
        bitwise_not(mGr, mGr);
    }

    JNIEXPORT void JNICALL Java_com_sample_dekespo_imageanalysisopencv_MainActivity_rotate90(JNIEnv *env, jobject instance, cv::Mat src, cv::Mat dst, jint angle) {
        if (src.data != dst.data) {
            src.copyTo(dst);
        }

        angle = ((angle / 90) % 4) * 90;

        //0 : flip vertical; 1 flip horizontal
        bool const flip_horizontal_or_vertical = angle > 0 ? 1 : 0;
        int const number = std::abs(angle / 90);

        for (int i = 0; i != number; ++i) {
            cv::transpose(dst, dst);
            cv::flip(dst, dst, flip_horizontal_or_vertical);
        }
    }
}
