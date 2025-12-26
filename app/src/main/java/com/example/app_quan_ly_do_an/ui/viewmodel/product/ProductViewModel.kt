package com.example.app_quan_ly_do_an.ui.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.model.Product
import com.example.app_quan_ly_do_an.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val repository = InventoryRepository()

    // Biến chứa danh sách sản phẩm gốc từ DB
    private val _rawProducts = MutableStateFlow<List<Product>>(emptyList())
    
    // Biến chứa danh sách sản phẩm sau khi đã tính toán totalStock
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchProducts()
    }

    // Lấy dữ liệu Realtime và tính toán totalStock
    private fun fetchProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Lắng nghe sản phẩm và tính toán tồn kho dựa trên inventory_lots
            repository.getProductsRealtime().collect { productList ->
                val allLots = repository.getAllInventoryLots()
                
                val productsWithStock = productList.map { product ->
                    val totalStock = allLots
                        .filter { it.productId == product.productId }
                        .sumOf { it.currentQuantity }
                    
                    product.copy(totalStock = totalStock)
                }
                
                _products.value = productsWithStock
                _isLoading.value = false
            }
        }
    }

    // Tính tổng tồn kho (Dùng cho Header)
    fun getTotalStock(): Int {
        return _products.value.sumOf { it.totalStock }
    }
}
