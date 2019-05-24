package com.gyt.learnandroidopengl.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log

/**
 * @author gyt
 * @date on 2019-05-21 16:58
 * @describer 纹理加载工具类
 */
object TextureUtil {
    private val TAG: String = "TextureUtil"

    data class TextureBean(var width: Int = 0, var height: Int = 0, var textureId: Int = 0)

    /**
     * 根据资源ID获取相应的OpenGL纹理ID，若加载失败则返回0
     * 注意：必须在GL线程调用
     */
    fun createTexture(context: Context, resourceId: Int): TextureBean {
        val bean = TextureBean()
        val textureObjectId = IntArray(1)
        // 创建纹理对象
        GLES20.glGenTextures(1, textureObjectId, 0)
        if (textureObjectId[0] == 0) {
            Log.e(TAG, "Can not create texture")
            return bean
        }

        val options = BitmapFactory.Options()
        // 是否缩放
        options.inScaled = false

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)
        if (bitmap == null) {
            Log.i(TAG, "Can not decode bitmap")
            return bean
        }

        //绑定纹理对象
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectId[0])

        // 多级渐远纹理，解决纹理缩放过程中的锯齿问题。若不设置，则会导致纹理为黑色
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        // 纹理环绕方式
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)

        // 将图片数据生成纹理图像，并且附加纹理图像到已绑定的的纹理对象上
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        // 为当前绑定的纹理自动生成所有需要的多级渐远纹理
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)

        // 生成了纹理和相应的多级渐远纹理后，释放图像的内存并解绑纹理对象
        bean.width = bitmap.width
        bean.height = bitmap.height
        bitmap.recycle()

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        bean.textureId = textureObjectId[0]
        return bean
    }

}