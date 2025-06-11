//
// Created by Ivotsm on 2017/5/4.
//
#include <jni.h>
#include <stdio.h>
#include "com_iyuba_concept2_util_RecordUtil.h"
#include <android/log.h>
#include <lame.h>
#define LOG_TAG "System.out"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

int flag = 0;

char* Jstring2CStr(JNIEnv* env, jstring jstr) {
    char* rtn = NULL;
    jclass clsstring = (*env)->FindClass(env, "java/lang/String");
    jstring strencode = (*env)->NewStringUTF(env, "GB2312");
    jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes",
            "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) (*env)->CallObjectMethod(env, jstr, mid,
            strencode); // String .getByte("GB2312");
    jsize alen = (*env)->GetArrayLength(env, barr);
    jbyte* ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char*) malloc(alen + 1); //"\0"
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    (*env)->ReleaseByteArrayElements(env, barr, ba, 0); //
    return rtn;
}

JNIEXPORT void Java_com_iyuba_concept2_util_RecordUtil_convertmp3(
        JNIEnv * env, jobject obj, jstring jwav, jstring jmp3) {
    char* cwav = Jstring2CStr(env, jwav);
    char* cmp3 = Jstring2CStr(env, jmp3);
    LOGI("wav = %s", cwav);
    LOGI("mp3 = %s", cmp3);

        //打开 wav,MP3文件
    FILE* fwav = fopen(cwav, "rb");
    FILE* fmp3 = fopen(cmp3, "wb");

    short int wav_buffer[8192 * 2];
    unsigned char mp3_buffer[8192];

    //初始化lame的编码器
    lame_t lame = lame_init();
    //设置lame mp3编码的采样率
    lame_set_in_samplerate(lame, 8000);
    lame_set_num_channels(lame, 2);
    //设置MP3的编码方式
    lame_set_VBR(lame, vbr_default);

    lame_init_params(lame);

    LOGI("lame init finish");
    int read;
    int write; //代表读了多少个次 和写了多少次
    int total = 0; // 当前读的wav文件的byte数目
    do {
        if (flag == 404) {
            return;
        }
        read = fread(wav_buffer, sizeof(short int) * 2, 8192, fwav);
        if (read != 0) {

            write = lame_encode_buffer_interleaved(lame, wav_buffer, read,
                    mp3_buffer, 8192);
            //把转化后的mp3数据写到文件里
            fwrite(mp3_buffer, sizeof(unsigned char), write, fmp3);
        }
        if (read == 0) {
            lame_encode_flush(lame, mp3_buffer, 8192);
        }

    } while (read != 0);
    LOGI("convert  finish");

    lame_close(lame);
    fclose(fwav);
    fclose(fmp3);
}
