package com.allen.androidcommonexample.opengl.programs

import android.content.Context
import android.opengl.GLES20.glUseProgram
import com.allen.androidcommonexample.opengl.ShaderHelper
import com.allen.androidcommonexample.opengl.Utils

open class ShaderProgram(context: Context, vertexShaderResourceId: Int, fragmentShaderResourceId: Int) {
    // Shader program
    protected val program: Int = ShaderHelper.buildProgram(Utils.readTextFileFromResource(context, vertexShaderResourceId),
            Utils.readTextFileFromResource(context, fragmentShaderResourceId))

    fun useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program)
    }

    companion object {
        // Uniform constants
        val U_MATRIX = "u_Matrix"
        val U_TEXTURE_UNIT = "u_TextureUnit"

        // Attribute constants
        val A_POSITION = "a_Position"
        val A_COLOR = "a_Color"
        val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
    }
}
