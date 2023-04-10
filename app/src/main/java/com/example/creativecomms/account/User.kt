package com.example.creativecomms.account


data class User (var uid : String? = "",
var username : String? = "",
var email : String? = "",
var password : String? = "",
var rating :Float? = 0.0f, var profileImageUri : String? = "") : java.io.Serializable{

}