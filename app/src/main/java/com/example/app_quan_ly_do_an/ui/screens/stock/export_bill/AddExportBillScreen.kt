package com.example.app_quan_ly_do_an.ui.screens.stock.export_bill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// --- MOCK DATA ---
data class ProductOption(
    val id: String,
    val name: String,
    val defaultPrice: Double
)

val availableProducts = listOf(
    ProductOption("1", "Diet Coke", 10000.0),
    ProductOption("2", "Pepsi Zero", 12000.0),
    ProductOption("3", "Fanta Cam", 11000.0),
    ProductOption("4", "7Up", 10500.0),
)

// DÙNG CLASS VỚI MUTABLE STATE ĐỂ NHẬP  LIỆU ---
class ExportItemState {
    val id: Long = System.currentTimeMillis()
    var selectedProduct by mutableStateOf<ProductOption?>(null)
    var quantity by mutableStateOf("") // Tự động cập nhật UI khi gõ
    var price by mutableStateOf("")    // Tự động cập nhật UI khi gõ
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExportBillScreen(
    navController: NavController,
    onBack: () -> Unit,
    bottomPadding: Dp = 0.dp
) {
    val primaryColor = Color(0xFF006633)
    val backgroundColor = Color(0xFFF5F5F5)

    // State chung
    var billCode by remember { mutableStateOf("") }
    var billDate by remember { mutableStateOf("22/11/2025") }

    // List sản phẩm
    val exportItems = remember { mutableStateListOf<ExportItemState>() }

    // --- TÍNH TỔNG TIỀN TỰ ĐỘNG (Dùng derivedStateOf để tối ưu) ---
    val totalAmount by remember {
        derivedStateOf {
            exportItems.sumOf { item ->
                val q = item.quantity.toLongOrNull() ?: 0
                val p = item.price.toDoubleOrNull() ?: 0.0
                q * p
            }
        }
    }

    Scaffold(
        modifier = Modifier.padding(bottom = bottomPadding),
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Xuất hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(onClick = onBack) {
                        Text("Lưu", color = primaryColor, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        // THANH TỔNG TIỀN + LƯU
        bottomBar = {
            Surface(
                shadowElevation = 10.dp,
                color = Color.White,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tổng tiền", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "%,.0f".format(totalAmount),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            // TODO: Xử lý lưu data vào DB tại đây
                            onBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Lưu phiếu", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Cho phép cuộn nội dung
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- THÔNG TIN CƠ BẢN ---
            Text("Thông tin cơ bản", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = billCode,
                        onValueChange = { billCode = it },
                        label = { Text("Mã phiếu xuất") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = billDate,
                        onValueChange = { billDate = it },
                        label = { Text("Ngày xuất") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // --- DANH SÁCH SẢN PHẨM ---
            exportItems.forEach { itemState ->
                ExportProductInputItem(
                    itemState = itemState,
                    primaryColor = primaryColor,
                    onDelete = { exportItems.remove(itemState) }
                )
            }

            // Nút Thêm sản phẩm
            Button(
                onClick = { exportItems.add(ExportItemState()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Thêm sản phẩm", fontWeight = FontWeight.Bold)
            }

            // Spacer để nội dung cuối cùng không bị che bởi BottomBar khi cuộn hết cỡ
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportProductInputItem(
    itemState: ExportItemState,
    primaryColor: Color,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Dropdown chọn SP và nút Xóa
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = itemState.selectedProduct?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Chọn sản phẩm") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            availableProducts.forEach { product ->
                                DropdownMenuItem(
                                    text = { Text(product.name) },
                                    onClick = {
                                        itemState.selectedProduct = product
                                        // Tự điền giá mặc định, convert sang chuỗi Integer cho đẹp
                                        itemState.price = product.defaultPrice.toInt().toString()
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Icon xóa
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }

            // Hiển thị ảnh nếu đã chọn sản phẩm
            if (itemState.selectedProduct != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Thay bằng AsyncImage nếu có URL
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.LightGray, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("IMG", fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(itemState.selectedProduct!!.name, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Input Số lượng
            OutlinedTextField(
                value = itemState.quantity,
                onValueChange = { itemState.quantity = it },
                label = { Text("Số lượng") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Input Đơn giá
            OutlinedTextField(
                value = itemState.price,
                onValueChange = { itemState.price = it },
                label = { Text("Đơn giá") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}