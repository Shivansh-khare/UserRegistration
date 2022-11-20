package com.example.android.roomwordssample

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

data class NewsModel(val status: String, val totalResults: Int, val articles: List<Article>)

@Parcelize
data class Article(
    val title: String?, val description: String?, val publishedAt: Date?,
    val urlToImage: String?, val url: String?, val source: Source?
) : Parcelable
@Parcelize
data class Source(val name: String?): Parcelable