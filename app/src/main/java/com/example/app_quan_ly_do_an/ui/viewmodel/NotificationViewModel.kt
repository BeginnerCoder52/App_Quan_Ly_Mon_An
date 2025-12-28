package com.example.app_quan_ly_do_an.ui.viewmodel

// HIEN'S CODE BEGIN
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.model.AppNotification
import com.example.app_quan_ly_do_an.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications

    init {
        viewModelScope.launch {
            repository.getNotificationsFlow().collect { list ->
                _notifications.value = list
            }
        }
    }
}
// HIEN'S CODE END