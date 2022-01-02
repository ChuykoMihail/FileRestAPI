package com.example.sitfiles.api

import com.google.gson.annotations.SerializedName

data class MyFile(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name : String
)
