package com.example.app_quan_ly_do_an.ui.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.model.InventoryLot
import com.example.app_quan_ly_do_an.data.model.Product
import com.example.app_quan_ly_do_an.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EditBatchUiState(
    val lot: InventoryLot? = null,
    val product: Product? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false
)

class EditBatchViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _uiState = MutableStateFlow(EditBatchUiState())
    val uiState: StateFlow<EditBatchUiState> = _uiState

    fun loadBatchData(lotId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val lot = repository.getInventoryLotById(lotId)
            val product = lot?.let { repository.getProductById(it.productId) }
            
            _uiState.value = _uiState.value.copy(
                lot = lot,
                product = product,
                isLoading = false
            )
        }
    }

    fun updateBatch(
        lot: InventoryLot,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val result = repository.updateInventoryLot(lot)
            _uiState.value = _uiState.value.copy(isSaving = false)
            
            if (result) {
                onSuccess()
            } else {
                onFailure("Không thể cập nhật lô hàng")
            }
        }
    }
}
