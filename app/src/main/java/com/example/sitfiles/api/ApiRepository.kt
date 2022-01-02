package com.example.sitfiles.api

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ApiRepository(context: Context) {
    private val TAG = "ApiRepository"
    var token: String = "Bearer "
    val filesApi: FilesApi
    val myRequestBody: MyRequestBody
    val thisContext = context

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://165.22.93.105:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        filesApi = retrofit.create(FilesApi::class.java)
        myRequestBody = MyRequestBody("", "", "", "")

    }


    fun loginUser(email: String, password: String): LiveData<MyResponseBody> {

        myRequestBody.apply {
            this.email = email
            this.password = password
        }

        val responseLiveData: MutableLiveData<MyResponseBody> = MutableLiveData()
        val loginRequest: Call<MyResponseBody> = filesApi.loginUser(myRequestBody)



        loginRequest.enqueue(object : Callback<MyResponseBody> {
            override fun onResponse(
                call: Call<MyResponseBody>,
                response: Response<MyResponseBody>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Login onResponse isSuccessful = true")
                    responseLiveData.value = response.body()
                    token += response.body()?.token!!
                    Log.d(TAG, "Login onResponse token = ${token}")
                    getFilesList()
                } else {
                    Log.d(TAG, "Login onResponse isSuccessful = false")
                }
            }

            override fun onFailure(call: Call<MyResponseBody>, t: Throwable) {
                Log.d(TAG, "Login network error")
            }
        })
        return responseLiveData
    }

    fun getFilesList(){
        val getFilesListRequest: Call<MyResponseBody> = filesApi.getFilesList(token)

        getFilesListRequest.enqueue(object : Callback<MyResponseBody> {
            override fun onResponse(
                call: Call<MyResponseBody>,
                response: Response<MyResponseBody>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "GetFilesList onResponse isSuccessful = true")
                    Log.d(TAG, "GetFilesList files: ${response.body()?.items}")
                    getFile(5)
                } else {
                    Log.d(TAG, "GetFilesList onResponse isSuccessful = false")
                }
            }

            override fun onFailure(call: Call<MyResponseBody>, t: Throwable) {
                Log.d(TAG, "Register network error")
                if (t is IOException) {
                    Log.d(TAG, "${t.message}");
                    // logging probably not necessary
                } else {
                    Log.d(TAG, "conversion issue! big problems :(");
                    // todo log to some central bug tracking service
                }
            }
        })
    }

    fun getFile(id: Int){
        val getFileRequest: Call<ResponseBody> = filesApi.getFile(token, id)

        getFileRequest.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "GetFile onResponse isSuccessful = true")
                    Log.d(TAG, "GetFile file: ${response.body()}")
                    uploadFile()
                } else {
                    Log.d(TAG, "GetFile onResponse isSuccessful = false")
                    Log.d(
                        TAG,
                        "GetFile onResponse isSuccessful = false ${
                            response.errorBody()!!.charStream().readText()
                        }"
                    )

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d(TAG, "GetFile network error")
            }
        })
    }

    fun uploadFile(){
        val file = File("storage/self/primary/DCIM/Camera/IMG_20211223_121112.jpg")
        if(file.exists()) Log.d(TAG, "File exist") else Log.d(TAG, "File dose not exist")
        val uri = Uri.fromFile(file)
        val cR: ContentResolver = thisContext.contentResolver
        val mime = MimeTypeMap.getSingleton()
        val type = mime.getExtensionFromMimeType(cR.getType(uri))


        val requestFile = RequestBody.create(
            MediaType.parse("image/jpeg"),
            file
        )
        val body = MultipartBody.Part.createFormData("uploaded_file", file.name, requestFile)
        val descriptionString = "string";
        val description = RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString)

        val uploadFileRequest: Call<ResponseBody> = filesApi.uploadFile(token, description, body)

        uploadFileRequest.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "GetFile onResponse isSuccessful = true")
                    Log.d(TAG, "GetFile file: ${response.body()?.charStream()?.readText()}")
                } else {
                    Log.d(TAG, "GetFile onResponse isSuccessful = false")
                    Log.d(
                        TAG,
                        "GetFile onResponse isSuccessful = false ${
                            response.errorBody()!!.charStream().readText()
                        }"
                    )
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d(TAG, "UploadFile network error")
                if (t is IOException) {
                    Log.d(TAG, "${t.message}");
                    // logging probably not necessary
                } else {
                    Log.d(TAG, "conversion issue! big problems :(");
                    // todo log to some central bug tracking service
                }
            }
        })


    }



}


