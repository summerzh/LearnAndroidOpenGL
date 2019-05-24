package com.gyt.learnandroidopengl.renderer

import android.content.Context
import android.opengl.GLES20
import com.gyt.learnandroidopengl.R
import com.gyt.learnandroidopengl.utils.BufferUtil
import com.gyt.learnandroidopengl.utils.ProjectionHelper
import com.gyt.learnandroidopengl.utils.TextureUtil
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author gyt
 * @date on 2019-05-20 17:46
 * @describer 纹理
 */
class P7_1_TextureRenderer(context: Context) : BaseRenderer(context) {
    companion object {
        private val VERTEX_SHADER = """
            attribute vec4 a_Position;
            // 纹理坐标
            attribute vec2 a_TexCoord;
            uniform mat4 u_Matrix;
            varying vec2 v_TexCoord;
            void main() {
                // 翻转y轴，否则图片会上下颠倒
                v_TexCoord = vec2(a_TexCoord.x, 1.0f - a_TexCoord.y);
                gl_Position = u_Matrix * a_Position;
            }
        """.trimIndent()

        private val FRAGMENT_SHADER = """
            precision mediump float;
            // 采样器
            uniform sampler2D u_Texture;
            varying vec2 v_TexCoord;
            void main() {
                gl_FragColor = texture2D(u_Texture, v_TexCoord);
            }
        """.trimIndent()

        private val VERTEX_DATA = floatArrayOf(
            // --  顶点坐标--      -- 颜色 --      --纹理坐标--
            -0.5f, 0.5f, 0.0f, /*1.0f, 0.0f, 0.0f,*/ 0.0f, 1.0f,  // 左上
            -0.5f, -0.5f, 0.0f, /*0.0f, 1.0f, 0.0f,*/ 0.0f, 0.0f, // 左下
            0.5f, -0.5f, 0.0f, /*0.0f, 0.0f, 1.0f,*/ 1.0f, 0.0f,  // 右下
            0.5f, 0.5f, 0.0f, /* 1.0f, 0.0f, 0.0f,*/ 1.0f, 1.0f   // 右上
        )


        private const val POSITION_COMPONENT_COUNT = 3
        private const val COLOR_COMPONENT_COUNT = 3
        private const val TEX_COORD_COMPONENT_COUNT = 2
        private const val BYTES_PER_FLOAT = 4
        private const val STRIDE = (POSITION_COMPONENT_COUNT + TEX_COORD_COMPONENT_COUNT) * BYTES_PER_FLOAT
    }

    private var mVertexBuffer: FloatBuffer

    private lateinit var mProjectionHelper: ProjectionHelper

    init {
        mVertexBuffer = BufferUtil.createFloatBuffer(VERTEX_DATA)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)

        createAndLinkProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        mProjectionHelper = ProjectionHelper(mProgram, "u_Matrix")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)

        mProjectionHelper.enable(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)

        val positionHandle = getAttriHandle("a_Position")
        val texCoordHandle = getAttriHandle("a_TexCoord")
        val textureHandle = getUniformHandle("u_Texture")

        mVertexBuffer.position(0)
        GLES20.glVertexAttribPointer(
            positionHandle,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            mVertexBuffer
        )

        mVertexBuffer.position(POSITION_COMPONENT_COUNT)
        GLES20.glVertexAttribPointer(
            texCoordHandle,
            TEX_COORD_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            mVertexBuffer
        )

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(texCoordHandle)

        val textureBean = TextureUtil.createTexture(context, R.drawable.tyrion_lannister)

        // 我们可以给纹理采样器分配一个位置值，这样的话我们能够在一个片段着色器中设置多个纹理。
        // 一个纹理的位置值通常称为一个纹理单元(Texture Unit)。一个纹理的默认纹理单元是0，它是默认的激活纹理单元
        // 纹理单元的主要目的是让我们在着色器中可以使用多于一个的纹理
        // 在绑定纹理之前先激活纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        // 绑定纹理图片到纹理单元上
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureBean.textureId)
        // 设置uniform采样器的位置值，保证每个uniform采样器对应着正确的纹理单元
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, VERTEX_DATA.size / (POSITION_COMPONENT_COUNT + TEX_COORD_COMPONENT_COUNT))

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }
}