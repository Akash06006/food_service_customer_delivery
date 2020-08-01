package com.example.services.model.tiffinModel

import android.graphics.Bitmap
import android.media.Image
import android.widget.ImageView

class TiffinMainList{

    var tiffanVendorImage: Bitmap? = null

    var tiffanVendorName: String? = null

    var tiffanVendorAvailability: List<String>? = null

    var tiffanVendorPackages: List<String>? = null

    var tiffanVendorTags: List<String>? = null

    var tiffinVegNonVeg: Int? = null

    constructor(
        tiffanVendorImage: Bitmap?,
        tiffanVendorName: String?,
        tiffanVendorAvailability: List<String>?,
        tiffanVendorPackages: List<String>?,
        tiffanVendorTags: List<String>?,
        tiffinVegNonVeg: Int?
    ) {
        this.tiffanVendorImage = tiffanVendorImage
        this.tiffanVendorName = tiffanVendorName
        this.tiffanVendorAvailability = tiffanVendorAvailability
        this.tiffanVendorPackages = tiffanVendorPackages
        this.tiffanVendorTags = tiffanVendorTags
        this.tiffinVegNonVeg = tiffinVegNonVeg
    }
}