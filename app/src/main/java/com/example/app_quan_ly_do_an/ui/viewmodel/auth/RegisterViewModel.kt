package com.example.app_quan_ly_do_an.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun register(
        fullName: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Giả lập đăng ký với delay
                delay(1500)

                // TODO: Thay bằng logic đăng ký thật từ Firebase hoặc API
                // Ví dụ: authRepository.register(fullName, email, password)

                // Giả lập kiểm tra đơn giản
                if (email.contains("@") && password.length >= 6) {
                    // Đăng ký thành công
                    onSuccess()
                } else {
                    onError("Email hoặc mật khẩu không hợp lệ")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Đăng ký thất bại")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
