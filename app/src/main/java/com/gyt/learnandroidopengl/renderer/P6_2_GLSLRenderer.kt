package com.gyt.learnandroidopengl.renderer

import android.opengl.GLES20
import com.gyt.learnandroidopengl.utils.BufferUtil
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author gyt
 * @date on 2019-05-20 16:43
 * @describer OpenGL Shading Language 着色器语言练习, 优化数据传递
 */
class P6_2_GLSLRenderer : BaseRenderer() {

    companion object {
        private val VERTEX_SHADER = """
            attribute vec4 a_Position;
            attribute vec4 a_Color;
            varying vec4 v_Color;
            void main() {
                v_Color = a_Color;
                gl_Position = a_Position;
            }
        """.trimIndent()

        private val FRAGMENT_SHADER = """
            precision mediump float;
            varying vec4 v_Color;
            void main() {
                gl_FragColor = v_Color;
            }
        """.trimIndent()

        /**
         * 前来两个分量表示坐标，后三个分量表示rgb
         */
        private val POINT_DATA = floatArrayOf(
            0f, 0.5f, 1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, -0.5f, 0.0f, 0.0f, 1.0f
        )

        private const val POSITION_COMPONENT_COUNT = 2

        private const val COLOR_COMPONENT_COUNT = 3

        private const val BYTES_PER_FLOAT = 4

        private const val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT
    }

    private var mPointData: FloatBuffer

    init {
        mPointData = BufferUtil.createFloatBuffer(POINT_DATA)
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

        // STRIDE：每次读取间隔是 (2个位置 + 3个颜色值) * Float占的Byte位
        GLES20.glVertexAttribPointer(
            positionHandle,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            mPointData
        )

        // 由于前两个分量是位置坐标，所以往后位移两位
        mPointData.position(POSITION_COMPONENT_COUNT)
        GLES20.glVertexAttribPointer(
            colorHandle,
            COLOR_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            mPointData
        )

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(colorHandle)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
    }
}