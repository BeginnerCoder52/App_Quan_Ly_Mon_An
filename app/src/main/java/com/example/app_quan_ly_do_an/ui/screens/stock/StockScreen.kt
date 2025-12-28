package com.example.app_quan_ly_do_an.ui.screens.stock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.app_quan_ly_do_an.ui.screens.stock.tabs.ExportStockTab
import com.example.app_quan_ly_do_an.ui.screens.stock.tabs.ImportStockTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(navController: NavController, innerPadding: PaddingValues, initialTab: Int = 0) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(initialTab) }
    val tabs = listOf("Nhập kho", "Xuất kho")
    val activeColor = Color(0xFF006633)

    // Bỏ padding bottom ở Scaffold này và quản lý trực tiếp trong Column
    Scaffold(
        containerColor = Color(0xFFF5F5F5)
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Sử dụng innerPadding.calculateBottomPadding() để tránh bị BottomBar che
                // Không sử dụng scaffoldPadding để tránh bị hở trên (TopPadding)
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // 1. TabRow sát mép trên
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

            // 2. Nội dung thay đổi bên dưới TabRow
            when (selectedTabIndex) {
                0 -> ImportStockTab(navController)
                1 -> ExportStockTab(navController)
            }
        }
    }
}
