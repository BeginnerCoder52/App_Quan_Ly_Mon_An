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

class EditExportBillViewModel : ViewModel() {
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
    var billDate = mutableStateOf(Date())
    val exportItems = mutableStateListOf<ExportItemState>()

    private var originalBill: ExportBill? = null
    private var originalDetails = listOf<ExportBillDetail>()

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
            val bill = repository.getExportBillById(billId)
            if (bill != null) {
                originalBill = bill
                billCode.value = bill.exportBillCode
                billDate.value = bill.date ?: Date()

                val details = repository.getExportBillDetails(billId)
                originalDetails = details
                exportItems.clear()
                details.forEach { detail ->
                    val product = _products.value.find { it.productId == detail.productId }
                    exportItems.add(ExportItemState().apply {
                        selectedProduct.value = product
                        quantity.value = detail.quantity.toString()
                        price.value = detail.sellPrice.toInt().toString()
                        detailId = detail.exportBillDetailId
                    })
                }
            }
            _isLoading.value = false
        }
    }

    fun addExportItem() {
        exportItems.add(ExportItemState())
    }

    fun removeExportItem(item: ExportItemState) {
        exportItems.remove(item)
    }

    fun updateExportBill() {
        val billId = originalBill?.exportBillId ?: return
        if (billCode.value.isBlank() || exportItems.isEmpty()) return

        viewModelScope.launch {
            _isSaving.value = true
            
            val totalAmount = exportItems.sumOf { 
                (it.quantity.value.toDoubleOrNull() ?: 0.0) * (it.price.value.toDoubleOrNull() ?: 0.0)
            }

            val updatedBill = originalBill!!.copy(
                exportBillCode = billCode.value,
                date = billDate.value,
                totalAmount = totalAmount
            )

            val newDetails = exportItems.map { itemState ->
                ExportBillDetail(
                    exportBillDetailId = itemState.detailId,
                    exportBillId = billId,
                    productId = itemState.selectedProduct.value?.productId ?: "",
                    quantity = itemState.quantity.value.toIntOrNull() ?: 0,
                    sellPrice = itemState.price.value.toDoubleOrNull() ?: 0.0
                )
            }

            val result = repository.updateExportBillTransaction(updatedBill, newDetails, originalDetails)
            _saveSuccess.value = result
            _isSaving.value = false
        }
    }

    fun resetSaveStatus() {
        _saveSuccess.value = null
    }

    class ExportItemState {
        var detailId: String = ""
        var selectedProduct = mutableStateOf<Product?>(null)
        var quantity = mutableStateOf("")
        var price = mutableStateOf("")
    }
}
