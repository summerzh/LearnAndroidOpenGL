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
 * @date on 2019-05-22 11:35
 * @describer 多纹理绘制
 */
class P7_2_TextureRenderer(context: Context) : BaseRenderer(context) {
    companion object {
        private val VERTEX_SHADER = """
            attribute vec4 a_Position;
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
            uniform sampler2D u_Texture;
            varying vec2 v_TexCoord;
            void main() {
                gl_FragColor = texture2D(u_Texture, v_TexCoord);
            }
        """.trimIndent()

        private val VERTEX_DATA1 = floatArrayOf(
            // --  顶点坐标--      -- 颜色 --      --纹理坐标--
            -1f, 1f, 0.0f, /*1.0f, 0.0f, 0.0f,*/ 0.0f, 1.0f,  // 左上
            -1f, -1f, 0.0f, /*0.0f, 1.0f, 0.0f,*/ 0.0f, 0.0f, // 左下
            1f, -1f, 0.0f, /*0.0f, 0.0f, 1.0f,*/ 1.0f, 0.0f,  // 右下
            1f, 1f, 0.0f, /* 1.0f, 0.0f, 0.0f,*/ 1.0f, 1.0f   // 右上
        )

        private val VERTEX_DATA2 = floatArrayOf(
            // --  顶点坐标--      -- 颜色 --      --纹理坐标--
            0.6f, 0.95f, 0.0f, /*1.0f, 0.0f, 0.0f,*/ 0.0f, 1.0f,
            0.6f, 0.8f, 0.0f, /*0.0f, 1.0f, 0.0f,*/ 0.0f, 0.0f,
            0.95f, 0.8f, 0.0f, /*0.0f, 0.0f, 1.0f,*/ 1.0f, 0.0f,
            0.95f, 0.95f, 0.0f, /* 1.0f, 0.0f, 0.0f,*/ 1.0f, 1.0f
        )


        private const val POSITION_COMPONENT_COUNT = 3
        private const val COLOR_COMPONENT_COUNT = 3
        private const val TEX_COORD_COMPONENT_COUNT = 2
        private const val BYTES_PER_FLOAT = 4
        private const val STRIDE = (POSITION_COMPONENT_COUNT + TEX_COORD_COMPONENT_COUNT) * BYTES_PER_FLOAT
    }

    private var mActorVertexBuffer: FloatBuffer
    private var mLogoVertexBuffer: FloatBuffer

    private lateinit var mProjectionHelper: ProjectionHelper
    private var mPositionHandle: Int = 0
    private var mTextureHandle: Int = 0

    init {
        mActorVertexBuffer = BufferUtil.createFloatBuffer(VERTEX_DATA1)
        mLogoVertexBuffer = BufferUtil.createFloatBuffer(VERTEX_DATA2)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)

        createAndLinkProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        mProjectionHelper = ProjectionHelper(mProgram, "u_Matrix")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)

        mProjectionHelper.enable(width, height)

        mPositionHandle = getAttriHandle("a_Position")
        val texCoordHandle = getAttriHandle("a_TexCoord")
        mTextureHandle = getUniformHandle("u_Texture")

        mActorVertexBuffer.position(POSITION_COMPONENT_COUNT)
        // 两个纹理图片的纹理坐标一样
        GLES20.glVertexAttribPointer(
            texCoordHandle,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            mActorVertexBuffer
        )

        GLES20.glEnableVertexAttribArray(texCoordHandle)
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)

        drawActor()
        drawLogo()
    }

    private fun drawLogo() {
        mLogoVertexBuffer.position(POSITION_COMPONENT_COUNT)
        // 纹理图片的顶点坐标不同
        GLES20.glVertexAttribPointer(
            mPositionHandle,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            mLogoVertexBuffer
        )
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        val logoTextureBean = TextureUtil.createTexture(context, R.drawable.game_of_thrones_logo)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, logoTextureBean.textureId)
        GLES20.glUniform1i(mTextureHandle, 1)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, VERTEX_DATA2.size / (POSITION_COMPONENT_COUNT + TEX_COORD_COMPONENT_COUNT))
    }

    private fun drawActor() {
        mActorVertexBuffer.position(POSITION_COMPONENT_COUNT)
        GLES20.glVertexAttribPointer(
            mPositionHandle,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            mActorVertexBuffer
        )
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        val actorTextureBean = TextureUtil.createTexture(context, R.drawable.tyrion_lannister)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, actorTextureBean.textureId)
        GLES20.glUniform1i(mTextureHandle, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, VERTEX_DATA1.size / (POSITION_COMPONENT_COUNT + TEX_COORD_COMPONENT_COUNT))
    }
}