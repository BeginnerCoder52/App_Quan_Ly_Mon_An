package com.example.app_quan_ly_do_an.ui.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.model.InventoryLot
import com.example.app_quan_ly_do_an.data.model.Product
import com.example.app_quan_ly_do_an.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BatchDetailUiState(
    val product: Product? = null,
    val lot: InventoryLot? = null,
    val isLoading: Boolean = false
)

class BatchDetailViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _uiState = MutableStateFlow(BatchDetailUiState())
    val uiState: StateFlow<BatchDetailUiState> = _uiState

    fun loadLotDetail(lotId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val lot = repository.getInventoryLotById(lotId)
            val product = lot?.let { repository.getProductById(it.productId) }
            
            _uiState.value = BatchDetailUiState(
                product = product,
                lot = lot,
                isLoading = false
            )
        }
    }
}
