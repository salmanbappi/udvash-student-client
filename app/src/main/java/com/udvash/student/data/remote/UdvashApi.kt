package com.udvash.student.data.remote

import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface UdvashApi {

    @GET("Account/Login")
    suspend fun getLoginPage(): Response<String>

    @FormUrlEncoded
    @POST("Account/Password")
    suspend fun postRegistrationNumber(
        @FieldMap fields: Map<String, String>
    ): Response<String>

    @FormUrlEncoded
    @POST("Account/Login")
    suspend fun postPassword(
        @FieldMap fields: Map<String, String>
    ): Response<String>

    @GET("Dashboard")
    suspend fun getDashboard(): Response<String>

    @GET("Routine/PastClasses")
    suspend fun getPastClasses(): Response<String>

    @GET("Content/Index?id=2")
    suspend fun getCourseContentPage(): Response<String>
}
