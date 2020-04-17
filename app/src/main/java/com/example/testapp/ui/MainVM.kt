package com.example.testapp.ui

import android.annotation.SuppressLint
import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.CallLog
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testapp.extension.format
import com.example.testapp.extension.toDateString
import com.example.testapp.model.*
import com.example.testapp.repository.CdrRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.NullPointerException
import java.util.*

@SuppressLint("CheckResult")
class MainVM(
    private val context: Application,
    private val cdrRepo: CdrRepository
) : ViewModel() {

    val event = MutableLiveData<Event>()

    fun getLastCallAndSendToApi() {
        showLoading()
        Observable.fromCallable {
            return@fromCallable getLastCallFromCursor()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ cdr ->
                cdr?.let {
                    event.value = Event.CdrFetched(it)
                    sendCdrToApi(it)
                }
            }, {
                handleFetchingCallError(it)
                hideLoading()
            })
    }

    private fun handleFetchingCallError(it: Throwable?) {
        val message = when(it){
            is NullPointerException -> "Список звонков пуст"
            else -> it?.message ?: "Непредвиденная ошибка :("
        }
        event.value = Event.ShowToast(message)
    }

    private fun getLastCallFromCursor(): CallDetailRecord? {
        obtainCursorForCallLog()?.let {
            while (it.moveToNext()) {
                val idIndex = it.getColumnIndex(CallLog.Calls._ID)
                val numberIndex: Int = it.getColumnIndex(CallLog.Calls.NUMBER)
                val durationIndex: Int = it.getColumnIndex(CallLog.Calls.DURATION)
                val dateIndex: Int = it.getColumnIndex(CallLog.Calls.DATE)
                val incomingTypeIndex: Int =
                    it.getColumnIndex(CallLog.Calls.INCOMING_TYPE.toString())

                val id = it.getString(idIndex)
                val number = it.getString(numberIndex)
                val duration = it.getString(durationIndex)
                val date =  it.getString(dateIndex).toLong().toDateString()
                val status =
                    if (incomingTypeIndex == -1) CallDetailRecord.Status.INCOMING
                    else CallDetailRecord.Status.OUTGOING
                val state =
                    if (incomingTypeIndex == -1) Content.State.RINGING
                    else Content.State.OFFHOOK

                it.close()

                return CallDetailRecord().apply {
                    this.id = id
                    phoneState = CallLogPhoneState(
                        listOf(
                            Content(Date().format(), number, state),
                            Content(Date().format(), number, Content.State.IDLE)
                        )
                    )
                    dateOfCall = date
                    this.duration = duration
                    this.number = number
                    this.status = status
                    type = status.int
                }
            }
        }
        return null
    }

    // cursor is recycled inside caller function
    @SuppressLint("Recycle")
    private fun obtainCursorForCallLog(): Cursor? {
        val contacts: Uri = CallLog.Calls.CONTENT_URI
        val filterQuery = " DESC limit 1;"

        return try {
            context.contentResolver.query(
                contacts,
                null,
                null,
                null,
                CallLog.Calls.DATE + filterQuery
            )
        } catch (e: SecurityException) {
            Log.d("ERROR", "Need CALL_LOG permission")
            null
        }
    }

    private fun sendCdrToApi(cdr: CallDetailRecord) {
        cdrRepo.senCdrToApi(cdr)
            .subscribe({
                handleSuccessfulResponse(it)
                hideLoading()
            }, {
                sendFailedEvent()
                hideLoading()
            })
    }

    private fun handleSuccessfulResponse(response: CdrApiResponse?) {
        if (response?.message == "ok")
            event.value = Event.ShowToast("Успешно")
        else
            sendFailedEvent()
    }

    private fun sendFailedEvent() {
        event.value = Event.ShowToast("Не успешно")
    }

    /** In real project these methods should be moved to BaseVM*/
    private fun showLoading(){ event.value = Event.ShowLoading}
    private fun hideLoading(){ event.value = Event.HideLoading}
}