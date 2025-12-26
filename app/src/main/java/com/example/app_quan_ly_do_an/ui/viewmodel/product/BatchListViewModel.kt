package com.example.app_quan_ly_do_an.ui.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.model.InventoryLot
import com.example.app_quan_ly_do_an.data.model.Product
import com.example.app_quan_ly_do_an.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BatchListUiState(
    val product: Product? = null,
    val batches: List<InventoryLot> = emptyList(),
    val isLoading: Boolean = false
)

class BatchListViewModel : ViewModel() {
    private val repository = InventoryRepository()

    // Khởi tạo isLoading = true để tránh flicker rỗng lúc đầu
    private val _uiState = MutableStateFlow(BatchListUiState(isLoading = true))
    val uiState: StateFlow<BatchListUiState> = _uiState

    private var currentId: String? = null

    fun loadBatches(productId: String) {
        if (currentId == productId) return // Tránh load lại nếu cùng ID
        currentId = productId

        viewModelScope.launch {
            // Giữ lại dữ liệu cũ nếu có, chỉ bật loading
            _uiState.value = _uiState.value.copy(isLoading = true)

            val product = repository.getProductById(productId)

            repository.getInventoryLotsByProductIdFlow(productId).collect { list ->
                _uiState.value = _uiState.value.copy(
                    product = product,
                    batches = list,
                    isLoading = false
                )
            }
        }
    }
}
