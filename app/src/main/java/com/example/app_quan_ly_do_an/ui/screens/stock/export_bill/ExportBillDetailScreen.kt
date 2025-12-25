package com.example.app_quan_ly_do_an.ui.screens.stock.export_bill
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem

// import com.example.app_quan_ly_do_an.R

// --- MOCK DATA CHO CHI TIẾT ---
data class ExportBillDetail(
    val id: String,
    val code: String,
    val date: String,
    val totalAmount: Double,
    val products: List<BillProductItem>
)

data class BillProductItem(
    val name: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportBillDetailScreen(
    navController: NavController,
    billId: String? // Nhận ID từ màn hình trước
) {
    val primaryColor = Color(0xFF006633)
    val backgroundColor = Color(0xFFF5F5F5)

    // Dữ liệu giả lập
    val mockDetail = ExportBillDetail(
        id = "1",
        code = "EBI0001",
        date = "22/11/2025",
        totalAmount = 1000000.0,
        products = listOf(
            BillProductItem("Diet Coke", 100, 10000.0),
            BillProductItem("Diet Coke", 100, 10000.0),
            BillProductItem("Pepsi Zero", 50, 12000.0)
        )
    )

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Thông tin phiếu xuất",
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
                                modifier = Modifier.clickable {
                                    navController.navigate(NavigationItem.EditExportBill.createRoute(mockDetail.id))
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Mã phiếu xuất
                        InfoField(label = "Mã phiếu xuất", value = mockDetail.code, showCopyIcon = true, primaryColor = primaryColor)
                        Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                        // Ngày xuất
                        InfoField(label = "Ngày xuất", value = mockDetail.date)
                        Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                        // Tổng tiền
                        InfoField(label = "Tổng tiền", value = "%,.0f".format(mockDetail.totalAmount))
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
                            "Danh sách sản phẩm",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        mockDetail.products.forEachIndexed { index, product ->
                            ProductDetailItem(product)
                            if (index < mockDetail.products.size - 1) {
                                Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENT CON: Dòng thông tin (Label + Value) ---
@Composable
fun InfoField(
    label: String,
    value: String,
    showCopyIcon: Boolean = false,
    primaryColor: Color = Color.Black
) {
    Column {
        Text(text = label, color = Color.Gray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black
            )
            if (showCopyIcon) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = primaryColor,
                    modifier = Modifier.size(16.dp).clickable { /* Handle Copy */ }
                )
            }
        }
    }
}

// --- COMPONENT CON: Item sản phẩm trong danh sách ---
@Composable
fun ProductDetailItem(item: BillProductItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ảnh placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEEEEEE)), // Màu xám thay cho ảnh
            contentAlignment = Alignment.Center
        ) {
            Text("IMG", fontSize = 10.sp, color = Color.Gray)

        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Số lượng: ${item.quantity}",
                color = Color.Gray,
                fontSize = 13.sp
            )
        }

        Text(
            text = "Đơn giá: ${"%,.0f".format(item.price)}",
            color = Color.Gray,
            fontSize = 13.sp
        )
    }
}