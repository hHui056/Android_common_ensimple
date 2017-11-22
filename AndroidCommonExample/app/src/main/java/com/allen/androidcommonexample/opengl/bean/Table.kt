package com.allen.androidcommonexample.opengl.bean


import android.opengl.GLES20.GL_TRIANGLE_FAN
import android.opengl.GLES20.glDrawArrays
import com.allen.androidcommonexample.opengl.data.Constants
import com.allen.androidcommonexample.opengl.data.VertexArray
import com.allen.androidcommonexample.opengl.programs.TextureShaderProgram

/**
 * Create By hHui on 2017/11/22.
 * 桌子模型
 */
class Table {
    private val POSITION_COMPONENT_COUNT = 2
    private val TEXTURE_COORDINATES_COMPONENT_COUNT = 2
    private val STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT

    private val VERTEX_DATA = floatArrayOf(
            // Order of coordinates: X, Y, S, T

            // Triangle Fan
            0f, 0f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.9f,
            0.5f, -0.8f, 1f, 0.9f,
            0.5f, 0.8f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.9f)
    private val vertexArray: VertexArray = VertexArray(VERTEX_DATA)

    fun bindData(textureProgram: TextureShaderProgram?) {
        vertexArray.setVertexAttribPointer(0, textureProgram!!.positionAttributeLocation, POSITION_COMPONENT_COUNT, STRIDE)
        vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, textureProgram.textureCoordinatesAttributeLocation, TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE)
    }

    fun draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
    }

}
