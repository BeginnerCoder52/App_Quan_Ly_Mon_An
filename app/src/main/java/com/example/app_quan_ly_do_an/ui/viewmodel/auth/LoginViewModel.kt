package com.example.app_quan_ly_do_an.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Giả lập đăng nhập với delay
                delay(1500)

                // TODO: Thay bằng logic đăng nhập thật từ Firebase hoặc API
                // Ví dụ: authRepository.login(email, password)

                // Giả lập kiểm tra đơn giản
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    // Đăng nhập thành công
                    onSuccess()
                } else {
                    onError("Email hoặc mật khẩu không được để trống")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Đăng nhập thất bại")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
