package com.example.app_quan_ly_do_an.data.model

// HIEN'S CODE BEGIN
data class FoodItem(
    val id: String = "",
    val name: String = "",
    val expiryDate: String = "", // Nên lưu dạng Timestamp trên Firebase, nhưng để String cho đơn giản demo
    val quantity: Int = 0,
    val category: String = "Khác",
    val unit: String = "Cái",
    val price: Double = 0.0,
    val points: Double = 0.0,
    val barcode: String = "" // Thêm trường barcode để quét
)
// HIEN'S CODE END
