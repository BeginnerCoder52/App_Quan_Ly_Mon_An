package com.example.app_quan_ly_do_an.ui.screens.stock.import_bill

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

// --- MOCK DATA  ---
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

// Class State cho từng dòng nhập hàng
class ImportItemState {
    val id: Long = System.currentTimeMillis()
    var selectedProduct by mutableStateOf<ProductOption?>(null)
    var quantity by mutableStateOf("")
    var price by mutableStateOf("")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddImportBillScreen(
    navController: NavController,
    onBack: () -> Unit,
    bottomPadding: Dp = 0.dp // Nhận padding để tránh bị BottomBar che
) {
    val primaryColor = Color(0xFF006633)
    val backgroundColor = Color(0xFFF5F5F5)

    // State thông tin chung
    var billCode by remember { mutableStateOf("") }
    var supplier by remember { mutableStateOf("") } // Thêm trường Nhà cung cấp
    var billDate by remember { mutableStateOf("07/12/2025") }

    // Danh sách hàng hóa
    val importItems = remember { mutableStateListOf<ImportItemState>() }

    // Tính tổng tiền tự động
    val totalAmount by remember {
        derivedStateOf {
            importItems.sumOf { item ->
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
                title = { Text("Nhập hàng", fontWeight = FontWeight.Bold) },
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
        // Thanh Tổng tiền & Lưu phiếu cố định ở đáy
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
                            // TODO: Xử lý lưu data vào DB
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- 1. THÔNG TIN CƠ BẢN ---
            Text("Thông tin cơ bản", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Mã phiếu
                    OutlinedTextField(
                        value = billCode,
                        onValueChange = { billCode = it },
                        label = { Text("Mã phiếu nhập") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Nhà cung cấp
                    OutlinedTextField(
                        value = supplier,
                        onValueChange = { supplier = it },
                        label = { Text("Nhà cung cấp") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Ngày nhập
                    OutlinedTextField(
                        value = billDate,
                        onValueChange = { billDate = it },
                        label = { Text("Ngày nhập") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // --- 2. DANH SÁCH SẢN PHẨM ---
            importItems.forEach { itemState ->
                ImportProductInputItem(
                    itemState = itemState,
                    primaryColor = primaryColor,
                    onDelete = { importItems.remove(itemState) }
                )
            }

            // Nút Thêm sản phẩm
            Button(
                onClick = { importItems.add(ImportItemState()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Thêm sản phẩm", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// Component Item nhập liệu
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportProductInputItem(
    itemState: ImportItemState,
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
            // Header: Dropdown + Xóa
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
                                        // Gợi ý giá mặc định (có thể sửa lại khi nhập hàng)
                                        itemState.price = product.defaultPrice.toInt().toString()
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }

            // Hiển thị ảnh
            if (itemState.selectedProduct != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
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

            // Số lượng
            OutlinedTextField(
                value = itemState.quantity,
                onValueChange = { itemState.quantity = it },
                label = { Text("Số lượng") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Đơn giá nhập
            OutlinedTextField(
                value = itemState.price,
                onValueChange = { itemState.price = it },
                label = { Text("Đơn giá nhập") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}