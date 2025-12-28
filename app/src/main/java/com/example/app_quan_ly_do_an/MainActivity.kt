package com.example.app_quan_ly_do_an

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app_quan_ly_do_an.ui.components.BottomNavigationBar
import com.example.app_quan_ly_do_an.ui.navigation.AppNavigation
import com.example.app_quan_ly_do_an.ui.theme.App_Quan_Ly_Do_AnTheme
import com.example.app_quan_ly_do_an.ui.viewmodel.home.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            App_Quan_Ly_Do_AnTheme {
                val navController = rememberNavController()
                val homeViewModel: HomeViewModel = viewModel()

                // Theo dõi route hiện tại để ẩn/hiện BottomBar
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Kiểm tra nếu đang ở màn hình Login hoặc Register
                val isAuthScreen = currentRoute == "login" || currentRoute == "register"

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Chỉ hiển thị BottomBar khi KHÔNG ở màn hình Login/Register
                        if (!isAuthScreen) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        innerPadding = innerPadding,
                        homeViewModel = homeViewModel
                    )
                }
            }
        }
    }
}


