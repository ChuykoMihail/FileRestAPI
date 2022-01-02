package com.example.sitfiles.api

import com.google.gson.annotations.SerializedName

data class MyResponseBody(
    @SerializedName("token") var token: String,
    @SerializedName("items") var items: MutableList<MyFile>,
)
