package com.example.android.roomwordssample.contacts

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import androidx.room.TypeConverter
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.util.*

public class DatabaseConverters {

    fun age(date: Date) : String{
        return "Age :"+(Calendar.getInstance().time.year - date.year).toString()
    }

    @TypeConverter
    public fun bytetoBitmap(value: ByteArray): Bitmap{
        return BitmapFactory.decodeByteArray(value,0,value.size)
    }

    @TypeConverter
    public fun bitmaptoStr(bitmap:Bitmap): ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,0,stream)
        return stream.toByteArray()
    }


    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

}