package com.example.app_quan_ly_do_an.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.app_quan_ly_do_an.ui.screens.home.HomeScreen
import com.example.app_quan_ly_do_an.ui.screens.product.ProductScreen
import com.example.app_quan_ly_do_an.ui.screens.stock.StockScreen
import com.example.app_quan_ly_do_an.ui.screens.notification.NotificationScreen
import com.example.app_quan_ly_do_an.ui.screens.profile.ProfileScreen
//HIEN'S CODE BEGIN
import com.example.app_quan_ly_do_an.ui.screens.stock.import_bill.AddImportBillScreen
import com.example.app_quan_ly_do_an.ui.screens.stock.import_bill.ImportBillDetailScreen
//HIEN'S CODE END

/**
 * Defines the navigation graph for the application.
 *
 * This composable sets up the navigation host and defines all the possible navigation
 * destinations within the app.
 *
 * @param navController The navigation controller used to navigate between screens.
 */
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationItem.Home.route
    ) {
        composable(NavigationItem.Home.route) {
            HomeScreen()
        }

        composable(NavigationItem.Product.route) {
            ProductScreen()
        }

        composable(NavigationItem.Stock.route) {
            StockScreen(navController = navController)
        }

        composable(NavigationItem.Notification.route) {
            NotificationScreen()
        }

        composable(NavigationItem.Profile.route) {
            ProfileScreen()
        }

        composable(NavigationItem.Stock.route) {
            StockScreen(navController = navController)
        }
        //HIEN'S CODE BEGIN
        composable(NavigationItem.AddImportBill.route) {
            AddImportBillScreen(onBack = { navController.popBackStack() })
        }

        composable(NavigationItem.ImportBillDetail.route) { backStackEntry ->
            val billId = backStackEntry.arguments?.getString("billId")
            ImportBillDetailScreen(
                billId = billId,
                onBack = { navController.popBackStack() }
            )
        }
        //HIEN'S CODE END
    }
}
