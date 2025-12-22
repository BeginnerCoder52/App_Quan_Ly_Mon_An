package com.example.app_quan_ly_do_an.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
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
        route = "stock?tab={tab}",
        title = "Kho",
        // Đổi từ QrCode2 sang Inventory (hoặc AllInbox nếu Inventory bị lỗi đỏ)
        icon = Icons.Default.Inventory
    ) {
        fun createRoute(tab: Int = 0) = "stock?tab=$tab"
    }

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

    //HIEN'S CODE BEGIN
    object AddImportBill : NavigationItem(
        route = "add_import_bill",
        title = "Tạo phiếu nhập",
        icon = Icons.Default.Add // Icon tạm, không quan trọng vì không hiện ở BottomBar
    )

    object ImportBillDetail : NavigationItem(
        route = "import_bill_detail/{billId}",
        title = "Chi tiết phiếu nhập",
        icon = Icons.Default.Info
    ) {
        fun createRoute(billId: String) = "import_bill_detail/$billId"
    }
    //HIEN'S CODE END

    object ProductDetail : NavigationItem(
        route = "product_detail/{productId}",
        title = "Chi tiết hàng hóa",
        icon = Icons.Default.Info
    ) {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object AddExportBill : NavigationItem(
        route = "add_export_bill",
        title = "Tạo phiếu xuất",
        icon = Icons.Default.Add // Icon tạm, không quan trọng vì không hiện ở BottomBar
    )

    object ExportBillDetail : NavigationItem(
        route = "export_bill_detail/{billId}",
        title = "Chi tiết phiếu xuất",
        icon = Icons.Default.Info
    ) {
        fun createRoute(billId: String) = "export_bill_detail/$billId"
    }
}

val navigationItems = listOf(
    NavigationItem.Home,
    NavigationItem.Product,
    NavigationItem.Stock,
    NavigationItem.Notification,
    NavigationItem.Profile,
)
