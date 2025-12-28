package com.example.app_quan_ly_do_an.ui.viewmodel.export_bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.model.ExportBill
import com.example.app_quan_ly_do_an.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ExportDetailUiItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val sellPrice: Double
)

data class ExportBillUiState(
    val billInfo: ExportBill? = null,
    val details: List<ExportDetailUiItem> = emptyList(),
    val isLoading: Boolean = false,
    val deleteSuccess: Boolean? = null
)

class ExportBillDetailViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _uiState = MutableStateFlow(ExportBillUiState())
    val uiState: StateFlow<ExportBillUiState> = _uiState

    fun loadBillDetails(billId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val bill = repository.getExportBillById(billId)
            val rawDetails = repository.getExportBillDetails(billId)

            val fullDetails = rawDetails.map { detail ->
                val product = repository.getProductById(detail.productId)
                ExportDetailUiItem(
                    productId = detail.productId,
                    productName = product?.productName ?: "Sản phẩm đã xóa",
                    quantity = detail.quantity,
                    sellPrice = detail.sellPrice
                )
            }

            _uiState.value = ExportBillUiState(
                billInfo = bill,
                details = fullDetails,
                isLoading = false
            )
        }
    }

    fun deleteBill(billId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.deleteExportBillTransaction(billId)
            _uiState.value = _uiState.value.copy(isLoading = false, deleteSuccess = result)
        }
    }

    fun resetDeleteStatus() {
        _uiState.value = _uiState.value.copy(deleteSuccess = null)
    }
}
