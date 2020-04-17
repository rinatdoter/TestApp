package com.example.testapp.model

sealed class Event {
    class ShowToast(val message: String): Event()
    class CdrFetched(val cdr: CallDetailRecord): Event()
    object ShowLoading: Event()
    object HideLoading: Event()
}