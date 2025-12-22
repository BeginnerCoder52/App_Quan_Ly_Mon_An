package com.example.app_quan_ly_do_an.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.app_quan_ly_do_an.ui.screens.home.HomeScreen
import com.example.app_quan_ly_do_an.ui.screens.product.ProductScreen
import com.example.app_quan_ly_do_an.ui.screens.stock.StockScreen
import com.example.app_quan_ly_do_an.ui.screens.notification.NotificationScreen
import com.example.app_quan_ly_do_an.ui.screens.profile.ProfileScreen
//HIEN'S CODE BEGIN
import com.example.app_quan_ly_do_an.ui.screens.stock.import_bill.AddImportBillScreen
import com.example.app_quan_ly_do_an.ui.screens.stock.import_bill.ImportBillDetailScreen
//HIEN'S CODE END
import com.example.app_quan_ly_do_an.ui.screens.product.ProductDetailScreen
import com.example.app_quan_ly_do_an.ui.screens.stock.export_bill.ExportBillDetailScreen
import com.example.app_quan_ly_do_an.ui.screens.stock.export_bill.AddExportBillScreen
/**
 * Defines the navigation graph for the application.
 *
 * This composable sets up the navigation host and defines all the possible navigation
 * destinations within the app.
 *
 * @param navController The navigation controller used to navigate between screens.
 */
@Composable
fun AppNavigation(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = NavigationItem.Home.route
    ) {
        composable(NavigationItem.Home.route) {
            HomeScreen()
        }

        composable(NavigationItem.Product.route) {
            ProductScreen(navController = navController)
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
            NotificationScreen()
        }

        composable(NavigationItem.Profile.route) {
            ProfileScreen()
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
                billId = billId,
                onBack = { navController.popBackStack() }
            )
        }
        //HIEN'S CODE END
        composable(NavigationItem.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ProductDetailScreen(
                productId = productId,
                onBack = { navController.popBackStack() }
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
    }
}
