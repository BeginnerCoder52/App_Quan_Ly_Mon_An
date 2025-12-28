package com.example.app_quan_ly_do_an.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository()

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
                val result = authRepository.login(email, password)

                if (result.isSuccess) {
                    // Đăng nhập thành công
                    onSuccess()
                } else {
                    // Đăng nhập thất bại
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
            exception?.message?.contains("password") == true -> "Mật khẩu không đúng"
            exception?.message?.contains("user") == true -> "Email không tồn tại"
            exception?.message?.contains("network") == true -> "Lỗi kết nối mạng"
            else -> exception?.message ?: "Đăng nhập thất bại"
        }
    }
}
