package com.example.services.model.address

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeliveryChargesResponse {
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
        @SerializedName("shipment")
        @Expose
        var shipment: String? = null
    }
}
