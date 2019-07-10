package com.gyt.learnandroidopengl.image

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import com.gyt.learnandroidopengl.R
import kotlinx.android.synthetic.main.activity_image_process.*

/**
 * @author gyt
 * @date on 2019-05-24 17:19
 * @describer 图片处理
 */
class ImageActivity : AppCompatActivity() {
    private val TAG: String = ImageActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_process)
        initView()
    }

    private fun initView() {
        mImageSurfaceView.setFilter(P1_ImageRenderer.Filter.NONE)

        sb_cool.progress = 0
        sb_cool.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var progress: Float = progress.toFloat() / 100.toFloat()
                Log.i(TAG, "process: $progress")
                mImageSurfaceView.setCoolProgress(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        sb_warm.progress = 0
        sb_warm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var progress: Float = progress.toFloat() / 100.toFloat()
                Log.i(TAG, "process: $progress")
                mImageSurfaceView.setWarmProgress(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

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