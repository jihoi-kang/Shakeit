LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

#opencv library
OPENCVROOT:= /Users/administrator/AndroidStudioProjects/Shakeit/opencv
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}/native/jni/OpenCV.mk


LOCAL_MODULE    := native-lib
LOCAL_SRC_FILES := main.cpp
LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)

APP_STL := c++_shared