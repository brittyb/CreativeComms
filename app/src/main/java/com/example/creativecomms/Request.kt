package com.example.creativecomms

data class Request (
var description : String? = "",
var imageUri :String? = "",
var commID : String? = "",
var requesterUID : String? = "",
var artistUID : String? = "",
var price : Double? = 0.0,
var pending: Boolean? = true,
var completed : Boolean? = false,
var paid : Boolean? = false) : java.io.Serializable{
}