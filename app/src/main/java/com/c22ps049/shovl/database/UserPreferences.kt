package com.c22ps049.shovl.database

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.c22ps049.shovl.network.User
import com.c22ps049.shovl.network.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    fun getUser(): Flow<UserModel> {
        return dataStore.data.map { pref ->
            UserModel(
                pref[ID_KEY] ?: "",
                pref[NAME_KEY] ?: "",
                pref[TOKEN] ?:"",
                pref[STATE_KEY] ?: false
            )
        }
    }

    fun getUserData(): Flow<User> {
        return dataStore.data.map { pref ->
            User(
                pref[EMAIL_KEY] ?: "",
                pref[NAME_KEY] ?: "",
            )
        }
    }

    suspend fun saveUser(user: UserModel) {
        dataStore.edit { pref ->
            pref[ID_KEY] = user.userId
            pref[NAME_KEY] = user.name
            pref[TOKEN] = user.token
            pref[STATE_KEY] = user.isLogin
        }
    }

    suspend fun saveUserEmail(user: User) {
        dataStore.edit { pref ->
            pref[EMAIL_KEY] = user.email
            pref[NAME_KEY] = user.username
        }
    }

    suspend fun logout() {
        dataStore.edit { pref ->
            pref.clear()
        }
    }

    companion object {
        @Volatile
        private var mInstance: UserPreferences? = null

        private val ID_KEY = stringPreferencesKey("userId")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val NAME_KEY = stringPreferencesKey("name")
        private val STATE_KEY = booleanPreferencesKey("state")
        private val TOKEN = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return mInstance ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                mInstance = instance
                instance
            }
        }
    }
}