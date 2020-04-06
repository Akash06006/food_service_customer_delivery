package com.example.services.model.cart

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CartListResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: ArrayList<Body>? = null
    @SerializedName("coupanDetails")
    @Expose
    var coupanDetails: CoupanDetails? = null


    inner class Body {
        @SerializedName("service")
        @Expose
        var service: Service? = null
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("serviceId")
        @Expose
        var serviceId: String? = null
        @SerializedName("userId")
        @Expose
        var userId: String? = null
        @SerializedName("price")
        @Expose
        var price: String? = null
        @SerializedName("quantity")
        @Expose
        var quantity: String? = null
        @SerializedName("created_at")
        @Expose
        var created_at: String? = null
        @SerializedName("updated_at")
        @Expose
        var updated_at: String? = null


    }

    inner class Service {
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("name")
        @Expose
        var name: String? = null
        @SerializedName("quantity")
        @Expose
        var quantity: String? = null
        @SerializedName("description")
        @Expose
        var description: String? = null
        @SerializedName("timeSlotStatus")
        @Expose
        var timeSlotStatus: String? = null

        @SerializedName("currency")
        @Expose
        var currency: String? = null
        @SerializedName("icon")
        @Expose
        var icon: String? = null
        @SerializedName("rating")
        @Expose
        var rating: Int? = null
        @SerializedName("price")
        @Expose
        var price: Int? = null
    }

    inner class CoupanDetails {
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("coupanId")
        @Expose
        var coupanId: String? = null
        @SerializedName("coupanCode")
        @Expose
        var coupanCode: String? = null
        @SerializedName("coupanDiscount")
        @Expose
        var coupanDiscount: String? = null
        @SerializedName("totalAmount")
        @Expose
        var totalAmount: String? = null
        @SerializedName("discountPrice")
        @Expose
        var discountPrice: String? = null
        @SerializedName("payableAmount")
        @Expose
        var payableAmount: String? = null
        @SerializedName("isCoupanValid")
        @Expose
        var isCoupanValid: String? = null
        @SerializedName("isCouponApplied")
        @Expose
        var isCouponApplied: String? = null

    }
}
