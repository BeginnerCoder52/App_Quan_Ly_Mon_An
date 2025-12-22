package com.example.app_quan_ly_do_an.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_quan_ly_do_an.ui.components.DetailRow
import com.example.app_quan_ly_do_an.ui.theme.App_Quan_Ly_Do_AnTheme

@Composable
fun BatchDetailScreen(
    batchId: String?,
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
                shape = RoundedCornerShape(
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
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
                            text = "Thông tin lô hàng",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = null)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ---------------- NỘI DUNG CUỘN ----------------
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 60.dp)
            ) {

                // -------- CARD: THÔNG TIN CƠ BẢN --------
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
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
                                    text = "Thông tin cơ bản",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Sửa",
                                    color = Color(0xFF0E8A38),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Placeholder image
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(
                                        Color(0xFFECECEC),
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("IMG", fontSize = 12.sp, color = Color.Gray)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Diet Coke",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = batchId ?: "LOT0001",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = Color(0xFF0E8A38),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Divider()

                            // --- Ngày nhập | Hạn sử dụng ---
                            TwoColumnRow(
                                leftLabel = "Ngày nhập",
                                leftValue = "22/11/2025",
                                rightLabel = "Hạn sử dụng",
                                rightValue = "22/11/2026"
                            )
                            Divider()

                            // --- Giá nhập | Số lượng nhập ---
                            TwoColumnRow(
                                leftLabel = "Giá nhập",
                                leftValue = "8,000",
                                rightLabel = "Số lượng nhập",
                                rightValue = "100"
                            )
                            Divider()

                            DetailRow("Số lượng tồn thực tế", "10")
                            Divider()
                            DetailRow("Vị trí lưu trữ", "Kho")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

// ---------------- TWO COLUMN ROW ----------------
@Composable
fun TwoColumnRow(
    leftLabel: String,
    leftValue: String,
    rightLabel: String,
    rightValue: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(leftLabel, fontSize = 14.sp, color = Color.Gray)
            Text(leftValue, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(rightLabel, fontSize = 14.sp, color = Color.Gray)
            Text(rightValue, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ---------------- PREVIEW ----------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BatchDetailScreenPreview() {
    App_Quan_Ly_Do_AnTheme {
        BatchDetailScreen(
            batchId = "LOT0001",
            onBack = {}
        )
    }
}