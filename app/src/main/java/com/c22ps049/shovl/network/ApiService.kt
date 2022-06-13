package com.c22ps049.shovl.network

import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("api/user")
    fun registerUser(
        @Body params: RequestBody,
    ): Call<RegisterResponse>

    @POST("login")
    fun loginUser(
        @Body params: RequestBody,
    ): Call<LoginResponse>

    @POST("login")
    fun jsonLogin(
        @Body params: String
    ): Call<LoginResponse>

    @PUT("api/user/{id}")
    fun updateUsers(
        @Path("id")
        @Body email: String?,
        @Body username: String?,
        @Body password: String?,
        id: String
    ):Call<UpdateResponse>

    @GET("api/user/{id}")
    fun getUserData(
        @Header("Authorization") Authorization: String,
        @Path("id")
        id: String
    ):Call<GetUserResponse>

    @GET("api/users")
    fun getAllUsers(
        @Header ("Authorization") Authorization: String
    )

    @DELETE("api/user/{id}")
    fun deleteUser(
        @Path("id")
        id: String
    ):Call<UpdateResponse>
}