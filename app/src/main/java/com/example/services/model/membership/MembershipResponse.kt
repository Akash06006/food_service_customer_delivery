package com.example.services.model.membership

import com.example.services.model.vendor.VendorListResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MembershipResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("body")
    @Expose
    var data: ArrayList<Body>? = null

     class Body {

        @SerializedName("features")
        @Expose
        var features: ArrayList<String>? = null

         @SerializedName("id")
         @Expose
         var id:String? = null

         @SerializedName("name")
         @Expose
         var name: String? = null

         @SerializedName("companyId")
         @Expose
         var companyId: String? = null

         @SerializedName("status")
         @Expose
         var status: Int? = null

         @SerializedName("createdAt")
         @Expose
         var createdAt: String? = null

         @SerializedName("updatedAt")
         @Expose
         var updatedAt: String? = null

         @SerializedName("subscriptionDurations")
         @Expose
         var subscriptionDurations: ArrayList<SubscriptionDurations>? = null

         @SerializedName("userSubscription")
         @Expose
         var userSubscription: UserSubscription? = null
    }

     class SubscriptionDurations {
        @SerializedName("duration")
        @Expose
        var duration: String? = null
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("price")
        @Expose
        var price: String? = null

    }

    class UserSubscription {

        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("amount")
        @Expose
        var amount: String? = null

        @SerializedName("duration")
        @Expose
        var duration: Int? = null

        @SerializedName("durationId")
        @Expose
        var durationId: String? = null

        @SerializedName("startDate")
        @Expose
        var startDate: String? = null

        @SerializedName("endDate")
        @Expose
        var endDate: String? = null
    }

}
