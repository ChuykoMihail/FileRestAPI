package com.example.sitfiles.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface FilesApi {
    @POST("login")
    fun loginUser(@Body body: MyRequestBody) : Call<MyResponseBody>


    @GET("files/list")
    fun getFilesList(@Header("Authorization") authorization: String) : Call<MyResponseBody>

    @GET("files/{id}")
    fun getFile(@Header("Authorization") authorization: String, @Path("id") id:Int): Call<ResponseBody>

    @Multipart
    @POST("files/upload")
    fun uploadFile(
        @Header("Authorization") authorization: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ):Call<ResponseBody>
}