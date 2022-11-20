/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.roomwordssample.contacts

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * A basic class representing an entity that is a row in a one-column database table.
 *
 * @ Entity - You must annotate the class as an entity and supply a table name if not class name.
 * @ PrimaryKey - You must identify the primary key.
 * @ ColumnInfo - You must supply the column name if it is different from the variable name.
 *
 * See the documentation for the full rich set of annotations.
 * https://developer.android.com/topic/libraries/architecture/room.html
 */

@Parcelize
@Entity(tableName = "word_table")
public data class User(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "Fname") var Fname: String,
    @ColumnInfo(name = "Lname") var Lname: String,
    @ColumnInfo(name = "Address") var Address: String,
    @ColumnInfo(name = "Moble") var mob:String,
    @ColumnInfo(name = "Email") var email:String,
    @ColumnInfo(name = "Gender") var gender:String,
    @ColumnInfo(name = "DOB") var dob:Date?,
    @ColumnInfo(name = "Income") var income:Double,
    @ColumnInfo(name = "Industry") var industry:String,
    @ColumnInfo(name = "image", typeAffinity = ColumnInfo.BLOB) var image: ByteArray?,
    @ColumnInfo(name = "Lat")var lat: Double,
    @ColumnInfo(name = "Lon")var lon: Double
    ) : Parcelable