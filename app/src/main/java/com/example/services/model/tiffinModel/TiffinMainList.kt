package com.example.services.model.tiffinModel

import android.graphics.Bitmap

class TiffinMainList{

    var tiffanOrderImage: Bitmap? = null

    var tiffanOrderName: String? = null

    var tiffanOrderDate: String? = null

    var tiffanOrderPrice: String? = null

    var tiffanOrderDistance: String? = null

    var tiffinOrderNoOfItems: String? = null

    constructor(
        tiffanOrderImage: Bitmap?,
        tiffanOrderName: String?,
        tiffanOrderDate: String?,
        tiffanOrderPrice: String?,
        tiffanOrderDistance: String?,
        tiffinOrderNoOfItems: String?
    ) {
        this.tiffanOrderImage = tiffanOrderImage
        this.tiffanOrderName = tiffanOrderName
        this.tiffanOrderDate = tiffanOrderDate
        this.tiffanOrderPrice = tiffanOrderPrice
        this.tiffanOrderDistance = tiffanOrderDistance
        this.tiffinOrderNoOfItems = tiffinOrderNoOfItems
    }
}