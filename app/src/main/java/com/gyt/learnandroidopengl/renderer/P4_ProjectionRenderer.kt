package com.gyt.learnandroidopengl.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author gyt
 * @date on 2019-05-16 15:22
 * @describer 视图(camera view)和投影
 */
class P4_ProjectionRenderer(context: Context) : P3_PolygonRenderer(context) {

    companion object {
        private val VERTEX_SHADER = """
            // 4 x4 投影矩阵
            uniform mat4 u_Matrix;
            attribute vec4 v_Position;
            //注意u_Matrix一定要在前面，因为矩阵相乘不符合交换律，先右后左
            void main() {
                gl_Position = u_Matrix * v_Position;
            }
        """.trimIndent()

        private const val U_MATRIX = "u_Matrix"
    }

    private var mProjectionMatrixHandle: Int = 0
    /**
     * 4 x4 的投影矩阵
     */
    private var mProjectMatrix = FloatArray(16)

    /**
     * 4 x4 设置camera位置
     */
    private var mViewMatrix = FloatArray(16)

    /**
     * 应用投影和camera效果后最终的矩阵
     */
    private var mVPMatrix = FloatArray(16)

    // 重写该属性
    override val vertexShader: String
        get() = VERTEX_SHADER


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        mProjectionMatrixHandle = getUniformHandle(U_MATRIX)
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)

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