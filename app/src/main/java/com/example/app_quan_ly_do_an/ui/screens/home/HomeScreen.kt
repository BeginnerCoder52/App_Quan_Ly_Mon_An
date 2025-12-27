package com.example.app_quan_ly_do_an.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem
import com.example.app_quan_ly_do_an.ui.viewmodel.home.DashboardProductData
import com.example.app_quan_ly_do_an.ui.viewmodel.home.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val primaryColor = Color(0xFF0E8A38)
    val backgroundColor = Color(0xFFF5F5F5)
    val warningColor = Color(0xFFE53935)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(primaryColor)
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = "DASHBOARD",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OverviewSection(
                        primaryColor = primaryColor,
                        totalBills = uiState.totalBills,
                        totalRevenue = uiState.totalRevenue,
                        totalProfit = uiState.totalProfit,
                        totalProducts = uiState.totalProducts,
                        totalStockCount = uiState.totalStockCount
                    )
                }

                item {
                    RevenueChartSection(chartData = uiState.revenueChartData)
                }

                if (uiState.bestSellers.isNotEmpty()) {
                    item {
                        SectionContainer(title = "Hàng bán chạy") {
                            uiState.bestSellers.forEachIndexed { index, item ->
                                ProductRowItem(item, navController)
                                if (index < uiState.bestSellers.size - 1) HorizontalDivider(color = Color(0xFFEEEEEE))
                            }
                        }
                    }
                }

                item {
                    SectionContainer(title = "Cảnh báo") {
                        if (uiState.expiringProducts.isNotEmpty()) {
                            Text(
                                "${uiState.expiringProducts.size} Lô hàng sắp hết hạn!",
                                color = warningColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            uiState.expiringProducts.forEach { item ->
                                WarningRowItem(item, type = WarningType.EXPIRING, navController)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        if (uiState.lowStockProducts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "${uiState.lowStockProducts.size} Sản phẩm dưới mức tối thiểu.",
                                color = warningColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            uiState.lowStockProducts.forEach { item ->
                                WarningRowItem(item, type = WarningType.LOW_STOCK, navController)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        
                        if (uiState.expiringProducts.isEmpty() && uiState.lowStockProducts.isEmpty()) {
                            Text("Không có cảnh báo nào", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OverviewSection(
    primaryColor: Color,
    totalBills: Int,
    totalRevenue: Double,
    totalProfit: Double,
    totalProducts: Int,
    totalStockCount: Int
) {
    Column {
        Text("Tổng quan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().height(140.dp), 
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("$totalBills hóa đơn xuất", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("%,.0f đ".format(totalRevenue), color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Lợi nhuận", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("%,.0f đ".format(totalProfit), color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Card(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Tổng tồn kho", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("$totalStockCount đơn vị", color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("$totalProducts hàng hóa", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RevenueChartSection(chartData: List<Float>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Doanh thu tháng này", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            if (chartData.all { it == 0f }) {
                Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    Text("Chưa có dữ liệu doanh thu", color = Color.Gray)
                }
            } else {
                SimpleBarChart(chartData)
            }
        }
    }
}

@Composable
fun SimpleBarChart(data: List<Float>) {
    val maxVal = (data.maxOrNull() ?: 1f).coerceAtLeast(1f)
    
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            data.forEach { value ->
                val heightPercent = value / maxVal
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(heightPercent.coerceIn(0.05f, 1f))
                        .background(Color(0xFF1976D2), RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                )
            }
        }
        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("01", fontSize = 10.sp, color = Color.Gray)
            Text("15", fontSize = 10.sp, color = Color.Gray)
            Text("${data.size}", fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SectionContainer(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ProductRowItem(item: DashboardProductData, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate(NavigationItem.ProductDetail.createRoute(item.id)) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.image,
            contentDescription = null,
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFEEEEEE)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(item.subInfo, color = Color.Gray, fontSize = 12.sp)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(item.topInfo + " đ", fontWeight = FontWeight.Bold, color = Color(0xFF0E8A38), fontSize = 14.sp)
            Text(item.bottomInfo, color = Color.Gray, fontSize = 11.sp)
        }
    }
}

enum class WarningType { EXPIRING, LOW_STOCK }

@Composable
fun WarningRowItem(item: DashboardProductData, type: WarningType, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(NavigationItem.ProductDetail.createRoute(item.id)) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.image,
            contentDescription = null,
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFEEEEEE)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(item.subInfo, color = Color.Black, fontSize = 12.sp)
        }

        if (type == WarningType.LOW_STOCK) {
            OutlinedButton(
                onClick = { navController.navigate(NavigationItem.AddImportBill.route) },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                modifier = Modifier.height(30.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0E8A38))
            ) {
                Text("Nhập", fontSize = 10.sp, color = Color(0xFF0E8A38))
            }
        }
    }
}
