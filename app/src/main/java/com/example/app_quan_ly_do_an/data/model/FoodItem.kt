package com.example.app_quan_ly_do_an.data.model

data class FoodItem(
    val id: String,
    val name: String,
    val expiryDate: String,
    val quantity: Int,
    val category: String,
    val imageUrl: String? = null,
    val points: Double = 0.0
)