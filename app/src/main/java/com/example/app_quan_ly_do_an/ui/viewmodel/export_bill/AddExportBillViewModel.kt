package com.example.app_quan_ly_do_an.ui.viewmodel.export_bill

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

class AddExportBillViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _saveResult = MutableStateFlow<SaveResult>(SaveResult.Idle)
    val saveResult: StateFlow<SaveResult> = _saveResult

    var billCode = mutableStateOf("")
    var billDate = mutableStateOf(Date())
    val exportItems = mutableStateListOf<ExportItemState>()

    sealed class SaveResult {
        object Idle : SaveResult()
        object Success : SaveResult()
        data class Error(val message: String) : SaveResult()
    }

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getAllProducts { _products.value = it }
        }
    }

    fun addExportItem() {
        exportItems.add(ExportItemState())
    }

    fun removeExportItem(item: ExportItemState) {
        exportItems.remove(item)
    }

    fun saveExportBill() {
        if (billCode.value.isBlank() || exportItems.isEmpty()) {
            _saveResult.value = SaveResult.Error("Vui lòng nhập đầy đủ thông tin")
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            
            val totalAmount = exportItems.sumOf { 
                (it.quantity.value.toDoubleOrNull() ?: 0.0) * (it.price.value.toDoubleOrNull() ?: 0.0)
            }

            val exportBill = ExportBill(
                exportBillCode = billCode.value,
                date = billDate.value,
                totalAmount = totalAmount
            )

            val details = exportItems.map { itemState ->
                ExportBillDetail(
                    productId = itemState.selectedProduct.value?.productId ?: "",
                    quantity = itemState.quantity.value.toIntOrNull() ?: 0,
                    sellPrice = itemState.price.value.toDoubleOrNull() ?: 0.0
                )
            }

            val result = repository.createExportBill(exportBill, details)
            if (result) {
                _saveResult.value = SaveResult.Success
            } else {
                _saveResult.value = SaveResult.Error("Lỗi khi lưu phiếu xuất. Có thể do hết hàng trong kho.")
            }
            _isSaving.value = false
        }
    }

    fun resetState() {
        _saveResult.value = SaveResult.Idle
    }

    class ExportItemState {
        var selectedProduct = mutableStateOf<Product?>(null)
        var quantity = mutableStateOf("")
        var price = mutableStateOf("")
    }
}
