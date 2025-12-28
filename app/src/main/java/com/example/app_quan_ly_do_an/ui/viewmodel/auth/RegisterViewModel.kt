package com.example.app_quan_ly_do_an.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val authRepository = AuthRepository()

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
                val result = authRepository.register(fullName, email, password)

                if (result.isSuccess) {
                    // Đăng ký thành công
                    onSuccess()
                } else {
                    // Đăng ký thất bại
                    val error = result.exceptionOrNull()
                    onError(getErrorMessage(error))
                }
            } catch (e: Exception) {
                onError(getErrorMessage(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getErrorMessage(exception: Throwable?): String {
        return when {
            exception?.message?.contains("already in use") == true -> "Email đã được sử dụng"
            exception?.message?.contains("weak-password") == true -> "Mật khẩu quá yếu"
            exception?.message?.contains("invalid-email") == true -> "Email không hợp lệ"
            exception?.message?.contains("network") == true -> "Lỗi kết nối mạng"
            else -> exception?.message ?: "Đăng ký thất bại"
        }
    }
}
