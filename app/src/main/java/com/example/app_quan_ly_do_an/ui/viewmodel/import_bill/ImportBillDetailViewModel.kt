package com.example.app_quan_ly_do_an.ui.viewmodel.import_bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.model.ImportBill
import com.example.app_quan_ly_do_an.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Model gộp cho toàn màn hình
data class ImportBillUiState(
    val billInfo: ImportBill? = null,
    val details: List<ImportDetailUiItem> = emptyList(), // Danh sách đã nối tên SP
    val isLoading: Boolean = false,
    val deleteSuccess: Boolean? = null
)

class ImportBillDetailViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _uiState = MutableStateFlow(ImportBillUiState())
    val uiState: StateFlow<ImportBillUiState> = _uiState

    fun loadBillDetails(billId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // 1. Lấy thông tin chung của phiếu (Header)
            val bill = repository.getImportBillById(billId)

            // 2. Lấy danh sách chi tiết (chỉ có productId)
            val rawDetails = repository.getImportBillDetails(billId)

            // 3. Thực hiện JOIN: Lặp qua từng chi tiết để lấy tên Product
            val fullDetails = rawDetails.map { detail ->
                val product = repository.getProductById(detail.productId)
                ImportDetailUiItem(
                    productId = detail.productId,
                    productName = product?.productName ?: "Sản phẩm đã xóa", // Fallback nếu ko tìm thấy
                    quantity = detail.quantity,
                    importPrice = detail.importPrice
                )
            }

            // 4. Cập nhật UI
            _uiState.value = ImportBillUiState(
                billInfo = bill,
                details = fullDetails,
                isLoading = false
            )
        }
    }

    fun deleteBill(billId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.deleteImportBillTransaction(billId)
            _uiState.value = _uiState.value.copy(isLoading = false, deleteSuccess = result)
        }
    }

    fun resetDeleteStatus() {
        _uiState.value = _uiState.value.copy(deleteSuccess = null)
    }
}