@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.app_quan_ly_do_an.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem
@Composable
fun ProductDetailScreen(
    productId: String?,
    navController: NavController,
    onBack: () -> Unit = {}

) {
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {

            // ---------------- HEADER (KHÔNG CUỘN) ----------------
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                        Text(
                            "Chi tiết hàng hóa",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ---------------- NỘI DUNG CUỘN ----------------
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 60.dp)
            ) {

                // CARD thông tin cơ bản
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            ,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 1.dp,
                        color = Color.White
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Thông tin cơ bản",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Sửa",
                                    color = Color(0xFF0E8A38),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(Modifier.height(14.dp))

                            // Placeholder image
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(Color(0xFFECECEC), RoundedCornerShape(8.dp))
                            )

                            Spacer(Modifier.height(12.dp))

                            Text("Diet Coke", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)

                            Spacer(Modifier.height(16.dp))

                            DetailRow("Mã hàng", productId ?: "SP000001")
                            Divider()
                            DetailRow("Mã vạch", "SP000001")
                            Divider()
                            DetailRow("Giá bán", "8,000")
                            Divider()
                            DetailRow("Đơn vị tính", "Lon")
                            Divider()
                            DetailRow("Nhóm hàng", "Thực phẩm ăn liền")
                            Divider()
                            DetailRow("Tồn kho", "100")
                            Divider()
                            DetailRow("Mức tồn kho tối thiểu", "10")
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                }

                // Nút xem danh sách lô hàng (CUỘN ĐƯỢC)
                item {
                    Button(
                        onClick = {
                            navController.navigate(
                                NavigationItem.BatchList.createRoute(productId ?: "")
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0E8A38)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Xem danh sách lô hàng", fontSize = 17.sp)
                    }
                }

                item { Spacer(Modifier.height(40.dp)) }
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductDetailScreenPreview() {
    ProductDetailScreen(
        productId = "SP000001",
        navController = androidx.navigation.compose.rememberNavController(),
        onBack = {}
    )
}


@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}
