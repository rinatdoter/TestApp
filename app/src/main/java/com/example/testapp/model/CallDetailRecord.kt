package com.example.testapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CallDetailRecord(
    @SerializedName("callId")
    var id: String = "",
    @SerializedName("callLogPhoneState")
    var phoneState: CallLogPhoneState? = null,
    @SerializedName("date")
    var dateOfCall: String? = "",
    var duration: String? = "",
    var number: String? = "",
    var status: Status? = null,
    var type: String = ""
) : Serializable {

    enum class Status(var int: String) {
        OUTGOING("2"),
        INCOMING("1")
    }

    override fun toString(): String {
        return "call_id: " + id + "\n" +
                "phone_num: " + number + "\n" +
                "duration: " + duration +"sec" + "\n" +
                "date: " + dateOfCall + "\n" +
                "type: " + status
    }
}

data class CallLogPhoneState(var content: List<Content>? = null) : Serializable

data class Content(
    @SerializedName("date")
    var dateOfCreation: String? = "",
    var phone: String? = "",
    var state: State? = null
) : Serializable {
    enum class State {
        OFFHOOK,
        RINGING,
        IDLE
    }
}