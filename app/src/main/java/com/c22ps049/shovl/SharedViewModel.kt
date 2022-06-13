package com.c22ps049.shovl

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.c22ps049.shovl.database.UserPreferences
import com.c22ps049.shovl.network.User
import com.c22ps049.shovl.network.UserModel
import kotlinx.coroutines.launch

class SharedViewModel(private val pref: UserPreferences): ViewModel() {
    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun saveUserEmail(user: User) {
        viewModelScope.launch {
            pref.saveUserEmail(user)
        }
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun getUserData(): LiveData<User> {
        return pref.getUserData().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}