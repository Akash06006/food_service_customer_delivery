package com.example.services.model.cart

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeliveryTipInstructionListResponse {
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
        @SerializedName("deliveryInstructions")
        @Expose
        var deliveryInstructions: ArrayList<DeliveryInstructions>? = null
        @SerializedName("pickupInstructions")
        @Expose
        var pickupInstructions: ArrayList<DeliveryInstructions>? = null
        @SerializedName("tips")
        @Expose
        var tips: ArrayList<Int>? = null
        @SerializedName("sum")
        @Expose
        var sum: String? = null
    }

    inner class DeliveryInstructions {

        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("heading")
        @Expose
        var heading: String? = null
        @SerializedName("selected")
        @Expose
        var selected: String? = null
        @SerializedName("instruction")
        @Expose
        var instruction: String? = null
    }

}
