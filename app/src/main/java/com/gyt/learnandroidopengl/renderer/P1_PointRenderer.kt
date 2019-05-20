package com.gyt.learnandroidopengl.renderer

import android.opengl.GLES20
import com.gyt.learnandroidopengl.utils.ShaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author gyt
 * @date on 2019-05-14 17:38
 * @describer 绘制最基础的点
 */
class P1_PointRenderer : BaseRenderer() {

    companion object {
        /**
         * 顶点着色器
         */
        private val VERTEX_SHADER = """
            // vec4代表4 x 4矩阵
            attribute vec4 v_Position;
            void main(){
                // gl_Position:GL中默认定义的输出变量，决定当前顶点的最终位置
                gl_Position = v_Position;
                // gl_PointSize: GL中默认定义的输出变量，决定了当前顶点的大小
                gl_PointSize = 40.0;
            }
        """.trimIndent()

        /**
         * 片段着色器
         */
        private val FRAGMENT_SHADER = """
            precision mediump float;
            uniform vec4 v_Color;
            void main(){
                // gl_FragColor: Gl中默认定义的输出变量，决定了当前片段的最终颜色
                gl_FragColor = v_Color;
            }
        """.trimIndent()

        private const val V_COLOR = "v_Color"
        private const val V_POSITION = "v_Position"
        /**
         * 点的x，y坐标(x, y各占一个分量）
         */
        private val POINT_DATA = floatArrayOf(0.0f, 0.0f)
        /**
         * 点向量相关的分量个数，当前只有x，y，所以是2
         */
        private const val POINT_VECTOR_COUNT = 2
        /**
         * Float类型占4Byte
         */
        private const val BYTES_PER_FLOAT = 4

    }

    // 设置颜色，四个参数分别是 red green blue and alpha
    private val color = floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)

    private var mProgram: Int = 0

    /**
     * 顶点坐标在OpenGL程序中句柄
     */
    private var mVPositionHandle = 0

    /**
     * 颜色Uniform在OpenGL程序中的句柄
     */
    private var mVColorHandle = 0

    /**
     * 顶点坐标数据缓冲区
     * 分配一块Native内存，用于与OpenGL数据传递。（我们通常的数据存在于JVM的内存中，1.无法访问硬件；2.会被垃圾回收）
     */
    private val mVertexData: FloatBuffer = ByteBuffer
        // 分配的字节大小 = 顶点坐标分量个数 * Float占的Byte位数
        .allocateDirect(POINT_DATA.size * BYTES_PER_FLOAT)
        .run {
            // 按照本地字节码顺序排序
            order(ByteOrder.nativeOrder())
            // Byte类型转Float类型
            asFloatBuffer().apply {
                // 将JVM中的内存数据拷贝到Native内存中
                put(POINT_DATA)
                // 将缓冲区的指针移动到头部，保证数据是从最开始处读取
                position(0)
            }
        }

    init {
        println("thread name: " + Thread.currentThread().name)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 设置清空屏幕所用的颜色，清除颜色缓冲之后，整个颜色缓冲都会被填充为glClearColor里所设置的颜色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
    }


    /**
     * 注意：GLSurfaceView.Renderer会在单独的现场被调用，而不是在主线程，
     * 所以与OpenGL api调用相关的操作不要放在主线程中使用
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        println("thread name: " + Thread.currentThread().name)
        // 设置窗口，告诉OpenGL渲染窗口的尺寸大小，
        // 函数前两个参数控制窗口左下角的位置，第三个和第四个参数控制渲染窗口的宽度和高度（像素）
        GLES20.glViewport(0, 0, width, height)

        // 编译顶点着色器
        val vertexShader = ShaderUtil.compileVertexShader(VERTEX_SHADER)
        // 编译片段着色器
        val fragmentShader = ShaderUtil.compileFragmentShader(FRAGMENT_SHADER)

        // 将顶点着色器和片段着色器进行链接，组装成一个OpenGL程序
        mProgram = ShaderUtil.linkProgram(vertexShader, fragmentShader)
    }

    override fun onDrawFrame(gl: GL10?) {
        println("onDrawFrame")
        // 清空屏幕的颜色缓冲，在每个新的渲染迭代开始的时候我们总是希望清屏，否则我们仍能看见上一次迭代的渲染结果
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // 通知OpenGL使用该程序
        GLES20.glUseProgram(mProgram)

        // 获取顶点坐标在OpenGL中的句柄
        mVPositionHandle = GLES20.glGetAttribLocation(mProgram, V_POSITION)
        // 获取颜色Uniform在OpenGL中的句柄
        mVColorHandle = GLES20.glGetUniformLocation(mProgram, V_COLOR)


        // OpenGL还不知道它该如何解释内存中的顶点数据，以及它该如何将顶点数据链接到顶点着色器的属性上。我们需要告诉OpenGL怎么做。
        // 将顶点坐标指针指向缓存数据，参数意思如下：
        // 1.顶点坐标句柄
        // 2.每个顶点所关联的分量个数（必须为1，2，3或4）
        // 3.数据类型
        // 4.指定当被访问时，固定点数据值是否应该被归一化(GL_TRUE)或者直接转换为固定点值(GL_FALSE)(只有使用整数数据时)
        // 5.指定连续顶点属性之间的偏移量。如果为0，那么顶点属性会被理解为：它们是紧密排列在一起的。初始值为0。
        // 6.数据缓冲区
        GLES20.glVertexAttribPointer(
            mVPositionHandle,
            POINT_VECTOR_COUNT,
            GLES20.GL_FLOAT,
            false,
            0,
            mVertexData
        )

        // 使用顶点坐标句柄
        GLES20.glEnableVertexAttribArray(mVPositionHandle)
        // 设置颜色
        GLES20.glUniform4fv(mVColorHandle, 1, color, 0)
//        GLES20.glUniform4f(mVColorHandle, 0.0f, 0.0f, 1.0f, 1.0f)

        // 画点
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1)
        // 取消使用顶点坐标句柄
        GLES20.glDisableVertexAttribArray(mVPositionHandle)
    }
}