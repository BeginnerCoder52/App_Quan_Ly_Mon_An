package com.example.app_quan_ly_do_an.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.app_quan_ly_do_an.ui.screens.home.HomeScreen
import com.example.app_quan_ly_do_an.ui.screens.product.ProductScreen
import com.example.app_quan_ly_do_an.ui.screens.stock.StockScreen
import com.example.app_quan_ly_do_an.ui.screens.notification.NotificationScreen
import com.example.app_quan_ly_do_an.ui.screens.product.BatchDetailScreen
import com.example.app_quan_ly_do_an.ui.screens.profile.ProfileScreen
import com.example.app_quan_ly_do_an.ui.screens.stock.import_bill.AddImportBillScreen
import com.example.app_quan_ly_do_an.ui.screens.stock.import_bill.ImportBillDetailScreen
import com.example.app_quan_ly_do_an.ui.viewmodel.home.HomeViewModel
import com.example.app_quan_ly_do_an.ui.screens.stock.import_bill.EditImportBillScreen
import com.example.app_quan_ly_do_an.ui.screens.product.ProductDetailScreen
import com.example.app_quan_ly_do_an.ui.screens.stock.export_bill.ExportBillDetailScreen
import com.example.app_quan_ly_do_an.ui.screens.stock.export_bill.AddExportBillScreen
import com.example.app_quan_ly_do_an.ui.screens.stock.export_bill.EditExportBillScreen

import com.example.app_quan_ly_do_an.ui.screens.product.BatchListScreen
import com.example.app_quan_ly_do_an.ui.screens.product.AddProductScreen
import com.example.app_quan_ly_do_an.ui.screens.product.EditProductScreen
import com.example.app_quan_ly_do_an.ui.screens.product.EditBatchScreen
import com.example.app_quan_ly_do_an.ui.screens.auth.LoginScreen
import com.example.app_quan_ly_do_an.ui.screens.auth.RegisterScreen
import com.example.app_quan_ly_do_an.ui.screens.product.CategoryManagementScreen

/**
 * Defines the navigation graph for the application.
 *
 * This composable sets up the navigation host and defines all the possible navigation
 * destinations within the app.
 *
 * @param navController The navigation controller used to navigate between screens.
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    innerPadding: PaddingValues,
    // HIEN'S CODE BEGIN
    homeViewModel: HomeViewModel // Thêm tham số này để khớp với MainActivity
    // HIEN'S CODE END
) {
    NavHost(
        navController = navController,
        startDestination = NavigationItem.Login.route
    ) {
        // Login & Register screens - KHÔNG có padding vì không có BottomBar
        composable(NavigationItem.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(NavigationItem.Register.route) {
            RegisterScreen(navController = navController)
        }

        // Các màn hình khác - CÓ padding vì có BottomBar
        composable(NavigationItem.Home.route) {
            Box(modifier = Modifier.padding(innerPadding)) {
                HomeScreen(navController = navController, viewModel = homeViewModel)
            }
        }

        composable(NavigationItem.Product.route) {
            Box(modifier = Modifier.padding(innerPadding)) {
                ProductScreen(navController = navController)
            }
        }

        //Lưu biến để quay về đúng tab trước
        composable(
            route = NavigationItem.Stock.route,
            arguments = listOf(navArgument("tab") { 
                type = NavType.IntType
                defaultValue = 0 
            })
        ) { backStackEntry ->
            val tab = backStackEntry.arguments?.getInt("tab") ?: 0
            StockScreen(navController = navController, innerPadding = innerPadding, initialTab = tab)
        }

        composable(NavigationItem.Notification.route) {
            Box(modifier = Modifier.padding(innerPadding)) {
                NotificationScreen()
            }
        }

        composable(NavigationItem.Profile.route) {
            Box(modifier = Modifier.padding(innerPadding)) {
                ProfileScreen(navController = navController)
            }
        }

        //HIEN'S CODE BEGIN
        //An sửa lại
        composable(NavigationItem.AddImportBill.route) {
            AddImportBillScreen(
                navController = navController,
                onBack = { navController.popBackStack() },
                bottomPadding = innerPadding.calculateBottomPadding()
            )
        }

        composable(NavigationItem.ImportBillDetail.route) { backStackEntry ->
            val billId = backStackEntry.arguments?.getString("billId")
            ImportBillDetailScreen(
                navController = navController,
                billId = billId,
            )
        }
        //HIEN'S CODE END
        composable(NavigationItem.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ProductDetailScreen(
                productId = productId,
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }
        composable(NavigationItem.BatchList.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            BatchListScreen(
                productId = productId,
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavigationItem.BatchDetail.route) { backStackEntry ->
            val batchId = backStackEntry.arguments?.getString("batchId")
            BatchDetailScreen(
                batchId = batchId,
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavigationItem.AddProduct.route) {
            AddProductScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavigationItem.EditProduct.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            EditProductScreen(
                productId = productId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavigationItem.EditBatch.route) { backStackEntry ->
            val batchId = backStackEntry.arguments?.getString("batchId")
            EditBatchScreen(
                batchId = batchId,
                onBack = { navController.popBackStack() },
            )
        }
        composable(NavigationItem.EditImportBill.route) { backStackEntry ->
            val billId = backStackEntry.arguments?.getString("billId")
            EditImportBillScreen(
                navController = navController,
                billId = billId,
                onBack = { navController.popBackStack() },
                bottomPadding = innerPadding.calculateBottomPadding() // Quan trọng để tránh BottomBar
            )
        }

        composable(NavigationItem.ExportBillDetail.route) { backStackEntry ->
            val billId = backStackEntry.arguments?.getString("billId")
            ExportBillDetailScreen(
                navController = navController,
                billId = billId
            )
        }
        composable(NavigationItem.AddExportBill.route) {
            AddExportBillScreen(
                navController = navController,
                onBack = { navController.popBackStack() },
                bottomPadding = innerPadding.calculateBottomPadding()
            )
        }

        composable(NavigationItem.EditExportBill.route) { backStackEntry ->
            val billId = backStackEntry.arguments?.getString("billId")
            EditExportBillScreen(
                navController = navController,
                billId = billId,
                onBack = { navController.popBackStack() },
                bottomPadding = innerPadding.calculateBottomPadding()
            )
        }

        composable(NavigationItem.CategoryManagement.route) {
            CategoryManagementScreen(
                onBack = { navController.popBackStack() },
                bottomPadding = innerPadding.calculateBottomPadding()
            )
        }
    }
}
