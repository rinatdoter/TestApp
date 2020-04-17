package com.example.testapp.network

import com.example.testapp.model.CallDetailRecord
import com.example.testapp.model.CdrApiResponse
import com.example.testapp.model.Constants
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CDRApi {

    @POST("some_route")
    fun sendCdr(
        @Header("Authorization") token: String? = Constants.TOKEN,
        @Body cdrs: CallDetailRecord
    ): Observable<CdrApiResponse>
}