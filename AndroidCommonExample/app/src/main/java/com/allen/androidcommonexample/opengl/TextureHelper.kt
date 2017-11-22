package com.allen.androidcommonexample.opengl

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLUtils.texImage2D


/**
 * Created by hHui on 2017/11/20.
 */
object TextureHelper {
    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureObjectIds = IntArray(1)
        glGenTextures(1, textureObjectIds, 0)
        if (textureObjectIds[0] == 0) {
            return 0
        }
        val options = BitmapFactory.Options()
        options.inScaled = false
        // Read in the resource
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

        if (bitmap == null) {
            glDeleteTextures(1, textureObjectIds, 0)
            return 0
        }
        // Bind to the texture in OpenGL
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0])
        // Set filtering: a default must be set, or the texture will be
        // black.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        // Load the bitmap into the bound texture.
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
        // Note: Following code may cause an error to be reported in the
        // ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
        // Failed to generate texture mipmap levels (error=3)
        // No OpenGL error will be encountered (glGetError() will return
        // 0). If this happens, just squash the source image to be
        // square. It will look the same because of texture coordinates,
        // and mipmap generation will work.
        glGenerateMipmap(GL_TEXTURE_2D)
        // Recycle the bitmap, since its data has been loaded into
        // OpenGL.
        bitmap.recycle()
        // Unbind from the texture.
        glBindTexture(GL_TEXTURE_2D, 0)
        return textureObjectIds[0]
    }
}