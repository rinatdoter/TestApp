package com.example.testapp.extension

import java.text.SimpleDateFormat
import java.util.*


const val defaultDateRegex = "yyyy-MM-dd HH:mm:ss"

fun Date?.format(regex: String? = defaultDateRegex): String =
    this?.let {
        SimpleDateFormat(regex!!, Locale.getDefault()).format(this)
    } ?: ""

fun Long?.toDateString(regex: String? = defaultDateRegex): String =
    this?.let { Date(this).format(regex) } ?: ""