package com.example.android.roomwordssample.news

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.roomwordssample.ApplicationClass.Companion.cashed
import com.example.android.roomwordssample.databinding.ActivityNewsBookMarksBinding

class NewsBookMarks : AppCompatActivity() {
    lateinit var binding:ActivityNewsBookMarksBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBookMarksBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.RVBookmarks.adapter = NewsAdapter(this)
        binding.RVBookmarks.layoutManager = LinearLayoutManager(this)


        (binding.RVBookmarks.adapter as NewsAdapter).setList(cashed.bookmark)
        (binding.RVBookmarks.adapter as NewsAdapter).isBookmarkPage = true

    }
}