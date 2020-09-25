package com.example.services.model.services

import com.example.services.model.LoginResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateCartResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("body")
    @Expose
    var data: Body? = null

    inner class Body {
        @SerializedName("orderTotalPrice")
        @Expose
        var orderTotalPrice: Int? = null

        @SerializedName("quantity")
        @Expose
        var quantity: String? = null

        @SerializedName("orderPrice")
        @Expose
        var orderPrice: String? = null

        @SerializedName("sum")
        @Expose
        var sum: Int? = null

        @SerializedName("totalQunatity")
        @Expose
        var totalQuantity: Int? = null
    }
}