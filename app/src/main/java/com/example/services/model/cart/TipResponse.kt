package com.example.services.model.cart

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TipResponse {
    @SerializedName("tips")
    @Expose
    var tips: String? = null
    @SerializedName("selected")
    @Expose
    var selected: String? = null
}
