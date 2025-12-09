package com.example.app_quan_ly_do_an.data.model
//Để không phụ thuộc vào bạn làm Backend, cần tạo một Model riêng cho Phiếu Nhập.
data class ImportBill(
    val id: String,
    val code: String,       // Mã phiếu (VD: PN001)
    val supplierName: String, // Nhà cung cấp
    val date: String,       // Ngày nhập
    val totalAmount: Double, // Tổng tiền
    val status: String = "Hoàn thành" // Trạng thái
)