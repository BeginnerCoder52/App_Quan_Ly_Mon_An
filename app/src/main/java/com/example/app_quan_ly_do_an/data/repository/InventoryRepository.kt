package com.example.app_quan_ly_do_an.data.repository

import com.example.app_quan_ly_do_an.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class InventoryRepository {
    private val db = FirebaseFirestore.getInstance()

    private val productRef = db.collection("products")
    private val lotRef = db.collection("inventory_lots")
    private val exportBillRef = db.collection("export_bills")
    private val importBillRef = db.collection("import_bills")
    private val exportBillDetailRef = db.collection("export_bill_details")
    private val importBillDetailRef = db.collection("import_bill_details")
    private val categoryRef = db.collection("categories")

    fun addProduct(product: Product, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val newDoc = productRef.document()
        newDoc.set(product.copy(productId = newDoc.id)).addOnSuccessListener { onSuccess(newDoc.id) }.addOnFailureListener { onFailure(it) }
    }

    suspend fun updateProduct(product: Product) = try { productRef.document(product.productId).set(product).await(); true } catch (e: Exception) { false }

    suspend fun deleteProduct(productId: String) = try { productRef.document(productId).delete().await(); true } catch (e: Exception) { false }

    fun getAllProducts(onResult: (List<Product>) -> Unit) {
        productRef.get().addOnSuccessListener { onResult(it.toObjects(Product::class.java)) }.addOnFailureListener { onResult(emptyList()) }
    }

    fun getProductsRealtime() = callbackFlow {
        val listener = productRef.addSnapshotListener { snapshot, _ -> trySend(snapshot?.toObjects(Product::class.java) ?: emptyList()) }
        awaitClose { listener.remove() }
    }

    suspend fun getCategories(): List<Category> = try { categoryRef.get().await().toObjects(Category::class.java) } catch (e: Exception) { emptyList() }

    suspend fun createImportBill(importBill: ImportBill, items: List<Pair<ImportBillDetail, InventoryLot>>): Boolean {
        return try {
            db.runTransaction { transaction ->
                val productUpdates = items.map { pair ->
                    val pRef = productRef.document(pair.first.productId)
                    val currentStock = transaction.get(pRef).getLong("totalStock") ?: 0L
                    Triple(pRef, currentStock, pair.first.quantity)
                }
                val bRef = importBillRef.document()
                transaction.set(bRef, importBill.copy(importBillId = bRef.id, date = importBill.date ?: Date()))
                items.forEachIndexed { i, pair ->
                    val dRef = importBillDetailRef.document()
                    transaction.set(dRef, pair.first.copy(importBillDetailId = dRef.id, importBillId = bRef.id, importBillDetailCode = "IBD${i + 1}"))
                    val lRef = lotRef.document()
                    transaction.set(lRef, pair.second.copy(lotId = lRef.id, importDate = importBill.date ?: Date(), lotCode = "LOT${System.currentTimeMillis() % 100000}_$i"))
                }
                productUpdates.forEach { (ref, stock, add) -> transaction.update(ref, "totalStock", stock + add) }
            }.await()
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    suspend fun updateImportBillTransaction(
        updatedBill: ImportBill,
        newItems: List<Pair<ImportBillDetail, InventoryLot>>,
        oldDetails: List<ImportBillDetail>
    ): Boolean {
        return try {
            db.runTransaction { transaction ->
                oldDetails.forEach { oldDetail ->
                    val pRef = productRef.document(oldDetail.productId)
                    val currentStock = transaction.get(pRef).getLong("totalStock") ?: 0L
                    transaction.update(pRef, "totalStock", currentStock - oldDetail.quantity)
                }
                val bRef = importBillRef.document(updatedBill.importBillId)
                transaction.set(bRef, updatedBill)
                oldDetails.forEach { 
                    transaction.delete(importBillDetailRef.document(it.importBillDetailId))
                }
                newItems.forEachIndexed { i, pair ->
                    val detail = pair.first
                    val lot = pair.second
                    val dRef = importBillDetailRef.document()
                    transaction.set(dRef, detail.copy(importBillDetailId = dRef.id, importBillId = updatedBill.importBillId, importBillDetailCode = "IBD${i + 1}"))
                    val lRef = lotRef.document()
                    transaction.set(lRef, lot.copy(lotId = lRef.id, importDate = updatedBill.date, lotCode = "LOT${System.currentTimeMillis() % 100000}_$i"))
                    val pRef = productRef.document(detail.productId)
                    val currentStock = transaction.get(pRef).getLong("totalStock") ?: 0L
                    transaction.update(pRef, "totalStock", currentStock + detail.quantity)
                }
            }.await()
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    // HÀM XÓA PHIẾU NHẬP (Transaction)
    suspend fun deleteImportBillTransaction(billId: String): Boolean {
        return try {
            val details = getImportBillDetails(billId)
            db.runTransaction { transaction ->
                // 1. GIAI ĐOẠN ĐỌC (READ PHASE)
                // Gom nhóm theo productId để tránh đọc 1 sản phẩm nhiều lần
                val updates = details.groupBy { it.productId }.map { (productId, prodDetails) ->
                    val pRef = productRef.document(productId)
                    val currentStock = transaction.get(pRef).getLong("totalStock") ?: 0L
                    val totalQtyToReduce = prodDetails.sumOf { it.quantity }
                    pRef to (currentStock - totalQtyToReduce)
                }

                // 2. GIAI ĐOẠN GHI (WRITE PHASE)
                updates.forEach { (ref, newStock) ->
                    transaction.update(ref, "totalStock", newStock)
                }
                details.forEach {
                    transaction.delete(importBillDetailRef.document(it.importBillDetailId))
                }
                transaction.delete(importBillRef.document(billId))
            }.await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    fun getImportBillsFlow() = callbackFlow {
        val listener = importBillRef.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { s, _ -> trySend(s?.toObjects(ImportBill::class.java) ?: emptyList()) }
        awaitClose { listener.remove() }
    }

    suspend fun updateInventoryLot(lot: InventoryLot): Boolean {
        return try {
            lotRef.document(lot.lotId).set(lot).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAllInventoryLots(): List<InventoryLot> {
        return try {
            lotRef.get().await().toObjects(InventoryLot::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getImportBillById(id: String) = try { importBillRef.document(id).get().await().toObject(ImportBill::class.java) } catch (e: Exception) { null }

    suspend fun getImportBillDetails(id: String) = try { importBillDetailRef.whereEqualTo("importBillId", id).get().await().toObjects(ImportBillDetail::class.java) } catch (e: Exception) { emptyList() }

    suspend fun getProductById(id: String) = try { productRef.document(id).get().await().toObject(Product::class.java) } catch (e: Exception) { null }

    suspend fun getExportBillById(id: String) = try { exportBillRef.document(id).get().await().toObject(ExportBill::class.java) } catch (e: Exception) { null }

    suspend fun getExportBillDetails(id: String) = try { exportBillDetailRef.whereEqualTo("exportBillId", id).get().await().toObjects(ExportBillDetail::class.java) } catch (e: Exception) { emptyList() }

    suspend fun getInventoryLotById(id: String) = try { lotRef.document(id).get().await().toObject(InventoryLot::class.java) } catch (e: Exception) { null }

    suspend fun createExportBill(exportBill: ExportBill, details: List<ExportBillDetail>): Boolean {
        return try {
            val lotsPerProduct = details.associate { detail ->
                val lots = lotRef.whereEqualTo("productId", detail.productId).get().await().toObjects(InventoryLot::class.java)
                    .filter { it.currentQuantity > 0 }.sortedWith(compareBy<InventoryLot> { it.expiryDate }.thenBy { it.importDate })
                detail.productId to lots
            }
            db.runTransaction { transaction ->
                val billData = details.map { detail ->
                    val pRef = productRef.document(detail.productId)
                    val pSnap = transaction.get(pRef)
                    val totalStock = pSnap.getLong("totalStock") ?: 0L
                    if (totalStock < detail.quantity) throw FirebaseFirestoreException("Hết hàng", FirebaseFirestoreException.Code.ABORTED)
                    val lotRefsWithQty = (lotsPerProduct[detail.productId] ?: emptyList()).map { lot ->
                        val lRef = lotRef.document(lot.lotId)
                        val lSnap = transaction.get(lRef)
                        val currentQty = lSnap.getLong("currentQuantity") ?: 0L
                        lRef to currentQty
                    }
                    Triple(pRef, totalStock, lotRefsWithQty)
                }
                val bRef = exportBillRef.document()
                transaction.set(bRef, exportBill.copy(exportBillId = bRef.id, date = Date()))
                details.forEachIndexed { i, detail ->
                    val (pRef, totalStock, lotRefsWithQty) = billData[i]
                    val dRef = exportBillDetailRef.document()
                    transaction.set(dRef, detail.copy(exportBillDetailId = dRef.id, exportBillId = bRef.id, exportBillDetailCode = "EBD${i + 1}"))
                    var rem = detail.quantity
                    for ((lRef, qty) in lotRefsWithQty) {
                        if (rem <= 0) break
                        val take = minOf(qty.toInt(), rem)
                        transaction.update(lRef, "currentQuantity", qty - take)
                        rem -= take
                    }
                    transaction.update(pRef, "totalStock", totalStock - detail.quantity)
                }
            }.await()
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    fun getExportBillsFlow() = callbackFlow {
        val listener = exportBillRef.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { s, _ -> trySend(s?.toObjects(ExportBill::class.java) ?: emptyList()) }
        awaitClose { listener.remove() }
    }
    // HÀM XÓA PHIẾU XUẤT (Transaction)
    suspend fun deleteExportBillTransaction(billId: String): Boolean {
        return try {
            val details = getExportBillDetails(billId)
            db.runTransaction { transaction ->
                // 1. GIAI ĐOẠN ĐỌC (READ PHASE)
                val updates = details.groupBy { it.productId }.map { (productId, prodDetails) ->
                    val pRef = productRef.document(productId)
                    val currentStock = transaction.get(pRef).getLong("totalStock") ?: 0L
                    val totalQtyToAdd = prodDetails.sumOf { it.quantity }
                    pRef to (currentStock + totalQtyToAdd)
                }

                // 2. GIAI ĐOẠN GHI (WRITE PHASE)
                updates.forEach { (ref, newStock) ->
                    transaction.update(ref, "totalStock", newStock)
                }
                details.forEach {
                    transaction.delete(exportBillDetailRef.document(it.exportBillDetailId))
                }
                transaction.delete(exportBillRef.document(billId))
            }.await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    fun getInventoryLotsByProductIdFlow(id: String) = callbackFlow {
        val listener = lotRef.whereEqualTo("productId", id).orderBy("importDate", Query.Direction.DESCENDING).addSnapshotListener { s, _ -> trySend(s?.toObjects(InventoryLot::class.java) ?: emptyList()) }
        awaitClose { listener.remove() }
    }
}
