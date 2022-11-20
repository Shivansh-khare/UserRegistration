package com.example.android.roomwordssample.Camera

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.android.roomwordssample.R


class SnapActivity : AppCompatActivity() {

    lateinit var cameraXFragment: CameraXFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)

        val pager = findViewById<View>(R.id.pager) as ViewPager
        pager.adapter = MyPagerAdapter(supportFragmentManager)
    }
}