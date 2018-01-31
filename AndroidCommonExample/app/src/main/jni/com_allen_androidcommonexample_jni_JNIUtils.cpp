#include "com_allen_androidcommonexample_jni_JNIUtils.h"
JNIEXPORT jstring JNICALL Java_com_allen_androidcommonexample_jni_JNIUtils_getWrold(JNIEnv *env,jobject obj)
{

return env -> NewStringUTF("I AM C++");

}