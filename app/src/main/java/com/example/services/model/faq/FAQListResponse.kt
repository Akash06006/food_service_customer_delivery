package com.example.services.model.faq

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FAQListResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    //

    @SerializedName("body")
    @Expose
    var data: Body? = null

    inner class Body {
        @SerializedName("category")
        @Expose
        var category: ArrayList<Category>? = null
        @SerializedName("data")
        @Expose
        var faqList: ArrayList<Data>? = null
    }

    inner class Category {
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("catName")
        @Expose
        var catName: String? = null
        @SerializedName("selected")
        @Expose
        var selected: String? = null
    }

    inner class Data {
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("question")
        @Expose
        var question: String? = null
        @SerializedName("answer")
        @Expose
        var answer: String? = null
        @SerializedName("status")
        @Expose
        var status: String? = null
        @SerializedName("selected")
        @Expose
        var selected: String? = null
        @SerializedName("language")
        @Expose
        var language: String? = null
    }
}
