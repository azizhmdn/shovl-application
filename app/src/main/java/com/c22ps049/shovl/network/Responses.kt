package com.c22ps049.shovl.network

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

data class RegisterResponse(
    val error: Boolean,
    val message: String
)

data class UpdateResponse(
    val error: Boolean,
    val message: String
)

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: UserLogin
)

data class GetUserResponse(
    val error: Boolean,
    val message: String,
    val user: User
)

data class UserLogin(
    val userId: String,
    val name: String,
    val token: String
)

@Entity(tableName = "user")
@Parcelize
data class User(
    @PrimaryKey
    val email: String,
    val username: String,
): Parcelable

data class UserModel(
    var userId: String,
    var name: String,
    var token: String,
    var isLogin: Boolean
)
