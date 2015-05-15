LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS := -llog

LOCAL_MODULE    := ndksetup
LOCAL_SRC_FILES := Inode_pid_pname_map.c

include $(BUILD_SHARED_LIBRARY)  