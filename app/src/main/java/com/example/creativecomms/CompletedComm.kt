package com.example.creativecomms

data class CompletedComm (
var notes : String? = "",
var fileUri :String? = "",
var commTitle : String? = "",
var requesterUID : String? = "",
var artistUID : String? = "",
var reqID : String? = "",
var paid : Boolean = false,
var fileName : String = "") : java.io.Serializable{
}