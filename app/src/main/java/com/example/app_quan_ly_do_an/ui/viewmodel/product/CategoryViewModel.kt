package com.example.app_quan_ly_do_an.ui.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.model.Category
import com.example.app_quan_ly_do_an.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            val list = repository.getCategories()
            _categories.value = list
            _isLoading.value = false
        }
    }

    fun addCategory(name: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (name.isBlank()) {
            onFailure("Tên phân loại không được để trống")
            return
        }
        viewModelScope.launch {
            val success = repository.addCategory(Category(categoryName = name))
            if (success) {
                loadCategories()
                onSuccess()
            } else {
                onFailure("Lỗi khi thêm phân loại")
            }
        }
    }

    fun deleteCategory(categoryId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            val success = repository.deleteCategory(categoryId)
            if (success) {
                loadCategories()
                onSuccess()
            } else {
                onFailure("Lỗi khi xóa phân loại")
            }
        }
    }
}
