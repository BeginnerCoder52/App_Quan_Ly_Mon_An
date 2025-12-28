package com.example.app_quan_ly_do_an.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app_quan_ly_do_an.ui.components.DetailRow
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem
import com.example.app_quan_ly_do_an.ui.viewmodel.product.BatchDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun BatchDetailScreen(
    batchId: String?,
    navController: NavController,
    viewModel: BatchDetailViewModel = viewModel(),
    onBack: () -> Unit = { navController.popBackStack() }
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(batchId) {
        if (batchId != null) {
            viewModel.loadLotDetail(batchId)
        }
    }

    fun formatDate(date: Date?): String {
        return if (date == null) "..." else
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF0E8A38))
            }
        } else {
            val lot = uiState.lot
            val product = uiState.product

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(paddingValues)
            ) {
                // ---------------- HEADER ----------------
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
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.clickable {
                                            navController.navigate(
                                                NavigationItem.EditBatch.createRoute(batchId ?: "")
                                            )
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Ảnh sản phẩm dùng Coil
                                AsyncImage(
                                    model = uiState.product?.productImage?.ifEmpty { null },
                                    contentDescription = uiState.product?.productName,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFEEEEEE)),
                                    contentScale = ContentScale.Crop,
                                    error = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery),
                                    placeholder = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = product?.productName ?: "...",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = lot?.lotCode ?: "...",
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

                                TwoColumnRow(
                                    leftLabel = "Ngày nhập",
                                    leftValue = formatDate(lot?.importDate),
                                    rightLabel = "Hạn sử dụng",
                                    rightValue = formatDate(lot?.expiryDate)
                                )
                                Divider()

                                TwoColumnRow(
                                    leftLabel = "Giá nhập",
                                    leftValue = "${"%,.0f".format(lot?.importPrice ?: 0.0)} đ",
                                    rightLabel = "Số lượng nhập",
                                    rightValue = lot?.initialQuantity?.toString() ?: "0"
                                )
                                Divider()

                                DetailRow("Số lượng tồn thực tế", lot?.currentQuantity?.toString() ?: "0")
                                Divider()
                                DetailRow("Vị trí lưu trữ", lot?.location ?: "Chưa rõ")
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

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
