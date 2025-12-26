package com.example.app_quan_ly_do_an.ui.viewmodel.import_bill

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.model.*
import com.example.app_quan_ly_do_an.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class EditImportBillViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _saveSuccess = MutableStateFlow<Boolean?>(null)
    val saveSuccess: StateFlow<Boolean?> = _saveSuccess

    // Form State
    var billCode = mutableStateOf("")
    var supplier = mutableStateOf("")
    var billDate = mutableStateOf(Date())
    val importItems = mutableStateListOf<ImportItemState>()

    private var originalBill: ImportBill? = null
    private var originalDetails = listOf<ImportBillDetail>()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getAllProducts { _products.value = it }
        }
    }

    fun loadBillData(billId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val bill = repository.getImportBillById(billId)
            if (bill != null) {
                originalBill = bill
                billCode.value = bill.importBillIdCode
                supplier.value = bill.supplier
                billDate.value = bill.date ?: Date()

                val details = repository.getImportBillDetails(billId)
                originalDetails = details
                importItems.clear()
                details.forEach { detail ->
                    val product = _products.value.find { it.productId == detail.productId }
                    importItems.add(ImportItemState().apply {
                        selectedProduct.value = product
                        quantity.value = detail.quantity.toString()
                        price.value = detail.importPrice.toString()
                        expiryDate.value = detail.expiryDate
                        detailId = detail.importBillDetailId
                    })
                }
            }
            _isLoading.value = false
        }
    }

    fun addImportItem() {
        importItems.add(ImportItemState())
    }

    fun removeImportItem(item: ImportItemState) {
        importItems.remove(item)
    }

    fun updateImportBill() {
        val billId = originalBill?.importBillId ?: return
        if (billCode.value.isBlank() || importItems.isEmpty()) return

        viewModelScope.launch {
            _isSaving.value = true
            
            val totalAmount = importItems.sumOf { 
                (it.quantity.value.toDoubleOrNull() ?: 0.0) * (it.price.value.toDoubleOrNull() ?: 0.0)
            }

            val updatedBill = originalBill!!.copy(
                importBillIdCode = billCode.value,
                supplier = supplier.value,
                date = billDate.value,
                totalAmount = totalAmount
            )

            val itemsToSave = importItems.map { itemState ->
                val product = itemState.selectedProduct.value
                val productId = product?.productId ?: ""
                val quantity = itemState.quantity.value.toIntOrNull() ?: 0
                val price = itemState.price.value.toDoubleOrNull() ?: 0.0
                
                val detail = ImportBillDetail(
                    importBillDetailId = itemState.detailId,
                    productId = productId,
                    quantity = quantity,
                    importPrice = price,
                    expiryDate = itemState.expiryDate.value
                )
                
                val lot = InventoryLot(
                    productId = productId,
                    importPrice = price,
                    initialQuantity = quantity,
                    currentQuantity = quantity,
                    expiryDate = itemState.expiryDate.value,
                    location = "Kho A1"
                )
                
                Pair(detail, lot)
            }

            // Lưu ý: Logic cập nhật phiếu nhập phức tạp vì liên quan đến tồn kho.
            // Trong phạm vi này, mình sẽ thực hiện xóa cũ và tạo mới hoặc cập nhật transaction.
            // Để đơn giản và an toàn cho dữ liệu tồn kho, mình sẽ gọi một hàm repo chuyên biệt.
            val result = repository.updateImportBillTransaction(updatedBill, itemsToSave, originalDetails)
            _saveSuccess.value = result
            _isSaving.value = false
        }
    }

    fun resetSaveStatus() {
        _saveSuccess.value = null
    }

    class ImportItemState {
        var detailId: String = ""
        var selectedProduct = mutableStateOf<Product?>(null)
        var quantity = mutableStateOf("")
        var price = mutableStateOf("")
        var expiryDate = mutableStateOf<Date?>(null)
    }
}
