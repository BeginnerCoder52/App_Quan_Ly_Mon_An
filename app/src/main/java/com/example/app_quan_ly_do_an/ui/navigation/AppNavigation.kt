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
            StockScreen()
        }

        composable(NavigationItem.Notification.route) {
            NotificationScreen()
        }

        composable(NavigationItem.Profile.route) {
            ProfileScreen()
        }
    }
}