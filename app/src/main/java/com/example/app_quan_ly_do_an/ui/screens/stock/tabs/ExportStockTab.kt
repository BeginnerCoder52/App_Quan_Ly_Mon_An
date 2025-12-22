package com.example.app_quan_ly_do_an.ui.screens.stock.tabs
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


data class ExportBill(
    val id: String,
    val code: String, // Ví dụ: EBI0001
    val date: String, // Ví dụ: 22/11/2025
    val totalAmount: Double
)
val primaryColor = Color(0xFF006633)
val backgroundColor = Color(0xFFF5F5F5)
@Composable
fun ExportStockTab(navController: NavController) {
    // Màu chủ đạo


    // Dữ liệu giả lập
    val sampleBills = listOf(
        ExportBill("1", "EBI0001", "22/11/2025", 1000000.0),
        ExportBill("2", "EBI0002", "22/11/2025", 1000000.0),
        ExportBill("3", "EBI0003", "22/11/2025", 1000000.0),
        ExportBill("4", "EBI0004", "21/11/2025", 500000.0),
        ExportBill("5", "EBI0005", "20/11/2025", 2500000.0),
    )

    Scaffold(
        containerColor = backgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavigationItem.AddExportBill.route) },
                containerColor = primaryColor,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            // --- HEADER SECTION ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                shadowElevation = 2.dp
            ) {
                Column {
                    // 1. Title và Action Icons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Phiếu xuất",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Row {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.SwapVert, contentDescription = "Sort") // Icon sort giống ảnh
                            }
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More")
                            }
                        }
                    }

                    // 2. Filter Button
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp)
                    ) {
                        Button(
                            onClick = { /* Open Filter Dropdown */ },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(
                                Icons.Default.List,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Tất cả phiếu xuất",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }

                    // 3. Tổng Summary
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "Tổng",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "${sampleBills.size}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // --- LIST SECTION ---
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                shadowElevation = 1.dp
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                ) {
                    items(sampleBills) { bill ->
                        ExportBillItem(bill = bill, onClick = {
                            navController.navigate(NavigationItem.ExportBillDetail.createRoute(bill.id))
                        })
                    }
                    // Spacer dưới cùng để không bị che bởi FAB
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun ExportBillItem(bill: ExportBill, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick();
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Cột bên trái: Mã + Ngày
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bill.code,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ngày xuất: ${bill.date}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Cột bên phải: Tổng tiền
            Text(
                text = "Tổng tiền: ${"%,.0f".format(bill.totalAmount)}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = primaryColor
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
    }

}

