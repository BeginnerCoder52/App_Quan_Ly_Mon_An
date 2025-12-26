package com.example.app_quan_ly_do_an.ui.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.model.Category
import com.example.app_quan_ly_do_an.data.model.Product
import com.example.app_quan_ly_do_an.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddProductViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val list = repository.getCategories()
            _categories.value = list
        }
    }

    fun saveProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            repository.addProduct(
                product = product,
                onSuccess = {
                    _isSaving.value = false
                    onSuccess()
                },
                onFailure = {
                    _isSaving.value = false
                    onFailure(it.message ?: "Lỗi không xác định")
                }
            )
        }
    }
}
