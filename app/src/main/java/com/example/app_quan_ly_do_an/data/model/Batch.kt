package com.example.app_quan_ly_do_an.data.model



data class Batch(
    val batchCode: String,
    val importDate: String,
    val quantity: Int,
    val expiryDate: String = "",
    val importPrice: Double = 0.0,
    val initialQuantity: Int = 0,
    val storageLocation: String = "Kho",
    val productId: String = "",
    val productName: String = ""
)