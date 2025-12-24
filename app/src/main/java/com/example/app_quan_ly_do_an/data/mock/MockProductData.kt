package com.example.app_quan_ly_do_an.data.mock

import com.example.app_quan_ly_do_an.data.model.FoodItem
import com.example.app_quan_ly_do_an.data.model.Batch

/**
 * Mock data cho Product Screen và Batch Screen
 * Dùng để test UI và chức năng sắp xếp
 */
object MockProductData {

    val sampleFoodItems = listOf(
        FoodItem(
            id = "SP000001",
            name = "Diet Coke",
            expiryDate = "12/2025",
            quantity = 100,
            category = "Nước giải khát",
            price = 10000.0
        ),
        FoodItem(
            id = "SP000002",
            name = "Apple Juice",
            expiryDate = "11/2025",
            quantity = 50,
            category = "Nước trái cây",
            price = 12000.0
        ),
        FoodItem(
            id = "SP000003",
            name = "Coca Cola",
            expiryDate = "10/2025",
            quantity = 200,
            category = "Nước giải khát",
            price = 10000.0
        ),
        FoodItem(
            id = "SP000004",
            name = "Pepsi",
            expiryDate = "12/2024",
            quantity = 80,
            category = "Nước giải khát",
            price = 9000.0
        ),
        FoodItem(
            id = "SP000005",
            name = "Fanta",
            expiryDate = "08/2025",
            quantity = 40,
            category = "Nước giải khát",
            price = 11000.0
        ),
        FoodItem(
            id = "SP000006",
            name = "Sprite",
            expiryDate = "09/2025",
            quantity = 120,
            category = "Nước giải khát",
            price = 10000.0
        ),
        FoodItem(
            id = "SP000007",
            name = "Red Bull",
            expiryDate = "04/2025",
            quantity = 60,
            category = "Năng lượng",
            price = 15000.0
        ),
        FoodItem(
            id = "SP000008",
            name = "Monster Energy",
            expiryDate = "03/2025",
            quantity = 70,
            category = "Năng lượng",
            price = 18000.0
        ),
        FoodItem(
            id = "SP000009",
            name = "7Up",
            expiryDate = "06/2025",
            quantity = 90,
            category = "Nước giải khát",
            price = 10000.0
        ),
        FoodItem(
            id = "SP000010",
            name = "Sting",
            expiryDate = "07/2025",
            quantity = 110,
            category = "Năng lượng",
            price = 8000.0
        ),
        FoodItem(
            id = "SP000011",
            name = "Trà xanh C2",
            expiryDate = "05/2025",
            quantity = 150,
            category = "Trà",
            price = 7000.0
        ),
        FoodItem(
            id = "SP000012",
            name = "Lipton",
            expiryDate = "02/2025",
            quantity = 30,
            category = "Trà",
            price = 6000.0
        )
    )

    /**
     * Tính tổng số lượng tồn kho
     */
    fun getTotalStock(): Int {
        return sampleFoodItems.sumOf { it.quantity }
    }

    /**
     * Tính tổng số loại hàng hóa
     */
    fun getTotalProducts(): Int {
        return sampleFoodItems.size
    }

    /**
     * Lấy danh sách theo category
     */
    fun getProductsByCategory(category: String): List<FoodItem> {
        return sampleFoodItems.filter { it.category == category }
    }

    /**
     * Lấy tất cả categories
     */
    fun getAllCategories(): List<String> {
        return sampleFoodItems.map { it.category }.distinct()
    }

    // ==================== BATCH DATA ====================

    val sampleBatches = listOf(
        Batch(
            batchCode = "LOT0001",
            importDate = "01/12/2024",
            quantity = 50,
            expiryDate = "01/12/2025",
            importPrice = 8000.0,
            initialQuantity = 100,
            storageLocation = "Kho A - Kệ 1",
            productId = "SP000001",
            productName = "Diet Coke"
        ),
        Batch(
            batchCode = "LOT0002",
            importDate = "05/12/2024",
            quantity = 30,
            expiryDate = "05/12/2025",
            importPrice = 7500.0,
            initialQuantity = 80,
            storageLocation = "Kho A - Kệ 2",
            productId = "SP000001",
            productName = "Diet Coke"
        ),
        Batch(
            batchCode = "LOT0003",
            importDate = "10/12/2024",
            quantity = 45,
            expiryDate = "10/12/2025",
            importPrice = 8200.0,
            initialQuantity = 90,
            storageLocation = "Kho B - Kệ 1",
            productId = "SP000001",
            productName = "Diet Coke"
        ),
        Batch(
            batchCode = "LOT0004",
            importDate = "12/12/2024",
            quantity = 20,
            expiryDate = "12/12/2025",
            importPrice = 7800.0,
            initialQuantity = 60,
            storageLocation = "Kho A - Kệ 3",
            productId = "SP000001",
            productName = "Diet Coke"
        ),
        Batch(
            batchCode = "LOT0005",
            importDate = "15/12/2024",
            quantity = 39,
            expiryDate = "15/12/2025",
            importPrice = 8100.0,
            initialQuantity = 75,
            storageLocation = "Kho B - Kệ 2",
            productId = "SP000001",
            productName = "Diet Coke"
        ),
        Batch(
            batchCode = "LOT0006",
            importDate = "18/12/2024",
            quantity = 25,
            expiryDate = "18/12/2025",
            importPrice = 7900.0,
            initialQuantity = 50,
            storageLocation = "Kho A - Kệ 4",
            productId = "SP000001",
            productName = "Diet Coke"
        ),
        Batch(
            batchCode = "LOT0007",
            importDate = "20/12/2024",
            quantity = 60,
            expiryDate = "20/12/2025",
            importPrice = 8300.0,
            initialQuantity = 120,
            storageLocation = "Kho C - Kệ 1",
            productId = "SP000001",
            productName = "Diet Coke"
        ),
        Batch(
            batchCode = "LOT0008",
            importDate = "22/12/2024",
            quantity = 15,
            expiryDate = "22/12/2025",
            importPrice = 7700.0,
            initialQuantity = 40,
            storageLocation = "Kho B - Kệ 3",
            productId = "SP000001",
            productName = "Diet Coke"
        ),
        Batch(
            batchCode = "LOT0009",
            importDate = "23/12/2024",
            quantity = 42,
            expiryDate = "23/12/2025",
            importPrice = 8000.0,
            initialQuantity = 85,
            storageLocation = "Kho A - Kệ 5",
            productId = "SP000001",
            productName = "Diet Coke"
        ),
        Batch(
            batchCode = "LOT0010",
            importDate = "24/12/2024",
            quantity = 38,
            expiryDate = "24/12/2025",
            importPrice = 8150.0,
            initialQuantity = 70,
            storageLocation = "Kho C - Kệ 2",
            productId = "SP000001",
            productName = "Diet Coke"
        )
    )

    /**
     * Lấy batch theo mã lô
     */
    fun getBatchByCode(batchCode: String): Batch? {
        return sampleBatches.find { it.batchCode == batchCode }
    }

    /**
     * Lấy danh sách batch theo productId
     */
    fun getBatchesByProductId(productId: String): List<Batch> {
        return sampleBatches.filter { it.productId == productId }
    }
}
