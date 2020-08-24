package com.example.services.model.search

import com.example.services.model.vendor.VendorListResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SearchResponse {
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

        @SerializedName("services")
        @Expose
        var services: ArrayList<Services>? = null


    }

    inner class Services {
        @SerializedName("logo1")
        @Expose
        var logo1: String? = null
        @SerializedName("thumbnail")
        @Expose
        var thumbnail: String? = null
        @SerializedName("distance")
        @Expose
        var distance: String? = null
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("itemType")
        @Expose
        var itemType: String? = null
        @SerializedName("companyName")
        @Expose
        var companyName: String? = null
        @SerializedName("address1")
        @Expose
        var address1: String? = null
        @SerializedName("startTime")
        @Expose
        var startTime: String? = null
        @SerializedName("endTime")
        @Expose
        var endTime: String? = null
        @SerializedName("rating")
        @Expose
        var rating: String? = null

        @SerializedName("coupan")
        @Expose
        var coupan: Coupon? = null


        @SerializedName("duration")
        @Expose
        var duration: String? = null
        @SerializedName("originalPrice")
        @Expose
        var originalPrice: String? = null
        @SerializedName("offer")
        @Expose
        var offer: String? = null
        @SerializedName("price")
        @Expose
        var price: String? = null
        @SerializedName("name")
        @Expose
        var name: String? = null


        @SerializedName("company")
        @Expose
        var company: Company? = null


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

    inner class Company {
        @SerializedName("companyName")
        @Expose
        var companyName: String? = null
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("rating")
        @Expose
        var rating: String? = null
    }
}
