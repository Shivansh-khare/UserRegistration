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

import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.util.*

/**
 * View Model to keep a reference to the word repository and
 * an up-to-date list of all words.
 */

public class UserViewModel(private val repository: UserRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allWords: LiveData<List<User>> = repository.allWords.asLiveData()


    val returnedVal = MutableLiveData<User>()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    suspend fun insert(user: User):Long {
        var usr:Long = -1
        viewModelScope.launch(Dispatchers.IO) {
            usr = repository.insert(user)!!
        }.join()
        return usr
    }

    fun delete(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(id)
    }

    suspend fun getUser(id: Long): User {
        var usr: User = User(null,"","","","","","",Calendar.getInstance().time,10.0,"",null,0.0,0.0)
        var jb = viewModelScope.launch(Dispatchers.IO){
            usr = repository.getUser(id)
        }
        jb.join()
        return usr
    }

    fun update(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(user)
    }

    fun updateLocation(id: Long, lng: Double, lat: Double) {
        repository.updateLocation(id, lng, lat)
    }
}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
