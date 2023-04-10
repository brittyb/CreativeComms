package com.example.creativecomms.chatting

data class Chat(var ownerUID: String = "",
var otherUID : String = "",
var chatID : String = "") : java.io.Serializable