package com.example.app_quan_ly_do_an.data.repository

import com.example.app_quan_ly_do_an.data.model.ImportBill
import com.example.app_quan_ly_do_an.data.model.InventoryLot
import com.example.app_quan_ly_do_an.data.model.Product
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects

class InventoryRepository {
    private val db = FirebaseFirestore.getInstance()

    // Các đường dẫn đến Collection
    private val productRef = db.collection("products")
    private val lotRef = db.collection("inventory_lots")
    private val exportBillRef = db.collection("export_bills")

    // THÊM SẢN PHẨM ---
    fun addProduct(product: Product, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val newDoc = productRef.document() // Tạo document trống để lấy ID trước
        val productWithId = product.copy(productId = newDoc.id) // Gán ID vào object

        newDoc.set(productWithId)
            .addOnSuccessListener { onSuccess(newDoc.id) }
            .addOnFailureListener { onFailure(it) }
    }

    // ---LẤY DANH SÁCH ---
    fun getAllProducts(onResult: (List<Product>) -> Unit) {
        productRef.get()
            .addOnSuccessListener { querySnapshot ->
                val list = querySnapshot.toObjects(Product::class.java)
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
    // HÀM NHẬP HÀNG (Transaction: Đảm bảo an toàn dữ liệu)
    fun importGoods(
        importBill: ImportBill,
        newLot: InventoryLot,
        onComplete: (Boolean) -> Unit
    ) {
        db.runTransaction { transaction ->
            // 1. Tạo tham chiếu cho Document mới
            val billRef = db.collection("import_bills").document()
            val lotRef = db.collection("inventory_lots").document()

            // 2. Gán ID tự sinh vào Object
            val finalBill = importBill.copy(importBillId = billRef.id)
            val finalLot = newLot.copy(lotId = lotRef.id, importDate = System.currentTimeMillis())

            // 3. Thực hiện ghi đồng thời vào Database
            transaction.set(billRef, finalBill)
            transaction.set(lotRef, finalLot)

            // Trả về kết quả thành công cho Transaction
            null
        }.addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            onComplete(false)
        }
    }

}
