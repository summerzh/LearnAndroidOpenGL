package com.gyt.learnandroidopengl.renderer

import android.opengl.GLSurfaceView
import com.gyt.learnandroidopengl.BaseActivity

/**
 * @author gyt
 * @date on 2019-05-14 13:41
 * @describer 三角形
 */
class Triangle : BaseActivity() {

    override fun getGLSurfaceView(): GLSurfaceView? {
        val glSurfaceView = MyGLSurfaceView(this)

        glSurfaceView.setShapeRenderer(P5_BallRenderer::class.java)
        return glSurfaceView
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContentView(R.layout.activity_triangle)
//
//        glSurfaceView.run {
//            setEGLContextClientVersion(2)
//            // 设置着色器
//            setRenderer(P1_PointRenderer())
//            //设置着色器的着色模式：只有当数据改变时才重新绘制，手动调用requestRender()可以强制重绘
//            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
//        }
//    }

}
