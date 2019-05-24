package com.gyt.learnandroidopengl.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author gyt
 * @date on 2019-05-17 16:02
 * @describer 绘制球
 */
class P5_BallRenderer(context: Context) : BaseRenderer(context) {

    companion object {
        private val VERTEX_SHADER = """
            // 4 x4 投影矩阵
            uniform mat4 u_Matrix;
            attribute vec4 a_Position;
            //注意u_Matrix一定要在前面，因为矩阵相乘不符合交换律，先右后左
            void main() {
                gl_Position = u_Matrix * a_Position;
                gl_PointSize = 10.0;
            }
        """.trimIndent()

        private val FRAGMENT_SHADER = """
            precision mediump float;
            uniform vec4 u_Color;
            void main() {
                gl_FragColor = u_Color;
            }
        """.trimIndent()


        private const val U_MATRIX = "u_Matrix"

        private const val U_COLOR = "u_Color"

        private const val A_POSITION = "a_Position"

        private const val RADIUS = 0.5F

        private const val BYTE_PER_FLOAT = 4

        private val COLOR = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
    }

    private val pointData = FloatArray(180 * 180 * 3)

    private var mAPositionHandle = 0

    private var mUMatrixHandle = 0

    private var mUColorHandle = 0

    private var mProjectionMatrix = FloatArray(16)


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)

        createAndLinkProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        mUMatrixHandle = getUniformHandle(U_MATRIX)
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
            Matrix.orthoM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 2.9f, 10f)
        else
            Matrix.orthoM(mProjectionMatrix, 0, -1.0f, 1.0f, -ratio, ratio, 2.9f, 10f)

        GLES20.glUniformMatrix4fv(mUMatrixHandle, 1, false, mProjectionMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)

        mAPositionHandle= getAttriHandle(A_POSITION)
        mUColorHandle = getUniformHandle(U_COLOR)


        val pointBuffer = assemblePointData()

        GLES20.glVertexAttribPointer(
            mAPositionHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            pointBuffer
        )

        GLES20.glEnableVertexAttribArray(mAPositionHandle)
        GLES20.glUniform4fv(mUColorHandle, 1, COLOR, 0)

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 180 * 180)
        GLES20.glDisableVertexAttribArray(mAPositionHandle)
    }

    private fun assemblePointData(): FloatBuffer{
        for(i in 0 until 180){
            val radianX = 2 * Math.PI / 360 * (i + 1) * 2
            for(j in 0 until 180){
                val radianZ = 2 * Math.PI / 360 * (j + 1) * 2
                pointData[i * 180 + j] = (RADIUS * Math.cos(radianX) * Math.sin(radianZ)).toFloat()
                pointData[i * 180 + j + 1] = (RADIUS * Math.sin(radianX) * Math.cos(radianZ)).toFloat()
                pointData[i * 180 + j + 2] = (RADIUS * Math.cos(radianZ)).toFloat()

                println("x: ${pointData[i * 180 + j]}, y: ${pointData[i * 180 + j + 1]}, z: ${pointData[i * 180 + j + 2]}")
            }
        }

        return ByteBuffer
            .allocateDirect(pointData.size * BYTE_PER_FLOAT)
            .run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(pointData)
                    position(0)
                }
            }
    }
}