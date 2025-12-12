package com.example.app_quan_ly_do_an

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.app_quan_ly_do_an.ui.components.BottomNavigationBar
import com.example.app_quan_ly_do_an.ui.navigation.AppNavigation
import com.example.app_quan_ly_do_an.ui.theme.App_Quan_Ly_Do_AnTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App_Quan_Ly_Do_AnTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    AppNavigation(navController = navController, innerPadding = innerPadding)

                }
            }
        }
    }
}