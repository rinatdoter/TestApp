package com.example.testapp.repository

import com.example.testapp.model.CallDetailRecord
import com.example.testapp.model.CdrApiResponse
import com.example.testapp.network.CDRApi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CdrRepository(private val cdrApi: CDRApi) {

    fun senCdrToApi(cdr: CallDetailRecord): Observable<CdrApiResponse?> =
        cdrApi.sendCdr(cdrs = cdr)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}
