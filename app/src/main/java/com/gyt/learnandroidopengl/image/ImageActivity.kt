package com.gyt.learnandroidopengl.image

import android.opengl.GLSurfaceView
import android.view.Menu
import android.view.MenuItem
import com.gyt.learnandroidopengl.BaseActivity
import com.gyt.learnandroidopengl.R

/**
 * @author gyt
 * @date on 2019-05-24 17:19
 * @describer 图片处理
 */
class ImageActivity : BaseActivity() {
    private lateinit var mImageSurfaceView: ImageSurfaceView

    override fun getGLSurfaceView(): GLSurfaceView? {
        mImageSurfaceView = ImageSurfaceView(this, P1_ImageRenderer.Filter.NONE)
        return mImageSurfaceView
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_image_filter, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) return false
        when (item.itemId) {
            R.id.action_none -> mImageSurfaceView.setFilter(P1_ImageRenderer.Filter.NONE)
            R.id.action_gray -> mImageSurfaceView.setFilter(P1_ImageRenderer.Filter.GRAY)
            R.id.action_cool -> mImageSurfaceView.setFilter(P1_ImageRenderer.Filter.COOL)
            R.id.action_warm -> mImageSurfaceView.setFilter(P1_ImageRenderer.Filter.WARM)
            R.id.action_mosaic -> mImageSurfaceView.setFilter(P1_ImageRenderer.Filter.MOSAIC)
        }
        return true
    }
}