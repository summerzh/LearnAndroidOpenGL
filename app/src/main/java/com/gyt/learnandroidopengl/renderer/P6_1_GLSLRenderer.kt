package com.gyt.learnandroidopengl.renderer

import android.opengl.GLES20
import com.gyt.learnandroidopengl.utils.BufferUtil
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author gyt
 * @date on 2019-05-20 15:43
 * @describer OpenGL Shading Language 着色器语言练习
 */
class P6_1_GLSLRenderer : BaseRenderer() {
    companion object {
        private val VERTEX_SHADER = """
            attribute vec4 a_Position;
            // 表示颜色的顶点属性，attribute关键字表示从应用程序中传递数据到顶点着色器
            // 由于不能直接传递数据到片段着色器，所以先传递数据到顶点着色器
            attribute vec4 a_Color;
            // varying关键字表示，从顶点着色器传递数据到片段着色器，
            varying vec4 v_Color;
            void main() {
                v_Color = a_Color;
                gl_Position = a_Position;
            }
        """.trimIndent()

        private val FRAGMENT_SHADER = """
            precision mediump float;
            // 接收顶点着色器输入的数据
            varying vec4 v_Color;
            void main() {
                gl_FragColor = v_Color;
            }
        """.trimIndent()

        private val POINT_DATA = floatArrayOf(
            0f, 0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f
        )

        private val COLOR_DATA = floatArrayOf(
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f
        )

        private const val POSITION_COMPONENT_COUNT = 2

        private const val COLOR_COMPONENT_COUNT = 3
    }

    private var mPointBuffer: FloatBuffer
    private var mColorBuffer: FloatBuffer

    init {
        mPointBuffer = BufferUtil.createFloatBuffer(POINT_DATA)
        mColorBuffer = BufferUtil.createFloatBuffer(COLOR_DATA)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)

        buildProgram(VERTEX_SHADER, FRAGMENT_SHADER)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)

        val positionHandle = getAttriHandle("a_Position")
        val colorHandle = getAttriHandle("a_Color")

        GLES20.glVertexAttribPointer(
            positionHandle,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            0,
            mPointBuffer
        )

        GLES20.glVertexAttribPointer(
            colorHandle,
            COLOR_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            0,
            mColorBuffer
        )

        GLES20.glEnableVertexAttribArray(colorHandle)
        GLES20.glEnableVertexAttribArray(positionHandle)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
    }
}