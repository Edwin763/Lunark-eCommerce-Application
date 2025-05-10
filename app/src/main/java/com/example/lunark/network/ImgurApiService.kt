package com.example.lunark.network

import com.example.lunark.model.ImgurResponse

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ImgurService {
    @Multipart
    @POST("3/image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Header("Authorization") auth: String
    ): Response<ImgurResponse>
}
