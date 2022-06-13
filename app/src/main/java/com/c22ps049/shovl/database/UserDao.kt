package com.c22ps049.shovl.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UserDao {

    @Query("DELETE FROM user")
    suspend fun deleteAll()
}