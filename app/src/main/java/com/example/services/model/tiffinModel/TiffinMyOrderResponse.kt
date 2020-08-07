package com.example.services.model.tiffinModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class TiffinMyOrderResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("body")
    @Expose
    var body: Body? = null

    class Body {

        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("excDays")
        @Expose
        var excDays: String? = null

        @SerializedName("excAvailability")
        @Expose
        var excAvailability: String? = null

        @SerializedName("excPrice")
        @Expose
        var excPrice: String? = null

        @SerializedName("createdAt")
        @Expose
        var createdAt: String? = null

        @SerializedName("updatedAt")
        @Expose
        var updatedAt: String? = null

        @SerializedName("tiffinId")
        @Expose
        var tiffinId: String? = null

        @SerializedName("orderPrice")
        @Expose
        var orderPrice: String? = null

        @SerializedName("orderTotalPrice")
        @Expose
        var orderTotalPrice: String? = null

        @SerializedName("quantity")
        @Expose
        var quantity: String? = null

        @SerializedName("userId")
        @Expose
        var userId: String? = null

        @SerializedName("companyId")
        @Expose
        var companyId: String? = null

        @SerializedName("package")
        @Expose
        var _package: String? = null

        @SerializedName("fromDate")
        @Expose
        var fromDate: String? = null

        @SerializedName("endDate")
        @Expose
        var endDate: String? = null
        
    }

}