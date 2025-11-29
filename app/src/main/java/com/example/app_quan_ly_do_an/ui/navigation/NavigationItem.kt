package com.example.app_quan_ly_do_an.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : NavigationItem(
        route = "home",
        title = "Trang chủ",
        icon = Icons.Default.Home
    )

    object Product : NavigationItem(
        route = "product",
        title = "Hàng hóa",
        icon = Icons.Default.DateRange
    )

    object Stock : NavigationItem(
        route = "stock",
        title = "Kho",
        icon = Icons.Default.QrCode2
    )

    object Notification : NavigationItem(
        route = "notification",
        title = "Thông báo",
        icon = Icons.Default.Notifications
    )

    object Profile : NavigationItem(
        route = "profile",
        title = "Tài khoản",
        icon = Icons.Default.Person
    )
}

val navigationItems = listOf(
    NavigationItem.Home,
    NavigationItem.Product,
    NavigationItem.Stock,
    NavigationItem.Notification,
    NavigationItem.Profile
)