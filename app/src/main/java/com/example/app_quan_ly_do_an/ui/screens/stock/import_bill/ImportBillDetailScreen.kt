package com.example.app_quan_ly_do_an.ui.screens.stock.import_bill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Model tạm cho chi tiết sản phẩm trong phiếu
data class BillItemDetail(val name: String, val qty: Int, val price: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportBillDetailScreen(billId: String?, onBack: () -> Unit) {
    // Dữ liệu giả mô phỏng chi tiết phiếu
    val fakeProducts = listOf(
        BillItemDetail("Nước ép Necta đào 1L", 100, 25000.0),
        BillItemDetail("Gạo ST25 Ông Cua", 50, 180000.0),
        BillItemDetail("Dầu ăn Neptune 5L", 20, 320000.0)
    )
    val totalMoney = fakeProducts.sumOf { it.qty * it.price }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết phiếu nhập", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Thanh tổng tiền ở dưới cùng
            Surface(
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tổng tiền thanh toán:", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${"%,.0f".format(totalMoney)} đ",
                        color = Color(0xFF006633),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            // 1. Thông tin chung
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Mã phiếu: ", color = Color.Gray)
                        Text("PN00${billId ?: "X"}", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("NCC: Công ty CP Food")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ngày nhập: 07/12/2025")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Danh sách hàng hóa", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // 2. Danh sách sản phẩm
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(fakeProducts) { item ->
                    ProductRowItem(item)
                }
            }
        }
    }
}

@Composable
fun ProductRowItem(item: BillItemDetail) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Đơn giá: ${"%,.0f".format(item.price)} đ", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "x${item.qty}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${"%,.0f".format(item.price * item.qty)} đ",
                    color = Color(0xFF006633),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
