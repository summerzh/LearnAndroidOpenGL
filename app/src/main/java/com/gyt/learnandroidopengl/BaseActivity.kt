package com.gyt.learnandroidopengl

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * @author gyt
 * @date on 2019-05-14 13:43
 * @describer TODO
 */

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val glSurfaceView = getGLSurfaceView() ?: throw IllegalArgumentException("Get GlSurfaceView is null!")
        setContentView(glSurfaceView)
    }

    abstract fun getGLSurfaceView(): GLSurfaceView?
}