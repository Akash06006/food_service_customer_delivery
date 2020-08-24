package com.example.services.model.vendor

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VendorListResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("body")
    @Expose
    var body: ArrayList<Body>? = null

    inner class Body {
        @SerializedName("logo1")
        @Expose
        var logo1: String? = null
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("companyName")
        @Expose
        var companyName: String? = null
        @SerializedName("address1")
        @Expose
        var address1: String? = null
        @SerializedName("coupan")
        @Expose
        var coupan: Coupon? = null
        @SerializedName("startTime")
        @Expose
        var startTime: String? = null
        @SerializedName("distance")
        @Expose
        var distance: String? = null
        @SerializedName("endTime")
        @Expose
        var endTIme: String? = null
        @SerializedName("rating")
        @Expose
        var rating: String? = null
        @SerializedName("tags")
        @Expose
        var tags: ArrayList<String>? = null
        @SerializedName("totalOrders24")
        @Expose
        var totalOrders: String? = null

    }

    inner class Coupon {
        @SerializedName("discount")
        @Expose
        var discount: String? = null
        @SerializedName("code")
        @Expose
        var code: String? = null
        @SerializedName("validUpto")
        @Expose
        var validUpto: String? = null
    }

    inner class Service {
        @SerializedName("icon")
        @Expose
        var icon: String? = null
        @SerializedName("thumbnail")
        @Expose
        var thumbnail: String? = null
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("name")
        @Expose
        var name: String? = null
        @SerializedName("description")
        @Expose
        var description: String? = null
        @SerializedName("price")
        @Expose
        var price: String? = null
        @SerializedName("type")
        @Expose
        var type: String? = null
        @SerializedName("duration")
        @Expose
        var duration: String? = null
        @SerializedName("turnaroundTime")
        @Expose
        var turnaroundTime: String? = null
        @SerializedName("includedServices")
        @Expose
        var includedServices: String? = null
        @SerializedName("excludedServices")
        @Expose
        var excludedServices: String? = null
        @SerializedName("createdAt")
        @Expose
        var createdAt: String? = null
        @SerializedName("status")
        @Expose
        var status: String? = null


    }
}
