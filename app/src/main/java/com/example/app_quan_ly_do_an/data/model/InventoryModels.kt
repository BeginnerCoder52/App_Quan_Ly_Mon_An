package com.example.app_quan_ly_do_an.data.model

import java.util.Date

// 1. Sản phẩm (Product)
data class Product(
    val productId: String = "",           // Document ID của Firestore
    val productCode: String = "",         // Mã SP0001
    val productName: String = "",         // Tên món ăn
    val productImage: String = "",         // Link từ Firebase Storage
    val productCategory: String = "",      // Danh mục
    val unit: String = "",                // kg, gói, chai...
    val sellPrice: Double = 0.0,
    val minStock: Int = 0,                // Mức tồn kho tối thiểu
    val totalStock: Int = 0               // Tổng tồn kho (Số lượng từ tất cả các lô)
)

// 2. Chi tiết Lô hàng & Tồn kho (InventoryLot)
data class InventoryLot(
    val lotId: String = "",
    val lotCode: String = "",
    val productId: String = "",        // Link tới productId
    val importPrice: Double = 0.0,
    val expiryDate: Date? = null,         // Hạn sử dụng (Date)
    val initialQuantity: Int = 0,
    val currentQuantity: Int = 0,
    val location: String = "",            // Vị trí lưu trữ
    val importDate: Date? = null, // Ngày nhập (Date)
    val importBillId: String = "",
)

// 3. Hóa đơn xuất hàng (ExportBill)
data class ExportBill(
    val exportBillId: String = "",
    val exportBillCode: String = "",
    val date: Date? = null,               // Ngày tạo (Timestamp)
    val totalAmount: Double = 0.0
)

// 4. Chi tiết hóa đơn xuất hàng (ExportBillDetail)
data class ExportBillDetail(
    val exportBillDetailId: String = "",
    val exportBillDetailCode: String = "",
    val exportBillId: String = "",        // FK: Hóa đơn xuất
    val productId: String = "",        // FK: Sản phẩm
    val quantity: Int = 0,
    val sellPrice: Double = 0.0
)

// 5. Hóa đơn nhập hàng (ImportBill)
data class ImportBill(
    val importBillId: String = "",
    val importBillIdCode: String = "",
    // Dùng Date để Firestore tự map từ Timestamp
    val date: Date? = null,
    val supplier: String = "",
    val totalAmount: Double = 0.0
)

// 6. Chi tiết hóa đơn nhập hàng (ImportBillDetail)
data class ImportBillDetail(
    val importBillDetailId: String = "",
    val importBillDetailCode: String = "",
    val importBillId: String = "",
    val productId: String = "",
    val quantity: Int = 0,
    val importPrice: Double = 0.0,
    val expiryDate: Date? = null,
)

data class Category(
    val categoryId: String = "",
    val categoryName: String = ""
)

// HIEN'S CODE BEGIN
// Model cho Thông báo / Nhật ký hoạt động
data class AppNotification(
    val id: String = "",
    val title: String = "",      // Ví dụ: "Thêm hàng hóa", "Xóa lô hàng"
    val content: String = "",    // Chi tiết: "Đã thêm Bánh Hura", "Xóa lô #123"
    val timestamp: java.util.Date? = null, // Thời gian thực hiện
    val type: String = "INFO"    // INFO, WARNING, ERROR
)
// HIEN'S CODE END