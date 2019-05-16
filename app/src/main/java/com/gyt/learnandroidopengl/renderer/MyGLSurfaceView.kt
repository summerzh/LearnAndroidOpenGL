package com.gyt.learnandroidopengl.renderer

import android.content.Context
import android.opengl.GLSurfaceView

/**
 * @author gyt
 * @date on 2019-05-15 09:16
 * @describer 自定义GLSurfaceView
 */
class MyGLSurfaceView(context: Context) : GLSurfaceView(context){

    init {
        // 创建OpenGL 2.0 context
        setEGLContextClientVersion(2)
        // 设置着色器
//        setRenderer(`P2_ShapeRenderer`())
//         //设置着色器的着色模式：只有当数据改变时才重新绘制，手动调用requestRender()可以强制重绘
//        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }


    fun <T: BaseRenderer> setShapeRenderer(clazz: Class<T>){

        try {
            val renderer= clazz.newInstance ()
            // 设置着色器
            setRenderer(renderer)
        }catch (e: Exception){
            setRenderer(P1_PointRenderer())
        }

        // 设置着色器的着色模式：只有当数据改变时才重新绘制，手动调用requestRender()可以强制重绘
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        setOnClickListener { requestRender() }
    }

}