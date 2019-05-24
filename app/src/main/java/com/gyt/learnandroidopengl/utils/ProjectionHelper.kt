package com.gyt.learnandroidopengl.utils

import android.opengl.GLES20
import android.opengl.Matrix

/**
 * @author gyt
 * @date on 2019-05-22 10:15
 * @describer 投影工具类
 */
class ProjectionHelper(program: Int, name: String) {

    private val mProjectionMatrixHandle: Int = GLES20.glGetUniformLocation(program, name)

    private val mProjectMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mVPMatrix = FloatArray(16)

    fun enable(width: Int, height: Int){
        var ratio = if (width > height)
            width.toFloat() / height.toFloat()
        else
            height.toFloat() / width.toFloat()

        // 为了解决之前的图像拉伸问题，就是要保证近平面的宽高比和视口的宽高比一致，而且是以较短的那一边作为 1 的标准，让图像保持居中。
        // 所以当height是短边时，上下平面设置为1，左右平面设置为ratio
        if(width > height)
        // 正交投影
            Matrix.orthoM(mProjectMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 2.9f, 10f)
        else
            Matrix.orthoM(mProjectMatrix, 0, -1.0f, 1.0f, -ratio, ratio, 2.9f, 10f)

        // 透视投影
//       Matrix.frustumM(mProjectMatrix, 0, -1.0f, 1.0f, -ratio, ratio, 2.9f, 20f)

        // 注意：eyez > near, far > near, near and far > 0
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        // 矩阵运算中先执行视图矩阵再进行投影矩阵，所以投影矩阵在左，视图矩阵在右
        Matrix.multiplyMM(mVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)

        GLES20.glUniformMatrix4fv(mProjectionMatrixHandle, 1, false, mVPMatrix, 0)
    }




}