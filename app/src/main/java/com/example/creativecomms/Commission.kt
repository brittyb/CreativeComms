package com.example.creativecomms

data class Commission(var title : String? = "",
        var description : String? = "",
        var minPrice : Double? = 0.0,
        var maxPrice : Double? = 0.0,
        var tag1 : String? = "",
                      var tag2: String? = "",
        var imageUri : String? = "",
        var uid : String? = "",
        var selectedET : String? = "") {

}