package com.example.services.model.tiffinModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class TiffinMainResponse {

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("body")
    @Expose
    var body: ArrayList<Body>? = null



    class Body {
        @SerializedName("tags")
        @Expose
        var tags: List<String>? = null

        @SerializedName("availability")
        @Expose
        var availability: List<String>? = null

        @SerializedName("packages")
        @Expose
        var packages: List<String>? = null

        @SerializedName("deliveryTimings")
        @Expose
        var deliveryTimings: List<Any>? = null

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

        @SerializedName("itemType")
        @Expose
        var itemType: String? = null

        /*@SerializedName("orderByInfo")
        @Expose
        var orderByInfo: HashMap<String, String>? = null*/

        @SerializedName("companyId")
        @Expose
        var companyId: String? = null

        @SerializedName("totalRatings")
        @Expose
        var totalRatings: String? = null

        @SerializedName("rating")
        @Expose
        var rating: String? = null

        @SerializedName("company")
        @Expose
        var company: Company? = null

    }

    class Company {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("companyName")
        @Expose
        var companyName: String? = null

        @SerializedName("distance")
        @Expose
        var distance: Double? = null

    }

}