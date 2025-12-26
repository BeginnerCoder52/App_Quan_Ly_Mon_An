package com.example.app_quan_ly_do_an.ui.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.model.Category
import com.example.app_quan_ly_do_an.data.model.Product
import com.example.app_quan_ly_do_an.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EditProductUiState(
    val product: Product? = null,
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

class EditProductViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _uiState = MutableStateFlow(EditProductUiState())
    val uiState: StateFlow<EditProductUiState> = _uiState

    fun loadProductData(productId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val product = repository.getProductById(productId)
            val categories = repository.getCategories()
            
            _uiState.value = _uiState.value.copy(
                product = product,
                categories = categories,
                isLoading = false
            )
        }
    }

    fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val result = repository.updateProduct(product)
            _uiState.value = _uiState.value.copy(isSaving = false)
            
            if (result) {
                onSuccess()
            } else {
                onFailure("Không thể cập nhật sản phẩm")
            }
        }
    }
}
