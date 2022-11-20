package com.example.android.roomwordssample.news

import android.os.Parcelable
import com.example.android.roomwordssample.Article
import kotlinx.parcelize.Parcelize


@Parcelize
class CachedData(var history:MutableList<String>, var bookmark:MutableList<Article>) : Parcelable  {
//    var history = mutableListOf<String>()
//    var bookmark = mutableListOf<Article>()

//    constructor(parcel: Parcel) : this() {
//        parcel.readStringList(history)
////        parcel.readParcelableList(bookmark,ArticleClassLoader)
//    }
//
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//            parcel.writeStringList(history)
//            parcel.writeParcelableList(bookmark,0)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<ChachedData> {
//        override fun createFromParcel(parcel: Parcel): ChachedData {
//            return ChachedData(parcel)
//        }
//
//        override fun newArray(size: Int): Array<ChachedData?> {
//            return arrayOfNulls(size)
//        }
//    }
}
