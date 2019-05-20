package com.gyt.learnandroidopengl.utils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * @author gyt
 * @date on 2019-05-20 16:19
 * @describer 创建缓冲区数据工具类
 */
object BufferUtil {

    private const val BYTES_PER_FLOAT = 4

    fun createFloatBuffer(data: FloatArray, position: Int = 0): FloatBuffer =
        ByteBuffer
            .allocateDirect(data.size * BYTES_PER_FLOAT)
            .run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(data)
                    position(0)
                }
            }
}