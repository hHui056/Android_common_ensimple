package com.allen.androidcommonexample.jni;

/**
 * Created by hHui on 2018/1/31.
 */

public class JNIUtils {
    static {
        System.loadLibrary("JNISample");
    }

    public native String getWrold();

}
