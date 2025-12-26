package com.example.app_quan_ly_do_an.ui.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.model.Product
import com.example.app_quan_ly_do_an.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    // Hàm load dữ liệu
    fun loadProduct(id: String) {
        viewModelScope.launch {
            val result = repository.getProductById(id)
            _product.value = result
        }
    }

    // Hàm xóa
    fun deleteProduct(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val success = repository.deleteProduct(id)
            if (success) onSuccess()
        }
    }
}