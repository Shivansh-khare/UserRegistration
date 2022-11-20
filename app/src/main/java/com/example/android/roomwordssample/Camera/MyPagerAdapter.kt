package com.example.android.roomwordssample.Camera

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.android.roomwordssample.R

class MyPagerAdapter(fm:FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int = 4

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> CameraXFragment()
            1 -> QrCodeFragment()
            2 -> CameraXFragment()
            3 -> QrCodeFragment()
            else -> CameraXFragment()
        }
    }
}