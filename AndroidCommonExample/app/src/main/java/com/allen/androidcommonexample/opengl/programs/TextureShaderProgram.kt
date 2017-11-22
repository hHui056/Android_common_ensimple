package com.allen.androidcommonexample.opengl.programs

import android.content.Context
import android.opengl.GLES20.*
import com.allen.androidcommonexample.R

class TextureShaderProgram(context: Context) : ShaderProgram(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader) {
    private val uMatrixLocation: Int = glGetUniformLocation(program, ShaderProgram.Companion.U_MATRIX)
    private val uTextureUnitLocation: Int = glGetUniformLocation(program, ShaderProgram.Companion.U_TEXTURE_UNIT)
    val positionAttributeLocation: Int = glGetAttribLocation(program, ShaderProgram.Companion.A_POSITION)
    val textureCoordinatesAttributeLocation: Int = glGetAttribLocation(program, ShaderProgram.Companion.A_TEXTURE_COORDINATES)


    fun setUniforms(matrix: FloatArray, textureId: Int) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        glUniform1i(uTextureUnitLocation, 0)
    }
}
