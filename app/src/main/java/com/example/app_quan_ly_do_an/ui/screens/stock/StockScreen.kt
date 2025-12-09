//package com.example.app_quan_ly_do_an.ui.screens.stock
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//
//@Composable
//fun StockScreen() {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF5F5F5)),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = "Kho",
//            style = MaterialTheme.typography.headlineMedium,
//            fontWeight = FontWeight.Bold
//        )
//    }
//}


//HIEN'S CODE BEGIN
package com.example.app_quan_ly_do_an.ui.screens.stock

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem
// HIEN'S CODE: Import file Tab của Hiển
import com.example.app_quan_ly_do_an.ui.screens.stock.tabs.ImportStockTab
// TODO FOR AN: Khi nào xong file ExportStockTab thì bỏ comment dòng dưới
// import com.example.app_quan_ly_do_an.ui.screens.stock.tabs.ExportStockTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Nhập kho", "Xuất kho")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Quản lý Kho", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        // --- LOGIC NÚT FAB (DẤU CỘNG) ---
        floatingActionButton = {
            // Nút chỉ hiện khi đang ở Tab Nhập kho (index 0)
            if (selectedTabIndex == 0) {
                FloatingActionButton(
                    onClick = { navController.navigate(NavigationItem.AddImportBill.route) },
                    containerColor = Color(0xFFFFC107), // Màu vàng
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm phiếu nhập")
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Thanh Tab
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color(0xFFFFC107),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color(0xFFFFC107)
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
                        selectedContentColor = Color(0xFFFFC107),
                        unselectedContentColor = Color.Gray
                    )
                }
            }

            // Nội dung của từng Tab
            when (selectedTabIndex) {
                0 -> ImportStockTab(navController)
                1 -> {
                    // Placeholder cho Tab Xuất kho của An
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tab Xuất Kho (Đang chờ phát triển)", color = Color.Gray)
                    }
                }
            }
        }
    }
}
//HIEN'S CODE END