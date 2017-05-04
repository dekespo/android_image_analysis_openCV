#include <jni.h>
#include "CameraAnalysisControl.h"

extern "C"
{
    JNIEXPORT void JNICALL Java_com_sample_dekespo_imageanalysisopencv_CameraUIController_applyCameraAnalysisControl(JNIEnv *env, jobject instance, jlong matAddress, jint modeStatus, jint colourType, jint thresholdType, jint thresholdValue, jint thresholdMaxValue)
    {
        CameraAnalysisControl(matAddress, modeStatus, colourType, thresholdType, thresholdValue, thresholdMaxValue);
    }
}

