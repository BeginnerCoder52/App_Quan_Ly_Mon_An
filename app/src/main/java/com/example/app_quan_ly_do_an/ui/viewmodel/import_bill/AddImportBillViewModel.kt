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

class AddImportBillViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    var billCode = mutableStateOf("")
    var supplier = mutableStateOf("")
    var billDate = mutableStateOf(Date())

    val importItems = mutableStateListOf<ImportItemState>()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _saveSuccess = MutableStateFlow<Boolean?>(null)
    val saveSuccess: StateFlow<Boolean?> = _saveSuccess

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getAllProducts { _products.value = it }
        }
    }

    fun addImportItem() {
        importItems.add(ImportItemState())
    }

    fun removeImportItem(item: ImportItemState) {
        importItems.remove(item)
    }

    fun saveImportBill() {
        // Validation cơ bản
        // 1. Tự sinh mã nếu người dùng để trống (Ví dụ: PN + thời gian hiện tại)
        if (billCode.value.isBlank()) {
            billCode.value = "PN${System.currentTimeMillis() / 1000}"
        }

        // 2. Chỉ kiểm tra danh sách sản phẩm
        if (importItems.isEmpty() || importItems.any {
                it.selectedProduct.value == null || it.quantity.value.isBlank() || it.price.value.isBlank()
            }) {
            _saveSuccess.value = false
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            
            // Tự động tính tổng tiền
            val totalAmount = importItems.sumOf { 
                (it.quantity.value.toDoubleOrNull() ?: 0.0) * (it.price.value.toDoubleOrNull() ?: 0.0)
            }

            val importBill = ImportBill(
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
                val expiryDate = itemState.expiryDate.value
                
                val detail = ImportBillDetail(
                    productId = productId,
                    quantity = quantity,
                    importPrice = price,
                    expiryDate = expiryDate
                    // importBillDetailCode sẽ được repository tự tạo (IBD1, IBD2...)
                )
                
                val lot = InventoryLot(
                    productId = productId,
                    importPrice = price,
                    initialQuantity = quantity,
                    currentQuantity = quantity,
                    expiryDate = expiryDate,
                    location = "Kho A1"
                )
                
                Pair(detail, lot)
            }

            val result = repository.createImportBill(importBill, itemsToSave)
            _saveSuccess.value = result
            _isSaving.value = false
        }
    }

    fun resetSaveStatus() {
        _saveSuccess.value = null
    }

    class ImportItemState {
        var selectedProduct = mutableStateOf<Product?>(null)
        var quantity = mutableStateOf("")
        var price = mutableStateOf("")
        var expiryDate = mutableStateOf<Date?>(null)
    }
}
