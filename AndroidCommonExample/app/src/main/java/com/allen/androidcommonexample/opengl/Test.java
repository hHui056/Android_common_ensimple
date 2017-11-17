package com.allen.androidcommonexample.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Admin on 2017/11/17.
 */

public class Test {
    FloatBuffer vertexData;
    float[] ss = {0f, 0f};

    public void test() {
        vertexData = ByteBuffer.allocateDirect(2).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(ss);

        int [] ss = new int[1];

    }
}
