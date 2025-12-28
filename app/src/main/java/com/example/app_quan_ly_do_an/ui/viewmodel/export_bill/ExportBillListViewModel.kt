package com.example.app_quan_ly_do_an.ui.viewmodel.export_bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.model.ExportBill
import com.example.app_quan_ly_do_an.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExportBillListViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _bills = MutableStateFlow<List<ExportBill>>(emptyList())
    val bills: StateFlow<List<ExportBill>> = _bills

    init {
        fetchBills()
    }

    private fun fetchBills() {
        viewModelScope.launch {
            repository.getExportBillsFlow().collect { list ->
                _bills.value = list
            }
        }
    }
}
