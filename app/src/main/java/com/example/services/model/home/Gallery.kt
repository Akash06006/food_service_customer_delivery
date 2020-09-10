package com.example.services.viewmodels.home


data class Gallery(
    val mediaHttpUrl: String,
    val id: String,
    val mediaType: String,
    val createdAt: String,
    val title: String,
    val description: String
)