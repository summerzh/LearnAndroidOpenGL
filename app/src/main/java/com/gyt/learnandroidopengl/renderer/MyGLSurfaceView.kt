package com.gyt.learnandroidopengl.renderer

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log

/**
 * @author gyt
 * @date on 2019-05-15 09:16
 * @describer 自定义GLSurfaceView
 */
class MyGLSurfaceView(private val cxt: Context) : GLSurfaceView(cxt){
    private val TAG: String = "MyGLSurfaceView"

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
            val constructor = clazz.getConstructor(Context::class.java)
            val renderer= constructor.newInstance(cxt)
            // 设置着色器
            setRenderer(renderer)
        }catch (e: Exception){
            Log.i(TAG, e.toString())
            setRenderer(P1_PointRenderer(cxt))
        }

        // 设置着色器的着色模式：只有当数据改变时才重新绘制，手动调用requestRender()可以强制重绘
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        setOnClickListener { requestRender() }
    }

}