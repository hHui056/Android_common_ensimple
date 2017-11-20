package com.allen.androidcommonexample.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.allen.androidcommonexample.R
import com.allen.common.opengl.ShaderHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by hHui 2017/11/17.
 *
 * 模仿 空气曲棍球游戏
 */
class AirHockeyRenderer(context: Context) : GLSurfaceView.Renderer {
    val U_COLOR = "u_Color"
    var uColorLocation = 0
    val A_POSITION = "a_Position"
    var aPositionLocation = 0

    val tableVerticesWithTriangles = floatArrayOf(
            //triangle1
            -0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,
            //triangle2
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,
            // line
            -0.5f, 0f,
            0.5f, 0f,
            //mallets
            0f, -0.25f,
            0f, 0.25f,
            // ball
            0f, 0f)

    var vertexData: FloatBuffer

    var vertexShaderStr = ""
    var fragmentShaderStr = ""
    var program = 0

    init {
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.size * 4) // 分配一块内存区域供OpenGL读取避免被回收
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertexData.put(tableVerticesWithTriangles)
        vertexShaderStr = Utils.readTextFileFromResource(context, R.raw.simple_vertex_shader)
        fragmentShaderStr = Utils.readTextFileFromResource(context, R.raw.simple_fragment_shader)
    }


    override fun onDrawFrame(gl: GL10?) { // 绘制一帧时调用
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        // 绘制桌子
        GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)
        // 绘制分割线
        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2)
        // 绘制点
        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1)
        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1)
        //绘制ball
        GLES20.glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 10, 1)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) { // surface尺寸发生变化
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) { // surface被创建时调用此方法
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        val vertexShaderInt = ShaderHelper.compileVertexShader(vertexShaderStr)
        val fragmentShaderInt = ShaderHelper.compileFragmentShader(fragmentShaderStr)
        program = ShaderHelper.linkProgram(vertexShaderInt, fragmentShaderInt)
        GLES20.glUseProgram(program)

        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR)
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION)
        vertexData.position(0)
        GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 0, vertexData)
        GLES20.glEnableVertexAttribArray(aPositionLocation)
    }

}