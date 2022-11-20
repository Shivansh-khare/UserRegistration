package com.example.android.roomwordssample.news

import com.example.android.roomwordssample.NewsModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface HeadlineAPI{
    @GET("/v2/top-headlines?country=In&apiKey=38f092e004734dedad9354f21cf7f48a")
    suspend fun getHeadlineNews(): Response<NewsModel>
}

object RetrofitHeadline {

    val baseUrl = "https://newsapi.org/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            // we need to add converter factory to
            // convert JSON object to Java object
            .build()
    }
}

interface NewsAPI{
    @GET("/v2/everything")
    suspend fun getNews(
        @Query("q")q:String,
        @Query("sortBy")sortBy:String,
        @Query("apiKey")apiKey:String,
        @Query("pageSize")pageSize:Int,
        @Query("page")page:Int
    ): Response<NewsModel>
}

object RetrofitHelper {

    val baseUrl = "https://newsapi.org/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            // we need to add converter factory to
            // convert JSON object to Java object
            .build()
    }
}