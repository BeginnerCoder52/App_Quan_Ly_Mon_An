package com.example.app_quan_ly_do_an.data.model

data class User(
    val userId: String = "",           // Firebase Auth UID
    val email: String = "",            // Email đăng nhập
    val displayName: String = "",      // Tên hiển thị
    val createdAt: java.util.Date? = null,  // Ngày tạo tài khoản
    val role: String = "user"          // Role: user, admin, manager
)

