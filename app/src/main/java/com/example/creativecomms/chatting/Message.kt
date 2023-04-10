package com.example.creativecomms.chatting

import com.example.creativecomms.account.User

data class Message(
    var message: String = "",
    var owner: String = "",
var timestamp : Long = 0) : java.io.Serializable