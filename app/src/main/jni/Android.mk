# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/FANN

LOCAL_MODULE    := hello-jni
LOCAL_SRC_FILES := hello-jni.c FANN/floatfann.c

#LOCAL_CFLAGS    := -O3 -fexceptions -DNDEBUG
#LOCAL_CPPFLAGS  := -O3 -std=c++11 -fexceptions -DNDEBUG

LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)
