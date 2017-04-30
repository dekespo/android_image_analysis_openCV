# Configure CMake settings
cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_VERBOSE_MAKEFILE on)

# Add libraries
add_library(lib_opencv SHARED IMPORTED)
add_library(native-lib SHARED src/main/cpp/native-lib.cpp src/main/cpp/CameraAnalysisControl.h src/main/cpp/CameraAnalysisControl.cpp)

# Add ANDROID abis to lib_opencv library
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION C:/Users/deniz/Desktop/Android_Apps/opencv-3.2.0-android-sdk/OpenCV-android-sdk/sdk/native/libs/${ANDROID_ABI}/libopencv_java3.so)

# Include the opencv files in the local machine
include_directories(C:/Users/deniz/Desktop/Android_Apps/opencv-3.2.0-android-sdk/OpenCV-android-sdk/sdk/native/jni/include)

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Link all the libraries
target_link_libraries( # Specifies the target library.
                       native-lib

                       # OpenCV lib
                       lib_opencv

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )