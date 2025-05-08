package com.example.lunark.mpesa

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MpesaAPI {
    @GET("oauth/v1/generate?grant_type=client_credentials")
    suspend fun getAccessToken(
        @Header("Authorization") authorization: String
    ): AccessTokenResponse

    @POST("mpesa/stkpush/v1/processrequest")
    suspend fun performSTKPush(
        @Header("Authorization") authorization: String,
        @Body stkPushRequest: STKPushRequest
    ): STKPushResponse
}