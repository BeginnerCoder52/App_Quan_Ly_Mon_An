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

    // HIEN'S CODE BEGIN
    private val notificationRef = db.collection("notifications")

    // Hàm ghi nhật ký hoạt động
    private fun logActivity(title: String, content: String) {
        val notif = AppNotification(
            title = title,
            content = content,
            timestamp = java.util.Date()
        )
        notificationRef.add(notif)
    }

    // Hàm lấy danh sách thông báo (cho màn hình Notification)
    fun getNotificationsFlow() = kotlinx.coroutines.flow.callbackFlow {
        val listener = notificationRef
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { s, _ ->
                trySend(s?.toObjects(AppNotification::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }
    // HIEN'S CODE END
    private val productRef = db.collection("products")
    private val lotRef = db.collection("inventory_lots")
    private val exportBillRef = db.collection("export_bills")
    private val importBillRef = db.collection("import_bills")
    private val exportBillDetailRef = db.collection("export_bill_details")
    private val importBillDetailRef = db.collection("import_bill_details")
    private val categoryRef = db.collection("categories")

    fun addProduct(product: Product, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val newDoc = productRef.document()
        newDoc.set(product.copy(productId = newDoc.id)).addOnSuccessListener {
            logActivity("Thêm hàng hóa", "Đã thêm mới: ${product.productName}")
            onSuccess(newDoc.id)
        }
            .addOnFailureListener { onFailure(it) }
    }

    suspend fun updateProduct(product: Product) = try {
        productRef.document(product.productId).set(product).await();
        // HIEN'S CODE BEGIN
        logActivity("Cập nhật hàng hóa", "Đã sửa thông tin: ${product.productName}")
        // HIEN'S CODE END
        true
    } catch (e: Exception) { false }

    suspend fun deleteProduct(productId: String) = try {
        // HIEN'S CODE BEGIN
        // Lấy tên sản phẩm trước khi xóa để ghi log cho đẹp
        val snapshot = productRef.document(productId).get().await()
        val name = snapshot.getString("productName") ?: "Sản phẩm $productId"

        productRef.document(productId).delete().await()
        logActivity("Xóa hàng hóa", "Đã xóa vĩnh viễn: $name")
        // HIEN'S CODE END
        true
    } catch (e: Exception) { false }

    fun getAllProducts(onResult: (List<Product>) -> Unit) {
        productRef.get().addOnSuccessListener {
            onResult(it.toObjects(Product::class.java)) }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getProductsRealtime() = callbackFlow {
        val listener = productRef.addSnapshotListener { snapshot, _ -> trySend(snapshot?.toObjects(Product::class.java) ?: emptyList()) }
        awaitClose { listener.remove() }
    }

    suspend fun getCategories(): List<Category> = try { categoryRef.get().await().toObjects(Category::class.java) } catch (e: Exception) { emptyList() }

    suspend fun addCategory(category: Category): Boolean = try {
        val newDoc = categoryRef.document()
        categoryRef.document(newDoc.id).set(category.copy(categoryId = newDoc.id)).await()
        logActivity("Thêm phân loại", "Đã thêm mới phân loại: ${category.categoryName}")
        true
    } catch (e: Exception) {
        false
    }

    suspend fun deleteCategory(categoryId: String): Boolean = try {
        val snapshot = categoryRef.document(categoryId).get().await()
        val name = snapshot.getString("categoryName") ?: "Phân loại $categoryId"
        categoryRef.document(categoryId).delete().await()
        logActivity("Xóa phân loại", "Đã xóa phân loại: $name")
        true
    } catch (e: Exception) {
        false
    }

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
                    transaction.set(lRef, pair.second.copy(
                        lotId = lRef.id, 
                        importBillId = bRef.id,
                        importDate = importBill.date ?: Date(), 
                        lotCode = "LOT${System.currentTimeMillis() % 100000}_$i"
                    ))
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
            val oldLots = lotRef.whereEqualTo("importBillId", updatedBill.importBillId).get().await().toObjects(InventoryLot::class.java)

            db.runTransaction { transaction ->
                val productIds = (oldDetails.map { it.productId } + newItems.map { it.first.productId }).distinct()
                val productSnapshots = productIds.associateWith { id ->
                    transaction.get(productRef.document(id))
                }

                val stockChanges = mutableMapOf<String, Long>()
                oldDetails.forEach { old ->
                    stockChanges[old.productId] = (stockChanges[old.productId] ?: 0L) - old.quantity
                }

                oldDetails.forEach {
                    transaction.delete(importBillDetailRef.document(it.importBillDetailId))
                }
                oldLots.forEach { lot ->
                    transaction.delete(lotRef.document(lot.lotId))
                }

                val bRef = importBillRef.document(updatedBill.importBillId)
                transaction.set(bRef, updatedBill)

                newItems.forEachIndexed { i, pair ->
                    val detail = pair.first
                    val lot = pair.second

                    val dRef = importBillDetailRef.document()
                    transaction.set(dRef, detail.copy(
                        importBillDetailId = dRef.id,
                        importBillId = updatedBill.importBillId,
                        importBillDetailCode = "IBD${i + 1}"
                    ))

                    val lRef = lotRef.document()
                    transaction.set(lRef, lot.copy(
                        lotId = lRef.id,
                        importBillId = updatedBill.importBillId, 
                        importDate = updatedBill.date,
                        lotCode = "LOT${System.currentTimeMillis() % 100000}_$i"
                    ))

                    stockChanges[detail.productId] = (stockChanges[detail.productId] ?: 0L) + detail.quantity
                }

                stockChanges.forEach { (productId, change) ->
                    val snapshot = productSnapshots[productId]
                    val currentTotalStock = snapshot?.getLong("totalStock") ?: 0L
                    transaction.update(productRef.document(productId), "totalStock", currentTotalStock + change)
                }
            }.await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteImportBillTransaction(billId: String): Boolean {
        return try {
            val details = getImportBillDetails(billId)
            val lots = lotRef.whereEqualTo("importBillId", billId).get().await().toObjects(InventoryLot::class.java)
            
            db.runTransaction { transaction ->
                val updates = details.groupBy { it.productId }.map { (productId, prodDetails) ->
                    val pRef = productRef.document(productId)
                    val currentStock = transaction.get(pRef).getLong("totalStock") ?: 0L
                    val totalQtyToReduce = prodDetails.sumOf { it.quantity }
                    pRef to (currentStock - totalQtyToReduce)
                }

                updates.forEach { (ref, newStock) ->
                    transaction.update(ref, "totalStock", newStock)
                }
                details.forEach {
                    transaction.delete(importBillDetailRef.document(it.importBillDetailId))
                }
                lots.forEach {
                    transaction.delete(lotRef.document(it.lotId))
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
            // HIEN'S CODE BEGIN
            logActivity("Cập nhật lô hàng", "Đã sửa lô: ${lot.lotCode}")
            // HIEN'S CODE END
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

    suspend fun updateExportBillTransaction(
        updatedBill: ExportBill,
        newDetails: List<ExportBillDetail>,
        oldDetails: List<ExportBillDetail>
    ): Boolean {
        return try {
            val productIds = (oldDetails.map { it.productId } + newDetails.map { it.productId }).distinct()
            val allLotsMap = productIds.associateWith { pid ->
                lotRef.whereEqualTo("productId", pid).get().await().toObjects(InventoryLot::class.java)
                    .sortedWith(compareBy<InventoryLot> { it.expiryDate }.thenBy { it.importDate })
            }

            db.runTransaction { transaction ->
                val productSnapshots = productIds.associateWith { id ->
                    transaction.get(productRef.document(id))
                }

                val lotRefsWithQtyMap = mutableMapOf<String, Long>()
                allLotsMap.values.flatten().forEach { lot ->
                    val qty = transaction.get(lotRef.document(lot.lotId)).getLong("currentQuantity") ?: 0L
                    lotRefsWithQtyMap[lot.lotId] = qty
                }

                val stockChanges = mutableMapOf<String, Long>()
                oldDetails.forEach { old ->
                    stockChanges[old.productId] = (stockChanges[old.productId] ?: 0L) + old.quantity
                    val targetLot = allLotsMap[old.productId]?.firstOrNull()
                    if (targetLot != null) {
                        val currentQtyInMap = lotRefsWithQtyMap[targetLot.lotId] ?: 0L
                        val newQty = currentQtyInMap + old.quantity
                        lotRefsWithQtyMap[targetLot.lotId] = newQty
                        transaction.update(lotRef.document(targetLot.lotId), "currentQuantity", newQty)
                    }
                    transaction.delete(exportBillDetailRef.document(old.exportBillDetailId))
                }

                transaction.set(exportBillRef.document(updatedBill.exportBillId), updatedBill)

                newDetails.forEachIndexed { i, detail ->
                    val dRef = exportBillDetailRef.document()
                    transaction.set(dRef, detail.copy(
                        exportBillDetailId = dRef.id,
                        exportBillId = updatedBill.exportBillId,
                        exportBillDetailCode = "EBD${i + 1}"
                    ))

                    var rem = detail.quantity
                    val productLots = allLotsMap[detail.productId] ?: emptyList()
                    val snapshot = productSnapshots[detail.productId]
                    val dbTotalStock = snapshot?.getLong("totalStock") ?: 0L
                    val availableStock = dbTotalStock + (stockChanges[detail.productId] ?: 0L)
                    
                    if (availableStock < detail.quantity) {
                         throw FirebaseFirestoreException("Hết hàng", FirebaseFirestoreException.Code.ABORTED)
                    }

                    for (lot in productLots) {
                        if (rem <= 0) break
                        val currentQty = lotRefsWithQtyMap[lot.lotId] ?: 0L
                        if (currentQty <= 0) continue
                        val take = minOf(currentQty.toInt(), rem)
                        val newQty = currentQty - take
                        transaction.update(lotRef.document(lot.lotId), "currentQuantity", newQty)
                        lotRefsWithQtyMap[lot.lotId] = newQty
                        rem -= take
                    }
                    stockChanges[detail.productId] = (stockChanges[detail.productId] ?: 0L) - detail.quantity
                }

                stockChanges.forEach { (productId, change) ->
                    val snapshot = productSnapshots[productId]
                    val currentTotalStock = snapshot?.getLong("totalStock") ?: 0L
                    transaction.update(productRef.document(productId), "totalStock", currentTotalStock + change)
                }
            }.await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getExportBillsFlow() = callbackFlow {
        val listener = exportBillRef.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { s, _ -> trySend(s?.toObjects(ExportBill::class.java) ?: emptyList()) }
        awaitClose { listener.remove() }
    }
    
    suspend fun deleteExportBillTransaction(billId: String): Boolean {
        return try {
            val details = getExportBillDetails(billId)
            val productIds = details.map { it.productId }.distinct()
            
            // Lấy danh sách lô hàng bên ngoài transaction
            val allLotsMap = productIds.associateWith { pid ->
                lotRef.whereEqualTo("productId", pid).get().await().toObjects(InventoryLot::class.java)
                    .sortedWith(compareBy<InventoryLot> { it.expiryDate }.thenBy { it.importDate })
            }

            db.runTransaction { transaction ->
                // 1. READ PHASE
                val productSnapshots = productIds.associateWith { id ->
                    transaction.get(productRef.document(id))
                }
                
                val lotRefsWithQtyMap = mutableMapOf<String, Long>()
                allLotsMap.values.flatten().forEach { lot ->
                    val qty = transaction.get(lotRef.document(lot.lotId)).getLong("currentQuantity") ?: 0L
                    lotRefsWithQtyMap[lot.lotId] = qty
                }

                // 2. WRITE PHASE - Hoàn trả số lượng vào product và lot
                val stockChanges = mutableMapOf<String, Long>()
                details.forEach { detail ->
                    // Cộng lại vào totalStock
                    stockChanges[detail.productId] = (stockChanges[detail.productId] ?: 0L) + detail.quantity
                    
                    // Cộng lại vào một lô hàng (Ưu tiên lô hàng đầu tiên có hạn dùng gần nhất)
                    val targetLot = allLotsMap[detail.productId]?.firstOrNull()
                    if (targetLot != null) {
                        val currentQty = lotRefsWithQtyMap[targetLot.lotId] ?: 0L
                        val newQty = currentQty + detail.quantity
                        transaction.update(lotRef.document(targetLot.lotId), "currentQuantity", newQty)
                        lotRefsWithQtyMap[targetLot.lotId] = newQty
                    }
                    
                    transaction.delete(exportBillDetailRef.document(detail.exportBillDetailId))
                }

                stockChanges.forEach { (productId, change) ->
                    val snapshot = productSnapshots[productId]
                    val currentTotalStock = snapshot?.getLong("totalStock") ?: 0L
                    transaction.update(productRef.document(productId), "totalStock", currentTotalStock + change)
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
