package com.gyt.learningandroidopengl.renderer

import android.opengl.GLES20
import com.gyt.learningandroidopengl.utils.ShaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author gyt
 * @date on 2019-05-15 11:36
 * @describer 画直线m
 */
class P2_TriangleRenderer : ShapeRenderer() {

    companion object {
        private val VERTEX_SHADER = """
            attribute vec4 v_Position;
            void main(){
                gl_Position = v_Position;
                gl_PointSize = 30.0;
            }
        """.trimIndent()

        private val FRAGMENT_SHADER = """
            precision mediump float;
            uniform vec4 v_Color;
            void main() {
                gl_FragColor = v_Color;
            }
        """.trimIndent()

        private const val V_COLOR = "v_Color"

        private const val V_POSITION = "v_Position"

        /**
         * 三个顶点的坐标
         */
        private val POINT_DATA = floatArrayOf(
            0.0f, 0.0f,
            0.0f, 0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f
        )

        /**
         * 内容的颜色
         */
        private val SOLID_COLOR = floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)

        /**
         * 直线的颜色
         */
        private val LINE_COLOR = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)

        /**
         * 点的颜色
         */
        private val POINT_COLOR = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)

        private const val BYTE_PER_FLOAT = 4

        private var POINT_TOTAL_NUM = POINT_DATA.size / 2
    }

    private var mProgram: Int = 0

    private var mVPositionHandle: Int = 0

    private var mVColorHandle: Int = 0

    private var mPointCount: Int = 0


    private var mVertexBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(POINT_DATA.size * BYTE_PER_FLOAT)
        .run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(POINT_DATA)
                position(0)
            }
        }


    override fun onDrawFrame(gl: GL10?) {
        println("onDrawFrame")
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glUseProgram(mProgram)

        mVPositionHandle = GLES20.glGetAttribLocation(mProgram, V_POSITION)
        mVColorHandle = GLES20.glGetUniformLocation(mProgram, V_COLOR)

        GLES20.glVertexAttribPointer(
            mVPositionHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            mVertexBuffer

        )

        GLES20.glEnableVertexAttribArray(mVPositionHandle)

        if (mPointCount == POINT_TOTAL_NUM) {
            mPointCount = 1
        } else {
            mPointCount++
        }

        drawLine()
        drawTriangle()
        drawPoint()

//        GLES20.glDisableVertexAttribArray(mVPositionHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        println("onSurfaceChanged")
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        println("onSurfaceCreated")
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

        val vertexShader = ShaderUtil.compileVertexShader(VERTEX_SHADER)
        val fragmentShader = ShaderUtil.compileFragmentShader(FRAGMENT_SHADER)

        mProgram = ShaderUtil.linkProgram(vertexShader, fragmentShader)
    }

    private fun drawPoint() {
        println("pointCount: $mPointCount")
        GLES20.glUniform4fv(mVColorHandle, 1, POINT_COLOR, 0)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, mPointCount)
    }

    private fun drawLine() {
        GLES20.glUniform4fv(mVColorHandle, 1, LINE_COLOR, 0)
        GLES20.glLineWidth(20f)
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, mPointCount)
    }

    private fun drawTriangle() {
        GLES20.glUniform4fv(mVColorHandle, 1, SOLID_COLOR, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, mPointCount)
    }

}