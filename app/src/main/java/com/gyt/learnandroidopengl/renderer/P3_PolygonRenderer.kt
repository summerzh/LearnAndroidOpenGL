package com.gyt.learnandroidopengl.renderer

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author gyt
 * @date on 2019-05-16 11:02
 * @describer 多边形
 */
class P3_PolygonRenderer : BaseRenderer() {

    companion object {
        private val VERTEXT_SHADER = """
            attribute vec4 v_Position;
            void main(){
                gl_Position = v_Position;
            }
        """.trimIndent()

        private val FRAGMENT_SHADER = """
            precision mediump float;
            uniform vec4 v_Color;
            void main() {
                gl_FragColor = v_Color;
            }
        """.trimIndent()

        /**
         * 所绘制的多边形顶点的总数
         */
        private const val VERTEX_TOTAL_NUM = 30

        /**
         * 每个点用x，y两个坐标表示
         */
        private const val VERTEX_COOR_NUM = 2

        private const val BYTE_PER_FLOAT = 4

        private const val RADIUS = 0.5f

        private const val V_COLOR = "v_Color"

        private const val V_POSITION = "v_Position"

        private val COLOR = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
    }

    private var vertexCount = 3


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)

        buildProgram(VERTEXT_SHADER, FRAGMENT_SHADER)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
    }


    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)

        val positionHandle = getAttriHandle(V_POSITION)
        val uniformHandle = getUniformHandle(V_COLOR)

        GLES20.glVertexAttribPointer(
            positionHandle,
            VERTEX_COOR_NUM,
            GLES20.GL_FLOAT,
            false,
            0,
            assembleData()
        )

        GLES20.glUniform4fv(uniformHandle, 1, COLOR, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)

        GLES20.glLineWidth(10.0f)
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, vertexCount)

        GLES20.glDisableVertexAttribArray(positionHandle)

        vertexCount++
        if (vertexCount > VERTEX_TOTAL_NUM) vertexCount = 3
    }

    private fun assembleData(): FloatBuffer {
        val pointData = FloatArray(vertexCount * 2)
        // 多边形每个角的度数
        val degree: Int = 360 / vertexCount

        for (i in 0 until vertexCount) {
            // 多边形每个顶点的度数
            val vertexDegree = degree * (i + 1)
            // 由于Math.cos()和Math.sin()参数是弧度而不是度，所以要计算每个顶点的弧度
            val radian = 2 * Math.PI / 360 * vertexDegree

            pointData[i * 2] = (RADIUS * Math.cos(radian)).toFloat()
            pointData[i * 2 + 1] = (RADIUS * Math.sin(radian)).toFloat()
        }

        return ByteBuffer
            .allocateDirect(vertexCount * VERTEX_COOR_NUM * BYTE_PER_FLOAT)
            .run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(pointData)
                    position(0)
                }
            }
    }

}