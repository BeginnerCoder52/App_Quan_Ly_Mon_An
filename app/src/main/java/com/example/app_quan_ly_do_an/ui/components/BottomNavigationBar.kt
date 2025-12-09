package com.example.app_quan_ly_do_an.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
// HIEN'S CODE BEGIN
import androidx.navigation.NavGraph.Companion.findStartDestination // Import quan trọng để sửa warning
// HIEN'S CODE END
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem
import com.example.app_quan_ly_do_an.ui.navigation.navigationItems

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        contentColor = Color(0xFFFFC107)
    ) {
        navigationItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    // HIEN'S CODE BEGIN: Sửa lỗi Type Mismatch (Nullable)
                    // Dùng item.icon!! vì các item trong BottomBar (Home, Product...) chắc chắn có icon.
                    // Nếu item.icon có thể null, dùng: item.icon ?: Icons.Default.Home
                    Icon(
                        imageVector = item.icon ?: Icons.Default.Home,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                    // HIEN'S CODE END
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // HIEN'S CODE BEGIN: Sửa Warning quan trọng về Navigation
                            // Thay thế startDestinationId bằng findStartDestination().id để tránh lỗi logic
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // HIEN'S CODE END
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFFC107),
                    selectedTextColor = Color(0xFFFFC107),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFFFFF8E1)
                )
            )
        }
    }
}