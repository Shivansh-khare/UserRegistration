package com.example.android.roomwordssample.contacts

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // The flow always holds/caches latest version of data. Notifies its observers when the
    // data has changed.
    @Query("SELECT * FROM word_table")
    fun getAlphabetizedWords(): Flow<List<User>>

    @Query("SELECT * FROM word_table where id=:id")
    fun getUser(id: Long): User

    @Insert
     fun insert(user: User):Long

    @Query("DELETE FROM word_table")
    fun deleteAll()

    @Query("DELETE FROM word_table Where id = :id")
    fun delete(id:Long)

    @Update
    fun update(user: User)

     @Query("UPDATE word_table SET lat = :lnt, lon = :lng WHERE id = :id")
    fun updateLocation(id: Long, lng: Double, lnt: Double)
}