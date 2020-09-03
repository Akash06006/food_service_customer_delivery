package com.example.services.model.home

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LandingResponse {
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

        @SerializedName("cartCompanyType")
        @Expose
        var cartCompanyType: String? = null
        @SerializedName("restOffers")
        @Expose
        var restOffers: ArrayList<RestOffers>? = null
        @SerializedName("offers")
        @Expose
        var offers: ArrayList<Offers>? = null
        @SerializedName("deals")
        @Expose
        var deals: ArrayList<Deal>? = null
        @SerializedName("vendors")
        @Expose
        var vendors: ArrayList<Vendors>? = null
        @SerializedName("banners")
        @Expose
        var banners: ArrayList<Banners>? = null
        @SerializedName("bestSeller")
        @Expose
        var bestSeller: ArrayList<BestSeller>? = null
        @SerializedName("topPicks")
        @Expose
        var topPicks: ArrayList<TopPicks>? = null
        @SerializedName("trending")
        @Expose
        var trending: ArrayList<Trending>? = null
        @SerializedName("recentOrder")
        @Expose
        var recentOrder: RecentOrder? = null


    }

    inner class RecentOrder {
        @SerializedName("orderNo")
        @Expose
        var orderNo: String? = null
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("progressStatus")
        @Expose
        var progressStatus: String? = null
        @SerializedName("totalOrderPrice")
        @Expose
        var totalOrderPrice: String? = null
        @SerializedName("orderStatus")
        @Expose
        var orderStatus: OrderStatus? = null
    }

    inner class OrderStatus {
        @SerializedName("statusName")
        @Expose
        var statusName: String? = null
        @SerializedName("status")
        @Expose
        var status: String? = null
    }

    inner class RestOffers {
        @SerializedName("discount")
        @Expose
        var discount: String? = null
        @SerializedName("name")
        @Expose
        var name: String? = null
        @SerializedName("thumbnail")
        @Expose
        var thumbnail: String? = null

    }

    inner class Offers {
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
        @SerializedName("code")
        @Expose
        var code: String? = null
        @SerializedName("discount")
        @Expose
        var discount: String? = null
        @SerializedName("validupto")
        @Expose
        var validupto: String? = null
        @SerializedName("company")
        @Expose
        var company: Company? = null
    }

    inner class Deal {
        @SerializedName("thumbnail")
        @Expose
        var thumbnail: String? = null
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("dealName")
        @Expose
        var dealName: String? = null
        @SerializedName("description")
        @Expose
        var description: String? = null
        @SerializedName("code")
        @Expose
        var code: String? = null
        @SerializedName("discount")
        @Expose
        var discount: String? = null
        @SerializedName("validupto")
        @Expose
        var validupto: String? = null
        @SerializedName("company")
        @Expose
        var company: Company? = null
    }

    inner class Company {
        @SerializedName("companyName")
        @Expose
        var companyName: String? = null
        @SerializedName("id")
        @Expose
        var id: String? = null
    }

    inner class Vendors {
        @SerializedName("totalOrders24")
        @Expose
        var totalOrders: String? = null
        @SerializedName("startTime")
        @Expose
        var startTime: String? = null
        @SerializedName("endTime")
        @Expose
        var endTIme: String? = null
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
        @SerializedName("rating")
        @Expose
        var rating: String? = null
        @SerializedName("distance")
        @Expose
        var distance: String? = null
        @SerializedName("coupan")
        @Expose
        var coupan: Coupon? = null
        @SerializedName("tags")
        @Expose
        var tags: ArrayList<String>? = null


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

    inner class Banners {
        @SerializedName("url")
        @Expose
        var url: String? = null
        @SerializedName("name")
        @Expose
        var name: String? = null
    }

    inner class BestSeller {
        @SerializedName("startTime")
        @Expose
        var startTime: String? = null
        @SerializedName("endTime")
        @Expose
        var endTIme: String? = null
        @SerializedName("distance")
        @Expose
        var distance: String? = null
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

    inner class TopPicks {
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
    }

    inner class Trending {
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
        @SerializedName("categoryId")
        @Expose
        var categoryId: String? = null
        @SerializedName("category")
        @Expose
        var category: Category? = null
    }

    inner class Category {
        @SerializedName("name")
        @Expose
        var name: String? = null
        @SerializedName("id")
        @Expose
        var id: String? = null
    }

}
