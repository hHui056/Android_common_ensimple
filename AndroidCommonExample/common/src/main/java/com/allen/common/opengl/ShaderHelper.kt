package com.allen.common.opengl

import android.opengl.GLES20

/**
 * Created by hHui on 2017/11/17.
 */
object ShaderHelper {
    // 编译顶点着色器
    fun compileVertexShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode)
    }

    //编译片段着色器
    fun compileFragmentShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode)
    }


    private fun compileShader(type: Int, shaderCode: String): Int {
        val shaderObjectId = GLES20.glCreateShader(type)  //返回OpenGL对象的引用
        GLES20.glShaderSource(shaderObjectId, shaderCode)
        GLES20.glCompileShader(shaderObjectId)
        return if (isCompileSuccess(shaderObjectId)) {
            shaderObjectId
        } else {
            GLES20.glDeleteShader(shaderObjectId)
            0
        }
    }

    /**
     * 检查是否能成功编译这个着色器
     */
    private fun isCompileSuccess(shaderObjectId: Int): Boolean {
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        return compileStatus[0] != 0
    }


    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programObjectId = GLES20.glCreateProgram()
        // 附上着色器
        GLES20.glAttachShader(programObjectId, vertexShaderId)
        GLES20.glAttachShader(programObjectId, fragmentShaderId)
        // 链接程序
        GLES20.glLinkProgram(programObjectId)
        return if (isLinkProgramSuccess(programObjectId)) {
            programObjectId
        } else {
            GLES20.glDeleteProgram(programObjectId)
            0
        }
    }

    /**
     * 检查连接程序是否成功
     */
    private fun isLinkProgramSuccess(programObjectId: Int): Boolean {
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0)
        return linkStatus[0] != 0
    }

    /**
     * 验证程序对于当前的OpenGL状态是否有效
     */
    fun validateStatus(programObjectId: Int): Boolean {
        GLES20.glValidateProgram(programObjectId)
        val validateStatus = IntArray(1)
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0)
        return validateStatus[0] != 0
    }

}