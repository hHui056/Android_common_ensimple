package com.airhockey.android

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix.*
import com.allen.androidcommonexample.R
import com.allen.androidcommonexample.opengl.MatrixHelper
import com.allen.androidcommonexample.opengl.ShaderHelper
import com.allen.androidcommonexample.opengl.Utils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AirHockeyRenderer(private val context: Context) : Renderer {

    private val vertexData: FloatBuffer
    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private var program = 0
    private var uMatrixLocation = 0
    private var aPositionLocation = 0
    private var aColorLocation = 0

    init {

        val tableVerticesWithTriangles = floatArrayOf(
                // Order of coordinates: X, Y, R, G, B

                // Triangle Fan
                0f, 0f, 1f, 1f, 1f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

                // Line 1
                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 1f, 0f, 0f,

                // Mallets
                0f, -0.4f, 0f, 0f, 1f,
                0f, 0.4f, 1f, 0f, 0f)

        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()

        vertexData.put(tableVerticesWithTriangles)
    }

    override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        val vertexShaderSource = Utils.readTextFileFromResource(context, R.raw.simple_vertex_shader)
        val fragmentShaderSource = Utils.readTextFileFromResource(context, R.raw.simple_fragment_shader)

        val vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource)

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader)
        glUseProgram(program)
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX)
        aPositionLocation = glGetAttribLocation(program, A_POSITION)
        aColorLocation = glGetAttribLocation(program, A_COLOR)
        vertexData.position(0)
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData)

        glEnableVertexAttribArray(aPositionLocation)
        vertexData.position(POSITION_COMPONENT_COUNT)
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData)

        glEnableVertexAttribArray(aColorLocation)
    }

    override fun onSurfaceChanged(glUnused: GL10, width: Int, height: Int) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height)

        MatrixHelper.perspectiveM(projectionMatrix, 45f, width.toFloat() / height.toFloat(), 1f, 10f)
        setIdentityM(modelMatrix, 0)  // 设置modelMatrix为单位矩阵

        translateM(modelMatrix, 0, 0f, 0f, -2.5f) // 增加平移矩阵
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f) // 增加旋转矩阵 ----> 沿(1,0,0)向量旋转-60°

        val temp = FloatArray(16)
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0) // projectionMatrix * modelMatrix ----> temp
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.size) // temp ----> projectionMatrix
    }

    override fun onDrawFrame(glUnused: GL10) {
        glClear(GL_COLOR_BUFFER_BIT)

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0) // 应用矩阵

        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
        glDrawArrays(GL_LINES, 6, 2)
        glDrawArrays(GL_POINTS, 8, 1)
        glDrawArrays(GL_POINTS, 9, 1)
    }

    companion object {
        private val U_MATRIX = "u_Matrix"
        private val A_POSITION = "a_Position"
        private val A_COLOR = "a_Color"
        private val POSITION_COMPONENT_COUNT = 2
        private val COLOR_COMPONENT_COUNT = 3
        private val BYTES_PER_FLOAT = 4
        private val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT
    }
}