package com.example.creativecomms

class Account : java.io.Serializable{
    var username : String
    private var email : String
    var password :String
    var isArtist : Boolean = false
    var rating : Float = 0.0F
    var profileImage : Int = 0
    var commArray : MutableList<Commission>
    private var uid :String = ""


    constructor(uid:String, username:String, email:String, password:String, isArtist: Boolean, rating:Float, profileImage:Int,
                commArray:MutableList<Commission>){
        this.uid = uid
        this.username = username
        this.email = email
        this.password = password
        this.isArtist = isArtist
        this.rating = rating
        this.profileImage = profileImage
        this.commArray = commArray
    }


}