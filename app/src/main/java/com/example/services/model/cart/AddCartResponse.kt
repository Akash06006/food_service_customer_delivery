package com.example.services.model.cart

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddCartResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("body")
    @Expose
    var body: Body? = null

    inner class Body {


        @SerializedName("id")
        @Expose
        var id: String? = null

    }

}
