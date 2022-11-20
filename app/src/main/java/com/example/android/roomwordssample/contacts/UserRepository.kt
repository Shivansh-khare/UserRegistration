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

import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow

/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */
public class UserRepository(private val userDao: UserDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allWords: Flow<List<User>>  = userDao.getAlphabetizedWords()
    var wrd = MutableLiveData<User>()
    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(user: User): Long? {
        return userDao.insert(user)
    }

    @WorkerThread
    suspend fun delete(id: Long){
        userDao.delete(id)
    }

    @WorkerThread
    fun getUser(id: Long): User {
        return userDao.getUser(id)
    }

    @WorkerThread
    suspend fun update(user: User)
    {
        userDao.update(user)
    }

    @WorkerThread
    fun updateLocation(id: Long, lng: Double, lnt: Double){
        userDao.updateLocation(id, lng, lnt)
    }
}
