package com.example.services.model.faq

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LinksResponse {
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
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("aboutus")
        @Expose
        var aboutus: String? = null
        @SerializedName("aboutusLink")
        @Expose
        var aboutusLink: String? = null

        @SerializedName("cancellationLink")
        @Expose
        var cancellationLink: String? = null

        @SerializedName("cancellationPolicy")
        @Expose
        var cancellationPolicy: String? = null


        @SerializedName("privacyContent")
        @Expose
        var privacyContent: String? = null
        @SerializedName("termsContent")
        @Expose
        var termsContent: String? = null
        @SerializedName("termsLink")
        @Expose
        var termsLink: String? = null
        @SerializedName("privacyLink")
        @Expose
        var privacyLink: String? = null
    }

}
