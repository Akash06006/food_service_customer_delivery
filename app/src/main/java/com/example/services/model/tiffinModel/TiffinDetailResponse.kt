package com.example.services.model.tiffinModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class TiffinDetailResponse {

    @SerializedName("code")
    @Expose
    val code: Int? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("body")
    @Expose
    val body: Body? = null


    class Body {

        @SerializedName("menu")
        @Expose
        val menu: List<Menu>? = null

        @SerializedName("info")
        @Expose
        val info: Info? = null

        @SerializedName("packages")
        @Expose
        val packages: ArrayList<Package>? = null
    }

    class Menu {

        @SerializedName("id")
        @Expose
        val id: String? = null

        @SerializedName("dayName")
        @Expose
        val dayName: String? = null

        @SerializedName("bMeal")
        @Expose
        val bMeal: String? = null

        @SerializedName("bPrice")
        @Expose
        val bPrice: String? = null

        @SerializedName("lMeal")
        @Expose
        val lMeal: String? = null

        @SerializedName("lPrice")
        @Expose
        val lPrice: String? = null

        @SerializedName("dMeal")
        @Expose
        val dMeal: String? = null

        @SerializedName("dPrice")
        @Expose
        val dPrice: String? = null

    }

    class Info {

        @SerializedName("deliveryTimings")
        @Expose
        val deliveryTimings: ArrayList<DeliveryTiming>? = null

        @SerializedName("thumbnail")
        @Expose
        val thumbnail: String? = null

        @SerializedName("id")
        @Expose
        val id: String? = null

        @SerializedName("name")
        @Expose
        val name: String? = null

        @SerializedName("contactInfo")
        @Expose
        val contactInfo: String? = null

        @SerializedName("itemType")
        @Expose
        val itemType: String? = null

        @SerializedName("rating")
        @Expose
        val rating: String? = null

        @SerializedName("totalRatings")
        @Expose
        val totalRatings: String? = null

    }

    class DeliveryTiming {

        @SerializedName("breakfast")
        @Expose
        val breakfast: String? = null

        @SerializedName("lunch")
        @Expose
        val lunch: String? = null

        @SerializedName("dinner")
        @Expose
        val dinner: String? = null
    }

    class Package {

        @SerializedName("id")
        @Expose
        val id: String? = null

        @SerializedName("packageName")
        @Expose
        val packageName: String? = null

        @SerializedName("price")
        @Expose
        val price: String? = null

        @SerializedName("oneTimePrice")
        @Expose
        val oneTimePrice: String? = null

    }

}