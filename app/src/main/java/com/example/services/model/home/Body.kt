package com.example.services.viewmodels.home


data class Body(
        val banners: List<Banners>,
        val services: List<Services>,
        val subcat: List<Subcat>,
        val offers: List<Offers>,
        val trending: List<Trending>
)