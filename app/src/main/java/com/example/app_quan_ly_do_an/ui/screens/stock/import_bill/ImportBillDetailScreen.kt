package com.example.app_quan_ly_do_an.ui.screens.stock.import_bill

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem


data class BillItemDetail(val name: String, val qty: Int, val price: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportBillDetailScreen(
    navController: NavController,
    billId: String?,
) {
    // Màu chủ đạo
    val primaryColor = Color(0xFF006633)
    val backgroundColor = Color(0xFFF5F5F5)

    // Dữ liệu giả mô phỏng
    val fakeProducts = listOf(
        BillItemDetail("Nước ép Necta đào 1L", 100, 25000.0),
        BillItemDetail("Gạo ST25 Ông Cua", 50, 180000.0),
        BillItemDetail("Dầu ăn Neptune 5L", 20, 320000.0)
    )
    val totalMoney = fakeProducts.sumOf { it.qty * it.price }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Thông tin phiếu nhập",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- PHẦN 1: THÔNG TIN CƠ BẢN ---
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header của Card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Thông tin cơ bản",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                "Sửa",
                                color = primaryColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { navController.navigate(NavigationItem.EditImportBill.createRoute(billId ?: "0")) }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Mã phiếu
                        InfoField(
                            label = "Mã phiếu nhập",
                            value = "PN00${billId ?: "X"}",
                            showCopyIcon = true,
                            primaryColor = primaryColor
                        )
                        Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                        // Nhà cung cấp
                        InfoFieldWithIcon(
                            label = "Nhà cung cấp",
                            value = "Công ty CP Food",
                            icon = Icons.Default.Person,
                            primaryColor = primaryColor
                        )
                        Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                        // Ngày nhập
                        InfoFieldWithIcon(
                            label = "Ngày nhập",
                            value = "07/12/2025",
                            icon = Icons.Default.CalendarToday,
                            primaryColor = primaryColor
                        )
                        Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                        // Tổng tiền
                        InfoField(
                            label = "Tổng tiền thanh toán",
                            value = "${"%,.0f".format(totalMoney)} đ",
                            isTotal = true,
                            primaryColor = primaryColor
                        )
                    }
                }
            }

            // --- PHẦN 2: DANH SÁCH SẢN PHẨM ---
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Danh sách hàng hóa",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        fakeProducts.forEachIndexed { index, product ->
                            ImportProductDetailItem(product)
                            if (index < fakeProducts.size - 1) {
                                Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Component hiển thị thông tin  ---
@Composable
fun InfoField(
    label: String,
    value: String,
    showCopyIcon: Boolean = false,
    isTotal: Boolean = false,
    primaryColor: Color
) {
    Column {
        Text(text = label, color = Color.Gray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontWeight = FontWeight.SemiBold,
                fontSize = if (isTotal) 18.sp else 16.sp,
                color = if (isTotal) primaryColor else Color.Black
            )
            if (showCopyIcon) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = primaryColor,
                    modifier = Modifier.size(16.dp).clickable { }
                )
            }
        }
    }
}

// --- Component hiển thị thông tin kèm Icon  ---
@Composable
fun InfoFieldWithIcon(
    label: String,
    value: String,
    icon: ImageVector,
    primaryColor: Color
) {
    Column {
        Text(text = label, color = Color.Gray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = primaryColor, // Dùng màu xanh chủ đạo cho icon luôn cho đẹp
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

// --- Component Item sản phẩm ---
@Composable
fun ImportProductDetailItem(item: BillItemDetail) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ảnh placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEEEEEE)),
            contentAlignment = Alignment.Center
        ) {
            Text("IMG", fontSize = 10.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Số lượng: ${item.qty}",
                color = Color.Gray,
                fontSize = 13.sp
            )
        }

        Text(
            text = "${"%,.0f".format(item.price)} đ",
            color = Color.Gray,
            fontSize = 13.sp
        )
    }
}

