package com.example.app_quan_ly_do_an.ui.screens.stock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem
import com.example.app_quan_ly_do_an.ui.screens.stock.tabs.ExportStockTab
import com.example.app_quan_ly_do_an.ui.screens.stock.tabs.ImportStockTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(navController: NavController, innerPadding: PaddingValues, initialTab: Int = 0) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(initialTab) }
    val tabs = listOf("Nhập kho", "Xuất kho")
    val activeColor = Color(0xFF006633)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF5F5F5),
        // Quản lý FAB tập trung ở đây để tránh lỗi padding lồng nhau
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTabIndex == 0)
                        navController.navigate(NavigationItem.AddImportBill.route)
                    else
                        navController.navigate(NavigationItem.AddExportBill.route)
                },
                containerColor = activeColor,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(), // Chống đè Status Bar
                    bottom = innerPadding.calculateBottomPadding() // Chống đè BottomBar
                )
        ) {
            // 1. TabRow
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = activeColor,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = activeColor
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = activeColor,
                        unselectedContentColor = Color.Gray
                    )
                }
            }

            // 2. Nội dung Tab
            when (selectedTabIndex) {
                0 -> ImportStockTab(navController)
                1 -> ExportStockTab(navController)
            }
        }
    }
}