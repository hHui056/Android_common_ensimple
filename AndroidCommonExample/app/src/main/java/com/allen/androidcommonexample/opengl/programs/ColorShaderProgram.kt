package com.allen.androidcommonexample.opengl.programs

import android.content.Context
import android.opengl.GLES20.*
import com.allen.androidcommonexample.R

class ColorShaderProgram(context: Context) : ShaderProgram(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader) {
    // Uniform locations
    private val uMatrixLocation: Int = glGetUniformLocation(program, ShaderProgram.U_MATRIX)
    // Attribute locations
    val positionAttributeLocation: Int = glGetAttribLocation(program, ShaderProgram.A_POSITION)
    val colorAttributeLocation: Int = glGetAttribLocation(program, ShaderProgram.A_COLOR)
    fun setUniforms(matrix: FloatArray) {
        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }
}
