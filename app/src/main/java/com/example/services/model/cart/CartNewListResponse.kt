package com.example.services.model.cart

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CartNewListResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("body") val body: Body
)


data class Body(

    @SerializedName("sum") val sum: Int,
    @SerializedName("totalQunatity") val totalQunatity: Int,
    @SerializedName("data") val data: List<Data>,
    @SerializedName("addOns") val addOns: List<AddOns>
)


data class Data(

    @SerializedName("id") val id: String,
    @SerializedName("serviceId") val serviceId: String,
    @SerializedName("orderPrice") val orderPrice: Int,
    @SerializedName("deliveryType") val deliveryType: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("orderTotalPrice") val orderTotalPrice: Int,
    @SerializedName("promoCode") val promoCode: String,
    @SerializedName("companyId") val companyId: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("createdAt") val createdAt: Int,
    @SerializedName("updatedAt") val updatedAt: Int,
    @SerializedName("service") val service: Service
)


data class Service(

    @SerializedName("addOnIds") val addOnIds: List<String>,
    @SerializedName("icon") val icon: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("productType") val productType: Int,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Int,
    @SerializedName("type") val type: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("includedServices") val includedServices: String,
    @SerializedName("excludedServices") val excludedServices: String,
    @SerializedName("createdAt") val createdAt: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("originalPrice") val originalPrice: Int,
    @SerializedName("offer") val offer: Int,
    @SerializedName("offerName") val offerName: String
)


data class AddOns(

    @SerializedName("icon") val icon: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("productType") val productType: Int,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Int,
    @SerializedName("type") val type: Int,
    @SerializedName("duration") val duration: String,
    @SerializedName("includedServices") val includedServices: String,
    @SerializedName("excludedServices") val excludedServices: String,
    @SerializedName("createdAt") val createdAt: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("originalPrice") val originalPrice: Int,
    @SerializedName("offer") val offer: Int,
    @SerializedName("offerName") val offerName: String
)