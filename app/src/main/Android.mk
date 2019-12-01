#
# Copyright (C) 2016 The Android Open Source Project
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

ifneq ($(TARGET_BUILD_PDK), true)

LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-java-files-under, java)

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res

LOCAL_PACKAGE_NAME := speedlock
LOCAL_PRIVATE_PLATFORM_APIS := true

LOCAL_CERTIFICATE := platform

LOCAL_MODULE_TAGS := optional

LOCAL_JAVA_LIBRARIES += android.car

LOCAL_STATIC_JAVA_LIBRARIES += \
    mobica-speedlock-retrofit \
    mobica-speedlock-okhttp3 \
    mobica-speedlock-okio \
    mobica-speedlock-gson \
    mobica-speedlock-converter-gson \
    mobica-speedlock-interceptor \
    mobica-speedlock-glide \
    mobica-speedlock-glide-gifcoder \
    mobica-speedlock-glide-compiler \
    mobica-speedlock-glide-lrucache \
    mobica-speedlock-glide-annotation \
    mobica-speedlock-guava \
    mobica-speedlock-javapoet

LOCAL_STATIC_JAVA_LIBRARIES += jsr305 \
    android.support.car \
    androidx-constraintlayout_constraintlayout-solver \
    androidx.annotation_annotation

LOCAL_STATIC_ANDROID_LIBRARIES += \
    androidx.appcompat_appcompat \
    androidx-constraintlayout_constraintlayout \
    androidx.recyclerview_recyclerview \
    android-support-v4

LOCAL_USE_AAPT2 := true

LOCAL_PROGUARD_ENABLED := disabled

LOCAL_DEX_PREOPT := false

include $(BUILD_PACKAGE)

###################################### BUILD_HOST_PREBUILT ###################################################
include $(CLEAR_VARS)

COMMON_LIBS_PATH := ../../../../../../../prebuilts/tools/common/m2/repository
MAVEN_REPO_PATH := ../../../../../../../prebuilts/maven_repo
LOCAL_LIBS_PATH := ../../libs

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
        mobica-speedlock-glide:$(MAVEN_REPO_PATH)/bumptech/com/github/bumptech/glide/glide/SNAPSHOT/glide-SNAPSHOT.jar \
        mobica-speedlock-glide-gifcoder:$(MAVEN_REPO_PATH)/bumptech/com/github/bumptech/glide/gifdecoder/SNAPSHOT/gifdecoder-SNAPSHOT.jar \
        mobica-speedlock-glide-compiler:$(MAVEN_REPO_PATH)/bumptech/com/github/bumptech/glide/compiler/SNAPSHOT/compiler-SNAPSHOT.jar \
        mobica-speedlock-glide-lrucache:$(MAVEN_REPO_PATH)/bumptech/com/github/bumptech/glide/disklrucache/SNAPSHOT/disklrucache-SNAPSHOT.jar \
        mobica-speedlock-glide-annotation:$(MAVEN_REPO_PATH)/bumptech/com/github/bumptech/glide/annotation/SNAPSHOT/annotation-SNAPSHOT.jar \
        mobica-speedlock-guava:$(COMMON_LIBS_PATH)/com/google/guava/guava/23.0/guava-23.0.jar \
        mobica-speedlock-javapoet:$(COMMON_LIBS_PATH)/com/squareup/javapoet/1.8.0/javapoet-1.8.0.jar \
        mobica-speedlock-retrofit:$(COMMON_LIBS_PATH)/com/squareup/retrofit2/retrofit/2.1.0/retrofit-2.1.0.jar \
        mobica-speedlock-okhttp3:$(COMMON_LIBS_PATH)/com/squareup/okhttp3/okhttp/3.4.1/okhttp-3.4.1.jar \
        mobica-speedlock-okio:$(COMMON_LIBS_PATH)/com/squareup/okio/okio/1.9.0/okio-1.9.0.jar \
        mobica-speedlock-gson:$(COMMON_LIBS_PATH)/com/google/code/gson/gson/2.8.0/gson-2.8.0.jar \
        mobica-speedlock-converter-gson:$(LOCAL_LIBS_PATH)/converter-gson-2.1.0.jar \
        mobica-speedlock-interceptor:$(LOCAL_LIBS_PATH)/logging-interceptor-3.4.1.jar 


include $(BUILD_MULTI_PREBUILT)

endif
