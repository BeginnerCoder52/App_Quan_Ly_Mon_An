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

    fun loadCategories() {
        viewModelScope.launch {
            val list = repository.getCategories()
            _categories.value = list
        }
    }

    fun addCategory(categoryName: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            val success = repository.addCategory(Category(categoryName = categoryName))
            if (success) {
                loadCategories()
                onSuccess()
            } else {
                onFailure("Không thể thêm phân loại mới")
            }
        }
    }

    // HIEN'S CODE BEGIN
    // Hàm giả lập check thông tin từ Barcode
    // Trả về Product mẫu nếu tìm thấy, null nếu không thấy
    fun checkBarcodeInfo(code: String): Product? {
        return if (code == "8934609602537") {
            Product(
                productCode = "HURA01",
                productName = "Bánh Hura Swissroll (Dâu)",
                productCategory = "Bánh kẹo",
                unit = "Hộp",
                sellPrice = 25000.0,
                productImage = "https://cdn.hstatic.net/products/200000743311/hura_swissroll_360_gam_dau_842f240315094eaa935cb26f0ecaa7ba_master.png" // Demo link
            )
        } else {
            null
        }
    }
    // HIEN'S CODE END

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
