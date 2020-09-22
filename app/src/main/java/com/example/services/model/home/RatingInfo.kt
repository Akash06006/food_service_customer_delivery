package com.example.services.viewmodels.home

import java.io.Serializable


data class RatingInfo(
    val rating: String,
    val review: String,
    val orderId: String,
    val foodQuality: String,
    val foodQuantity: String,
    val packingPres: String
) : Serializable