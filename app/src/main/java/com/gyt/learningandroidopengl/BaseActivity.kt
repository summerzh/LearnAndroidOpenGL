package com.gyt.learningandroidopengl

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
        println("onCreate")
        val glSurfaceView = getGLSurfaceView() ?: throw IllegalArgumentException("Get GlSurfaceView is null!")


        setContentView(glSurfaceView)
    }

//    override fun onResume() {
//        super.onResume()
//        myGLSurfaceView.onResume()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        myGLSurfaceView.onPause()
//    }



    abstract fun getGLSurfaceView(): GLSurfaceView?
}