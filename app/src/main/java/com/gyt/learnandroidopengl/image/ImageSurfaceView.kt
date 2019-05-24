package com.gyt.learnandroidopengl.image

import android.content.Context
import android.opengl.GLSurfaceView

/**
 * @author gyt
 * @date on 2019-05-24 17:21
 * @describer TODO
 */
class ImageSurfaceView(val ctx: Context, val filter: P1_ImageRenderer.Filter) : GLSurfaceView(ctx) {
    private val mImageRenderer: P1_ImageRenderer
    init {
        // 创建OpenGL 2.0 context
        setEGLContextClientVersion(2)
        mImageRenderer = P1_ImageRenderer(ctx, filter)
        setRenderer(mImageRenderer)
        // 设置着色器的着色模式：只有当数据改变时才重新绘制，手动调用requestRender()可以强制重绘
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    fun setFilter(filter: P1_ImageRenderer.Filter){
        mImageRenderer.filter = filter
        requestRender()
    }

}