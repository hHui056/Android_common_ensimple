
package com.allen.androidcommonexample.opengl

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix.*
import com.allen.androidcommonexample.R
import com.allen.androidcommonexample.opengl.bean.Mallet
import com.allen.androidcommonexample.opengl.bean.Table
import com.allen.androidcommonexample.opengl.programs.ColorShaderProgram
import com.allen.androidcommonexample.opengl.programs.TextureShaderProgram
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class AirHockeyRendererPlus(private val context: Context) : Renderer {

    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    private var table: Table? = null
    private var mallet: Mallet? = null

    private var textureProgram: TextureShaderProgram? = null
    private var colorProgram: ColorShaderProgram? = null

    private var texture: Int = 0

    override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        table = Table()
        mallet = Mallet()

        textureProgram = TextureShaderProgram(context)
        colorProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)
    }

    override fun onSurfaceChanged(glUnused: GL10, width: Int, height: Int) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height)

        MatrixHelper.perspectiveM(projectionMatrix, 45f, width.toFloat() / height.toFloat(), 1f, 10f)

        setIdentityM(modelMatrix, 0)
        translateM(modelMatrix, 0, 0f, 0f, -2.5f)
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f)

        val temp = FloatArray(16)
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.size)
    }

    override fun onDrawFrame(glUnused: GL10) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT)

        // Draw the table.
        textureProgram!!.useProgram()
        textureProgram!!.setUniforms(projectionMatrix, texture)
        table!!.bindData(textureProgram)
        table!!.draw()

        // Draw the mallets.
        colorProgram!!.useProgram()
        colorProgram!!.setUniforms(projectionMatrix)
        mallet!!.bindData(colorProgram)
        mallet!!.draw()
    }
}