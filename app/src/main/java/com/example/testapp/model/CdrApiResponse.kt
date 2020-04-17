package com.example.testapp.model

import com.google.gson.annotations.SerializedName

data class CdrApiResponse(
    @SerializedName("message", alternate = ["error"])
    var message: String? = ""
)