package com.example.app_quan_ly_do_an.ui.viewmodel.import_bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.model.ImportBill
import com.example.app_quan_ly_do_an.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ImportBillListViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _bills = MutableStateFlow<List<ImportBill>>(emptyList())
    val bills: StateFlow<List<ImportBill>> = _bills

    init {
        fetchBills()
    }

    private fun fetchBills() {
        viewModelScope.launch {
            // Lắng nghe dữ liệu realtime
            repository.getImportBillsFlow().collect { list ->
                _bills.value = list
            }
        }
    }
}
data class ImportDetailUiItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val importPrice: Double
)