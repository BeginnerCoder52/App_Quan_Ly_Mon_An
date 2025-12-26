package com.example.app_quan_ly_do_an.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem

// --- MOCK DATA ---
data class DashboardProduct(
    val id: String,
    val name: String,
    val subInfo: String, // Ví dụ: "272 hàng hóa" hoặc "LOT001"
    val rightTopInfo: String, // Ví dụ: Doanh thu
    val rightBottomInfo: String, // Ví dụ: Tồn kho
    val isWarning: Boolean = false
)

@Composable
fun HomeScreen(navController: NavController) {
    val primaryColor = Color(0xFF0E8A38) // Màu xanh chủ đạo từ ProductScreen
    val backgroundColor = Color(0xFFF5F5F5)
    val warningColor = Color(0xFFE53935) // Màu đỏ cảnh báo

    // Dữ liệu giả lập
    val bestSellers = List(4) {
        DashboardProduct("SP00$it", "Nước ép Necta đào 1L", "272 hàng hóa", "10,336,000", "Tồn kho: 300")
    }
    val expiringProducts = List(3) {
        DashboardProduct("SP00$it", "Nước ép Necta đào 1L", "LOT001 - Hết hạn: 30/12/2025", "", "", true)
    }
    val lowStockProducts = List(3) {
        DashboardProduct("SP00$it", "Nước ép Necta đào 1L", "Tồn kho: 1", "", "", true)
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            // Header giả lập Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(primaryColor)
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = "LOGO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Tránh bị che bởi BottomNav
        ) {
            // 1. SECTION TỔNG QUAN
            item {
                Spacer(modifier = Modifier.height(8.dp))
                OverviewSection(primaryColor)
            }

            // 2. SECTION DOANH THU (BIỂU ĐỒ)
            item {
                RevenueChartSection()
            }

            // 3. SECTION HÀNG BÁN CHẠY
            item {
                SectionContainer(title = "Hàng bán chạy") {
                    bestSellers.forEachIndexed { index, item ->
                        ProductRowItem(item, navController)
                        if (index < bestSellers.size - 1) Divider(color = Color(0xFFEEEEEE))
                    }
                }
            }

            // 4. SECTION CẢNH BÁO
            item {
                SectionContainer(title = "Cảnh báo") {
                    // 4.1 Lô hàng sắp hết hạn
                    Text(
                        "3 Lô hàng sắp hết hạn!",
                        color = warningColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    expiringProducts.forEach { item ->
                        WarningRowItem(item, type = WarningType.EXPIRING, navController)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4.2 Tồn kho thấp
                    Text(
                        "3 Sản phẩm dưới mức tồn kho tối thiểu.",
                        color = warningColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    lowStockProducts.forEach { item ->
                        WarningRowItem(item, type = WarningType.LOW_STOCK, navController)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// --- COMPONENTS CON ---

@Composable
fun OverviewSection(primaryColor: Color) {
    Column {
        Text("Tổng quan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Filter Button
        Surface(
            color = Color(0xFFC8E6C9), // Xanh nhạt
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tháng này", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF2E7D32))
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Card Doanh thu
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("20 hóa đơn", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("11.98 triệu", color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Lợi nhuận", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("2.28 triệu", color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Card Tồn kho
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Tổng tồn", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("184", color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Text("25 hàng hóa", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RevenueChartSection() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Doanh thu", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row {
                    Icon(Icons.Default.BarChart, contentDescription = null, tint = Color.Blue)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Default.OpenInFull, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Vẽ biểu đồ cột đơn giản
            SimpleBarChart()
        }
    }
}

@Composable
fun SimpleBarChart() {
    val barValues = listOf(0.3f, 0.9f, 0.8f, 0.85f, 0.15f, 0.35f, 0.15f, 0.38f, 0.8f, 0.5f, 0.8f, 0.79f, 0.92f, 0.8f, 0.75f, 0.6f, 0.75f, 0.6f, 0.4f, 0.25f)
    val labels = listOf("01", "04", "07", "10", "13", "16", "19")

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            barValues.forEach { value ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(value) // Chiều cao dựa trên giá trị
                        .padding(horizontal = 2.dp)
                        .background(Color(0xFF1976D2), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                )
            }
        }
        Divider(color = Color.LightGray, thickness = 1.dp)

        // Labels trục X (giả lập vị trí tương đối)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach {
                Text(text = it, fontSize = 10.sp, color = if(it == "01" || it == "16") Color.Red else Color.Gray, fontWeight = FontWeight.Bold)
            }
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
fun ProductRowItem(item: DashboardProduct, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // Điều hướng sang chi tiết sản phẩm
                navController.navigate(NavigationItem.ProductDetail.createRoute(item.id))
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ảnh placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFEEEEEE), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder ảnh lon nước
            Text("IMG", fontSize = 10.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Thông tin giữa
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(item.subInfo, color = Color.Gray, fontSize = 12.sp)
        }

        // Thông tin phải
        Column(horizontalAlignment = Alignment.End) {
            Text(item.rightTopInfo, fontWeight = FontWeight.Bold, color = Color(0xFF0E8A38), fontSize = 14.sp)
            Text(item.rightBottomInfo, color = Color.Gray, fontSize = 11.sp)
        }
    }
}

enum class WarningType { EXPIRING, LOW_STOCK }

@Composable
fun WarningRowItem(item: DashboardProduct, type: WarningType, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(NavigationItem.ProductDetail.createRoute(item.id)) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFEEEEEE), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("IMG", fontSize = 10.sp, color = Color.Gray)
        }

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
                Text("Nhập hàng", fontSize = 10.sp, color = Color(0xFF0E8A38))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}