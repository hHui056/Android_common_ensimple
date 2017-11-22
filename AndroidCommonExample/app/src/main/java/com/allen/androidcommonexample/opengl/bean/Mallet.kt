package com.allen.androidcommonexample.opengl.bean


import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glDrawArrays
import com.allen.androidcommonexample.opengl.data.Constants
import com.allen.androidcommonexample.opengl.data.VertexArray
import com.allen.androidcommonexample.opengl.programs.ColorShaderProgram

/**
 * Create By hHui on 2017/11/22.
 * 木槌模型
 */
class Mallet {
    private val POSITION_COMPONENT_COUNT = 2
    private val COLOR_COMPONENT_COUNT = 3
    private val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT
    private val VERTEX_DATA = floatArrayOf(
            // Order of coordinates: X, Y, R, G, B
            0f, -0.4f, 0f, 0f, 1f,
            0f, 0.4f, 1f, 0f, 0f)
    private val vertexArray: VertexArray = VertexArray(VERTEX_DATA)


    fun bindData(colorProgram: ColorShaderProgram?) {
        vertexArray.setVertexAttribPointer(0, colorProgram!!.positionAttributeLocation, POSITION_COMPONENT_COUNT, STRIDE)
        vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, colorProgram.colorAttributeLocation, COLOR_COMPONENT_COUNT, STRIDE)
    }

    fun draw() {
        glDrawArrays(GL_POINTS, 0, 2)
    }
}
