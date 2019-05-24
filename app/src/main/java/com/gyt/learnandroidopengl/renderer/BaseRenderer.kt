package com.gyt.learnandroidopengl.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.gyt.learnandroidopengl.utils.ShaderUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author gyt
 * @date on 2019-05-15 15:09
 * @describer TODO
 */

abstract class BaseRenderer(val context: Context) : GLSurfaceView.Renderer{
    internal var mProgram: Int = 0

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
    }

    /**
     * 构建OpenGL program
     * @param vertexShader: 顶点着色器
     * @param fragmentShader: 片元着色器
     */
    fun createAndLinkProgram(vertexShader: String, fragmentShader: String){

        val vertexShader = ShaderUtil.compileVertexShader(vertexShader)
        val fragmentShader = ShaderUtil.compileFragmentShader(fragmentShader)

        mProgram  = ShaderUtil.linkProgram(vertexShader, fragmentShader)

        GLES20.glUseProgram(mProgram)
    }

    /**
     * 获取顶点坐标在OpenGL程序中句柄
     */
    fun getAttriHandle(attri: String): Int {
        if(mProgram == 0) throw IllegalAccessException("Please call createAndLinkProgram() first!")

        return GLES20.glGetAttribLocation(mProgram, attri)
    }

    /**
     * 获取颜色Uniform在OpenGL程序中的句柄
     */
    fun getUniformHandle(uniform: String): Int{
        if(mProgram == 0) throw IllegalAccessException("Please call createAndLinkProgram() first!")

        return GLES20.glGetUniformLocation(mProgram, uniform)
    }


}