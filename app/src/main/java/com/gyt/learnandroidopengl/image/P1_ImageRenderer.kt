package com.gyt.learnandroidopengl.image

import android.content.Context
import android.opengl.GLES20
import com.gyt.learnandroidopengl.R
import com.gyt.learnandroidopengl.renderer.BaseRenderer
import com.gyt.learnandroidopengl.utils.BufferUtil
import com.gyt.learnandroidopengl.utils.ProjectionHelper
import com.gyt.learnandroidopengl.utils.TextureUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author gyt
 * @date on 2019-05-23 17:02
 * @describer 图片处理：冷色调，暖色调，黑白，放大镜效果，马赛克
 */
class P1_ImageRenderer(context: Context) : BaseRenderer(context) {

    companion object {
        private val VERTEX_SHADER = """
            attribute vec4 a_Position;
            attribute vec2 a_TexCoord;
            varying vec2 v_TexCoord;
            uniform mat4 u_Matrix;
            void main() {
                gl_Position = u_Matrix * a_Position;
                v_TexCoord = vec2(a_TexCoord.x, 1.0f - a_TexCoord.y);
            }
        """.trimIndent()

        private val FRAGMENT_SHADER = """
            precision mediump float;
            // 颜色控制变量
            uniform vec3 u_ChangeColor;
            // 图片处理类型
            uniform int u_Type;
            uniform sampler2D u_Texture;
            varying vec2 v_TexCoord;
            const vec2 TexSize = vec2(400.0, 400.0);
            const vec2 mosaicSize = vec2(10.0, 10.0);

            void modifyColor(vec4 color) {
                color.r = max(min(color.r, 1.0), 0.0);
                color.g = max(min(color.g, 1.0), 0.0);
                color.b = max(min(color.b, 1.0), 0.0);
                color.a = max(min(color.a, 1.0), 0.0);
            }

            void main() {
                vec4 v_Color = texture2D(u_Texture, v_TexCoord);
                if(u_Type == 1){
                    float color = v_Color.r * u_ChangeColor.r + v_Color.g * u_ChangeColor.g + v_Color.b * u_ChangeColor.b;
                    gl_FragColor = vec4(color, color, color, v_Color.a);
                }else if(u_Type == 2){
                    vec4 color = v_Color + vec4(u_ChangeColor, 0.0f);
                    modifyColor(color);
                    gl_FragColor = color;
                }else if(u_Type == 3){
                    // 这段代码的关键是借助floor操作符(向下取整)，比如0.0-0.9之间都取0.0，那么长和宽都为mosaicSize的正方形内的纹理坐标就是同一个点的值
                    // 该正方形的颜色就是那个点的颜色，从而实现了马赛克效果
                    vec2 intXY = vec2(v_TexCoord.x * TexSize.x, v_TexCoord.y * TexSize.y);
                    vec2 XYMosaic = vec2(floor(intXY.x / mosaicSize.x) * mosaicSize.x, floor(intXY.y / mosaicSize.y) * mosaicSize.y);
                    vec2 UVMosaic = vec2(XYMosaic.x / TexSize.x, XYMosaic.y / TexSize.y);

                    // 不可以写成这样，因为这个计算结果一直是（0，0）
                    // vec2 xy = vec2(floor(v_TexCoord.x / mosaicSize.x) * mosaicSize.x, floor(v_TexCoord.y / mosaicSize.y) * mosaicSize.y);
                    vec4 color = texture2D(u_Texture, UVMosaic);
                    gl_FragColor = color;
                }else{
                    gl_FragColor = v_Color;
                }
            }
        """.trimIndent()

        private val VERTEX_DATA = floatArrayOf(
            // --  顶点坐标--      -- 颜色 --      --纹理坐标--
            -1f, 1f, 0.0f, /*1.0f, 0.0f, 0.0f,*/ 0.0f, 1.0f,  // 左上
            -1f, -1f, 0.0f, /*0.0f, 1.0f, 0.0f,*/ 0.0f, 0.0f, // 左下
            1f, -1f, 0.0f, /*0.0f, 0.0f, 1.0f,*/ 1.0f, 0.0f,  // 右下
            1f, 1f, 0.0f, /* 1.0f, 0.0f, 0.0f,*/ 1.0f, 1.0f   // 右上
        )

        private val CHANGE_COLOR = floatArrayOf(0.299f, 0.587f, 0.114f)

        private const val POSITION_COMPONENT_COUNT = 3
        private const val TEX_COORD_COMPONENT_COUNT = 2
        private const val BYTES_PER_FLOAT = 4
        private const val STRIDE = (POSITION_COMPONENT_COUNT + TEX_COORD_COMPONENT_COUNT) * BYTES_PER_FLOAT
    }

    private val mVertexBuffer = BufferUtil.createFloatBuffer(VERTEX_DATA)

    private val mChangeColorBuffer = BufferUtil.createFloatBuffer(CHANGE_COLOR)

    private lateinit var mProjectionHelper: ProjectionHelper

    private lateinit var filter: Filter

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
        val changeColorHandle = getUniformHandle("u_ChangeColor")
        val textureHandle = getUniformHandle("u_Texture")
        val typeHandle = getUniformHandle("u_Type")

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

        GLES20.glUniform3fv(changeColorHandle, 1, filter.floatArray, 0)
        GLES20.glUniform1i(typeHandle, filter.type)

        val textureBean = TextureUtil.createTexture(context, R.drawable.tyrion_lannister)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureBean.textureId)
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)

    }

    fun setFilter(filter: Filter) {
        this.filter = filter
    }

    /**
     * 设置冷色调的程度
     */
    fun setCoolProgress(precess: Float) {
        this.filter = Filter.COOL
        filter.floatArray = floatArrayOf(0.0f, 0.0f, precess)
    }

    /**
     * 设置暖色调的程度
     */
    fun setWarmProgress(process: Float) {
        this.filter = Filter.WARM
        filter.floatArray = floatArrayOf(process, process, 0.0f)
    }

    enum class Filter(val type: Int, var floatArray: FloatArray) {
        NONE(0, floatArrayOf(0.0f, 0.0f, 0.0f)),
        GRAY(1, floatArrayOf(0.299f, 0.587f, 0.114f)),
        COOL(2, floatArrayOf(0.0f, 0.0f, 0.1f)),
        WARM(2, floatArrayOf(0.1f, 0.1f, 0.0f)),
        MOSAIC(3, floatArrayOf(0.0f, 0.0f, 0.0f)),
    }
}