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
    object EditImportBill : NavigationItem(
        route = "edit_import_bill/{billId}",
        title = "Sửa phiếu nhập"
    ) {
        fun createRoute(billId: String) = "edit_import_bill/$billId"
    }
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

    object EditExportBill : NavigationItem(
        route = "edit_export_bill/{billId}",
        title = "Chỉnh sửa phiếu xuất"
    ) {
        fun createRoute(billId: String) = "edit_export_bill/$billId"
    }
    object BatchList : NavigationItem(
        route = "batch_list/{productId}",
        title = "Danh sách lô hàng"
    ) {
        fun createRoute(productId: String) = "batch_list/$productId"
    }
    object BatchDetail : NavigationItem(
        route = "batch_detail/{batchId}",
        title = "Chi tiết lô hàng"
    ) {
        fun createRoute(batchId: String) = "batch_detail/$batchId"
    }
    object AddProduct : NavigationItem(
        route = "add_product",
        title = "Thêm hàng hóa"
    )

    object EditProduct : NavigationItem(
        route = "edit_product/{productId}",
        title = "Sửa hàng hóa"
    ) {
        fun createRoute(productId: String) = "edit_product/$productId"
    }

    object EditBatch : NavigationItem(
        route = "edit_batch/{batchId}",
        title = "Sửa lô hàng"
    ) {
        fun createRoute(batchId: String) = "edit_batch/$batchId"
    }

    object CategoryManagement : NavigationItem(
        route = "category_management",
        title = "Quản lý nhóm hàng"
    )

    object Login : NavigationItem(
        route = "login",
        title = "Đăng nhập"
    )

    object Register : NavigationItem(
        route = "register",
        title = "Đăng ký"
    )
}

val navigationItems = listOf(
    NavigationItem.Home,
    NavigationItem.Product,
    NavigationItem.Stock,
    NavigationItem.Notification,
    NavigationItem.Profile,
)
