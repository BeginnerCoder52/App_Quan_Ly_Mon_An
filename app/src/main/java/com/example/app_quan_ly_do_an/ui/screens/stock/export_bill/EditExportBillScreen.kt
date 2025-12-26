package com.example.app_quan_ly_do_an.ui.screens.stock.export_bill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

// --- Tái sử dụng Class State và Data từ màn hình Add ---
// Nếu bạn đã định nghĩa ProductOption và availableProducts ở file AddExportBillScreen
// và để chúng là public thì có thể import dùng lại.
// Tại đây mình khai báo lại để code độc lập chạy được ngay.

data class EditProductOption(val id: String, val name: String, val defaultPrice: Double)

val editAvailableProducts = listOf(
    EditProductOption("1", "Diet Coke", 10000.0),
    EditProductOption("2", "Pepsi Zero", 12000.0),
    EditProductOption("3", "Fanta Cam", 11000.0),
    EditProductOption("4", "7Up", 10500.0),
)

class EditExportItemState {
    val id: Long = System.currentTimeMillis()
    var selectedProduct by mutableStateOf<EditProductOption?>(null)
    var quantity by mutableStateOf("")
    var price by mutableStateOf("")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExportBillScreen(
    navController: NavController,
    billId: String?,
    onBack: () -> Unit,
    bottomPadding: Dp = 0.dp
) {
    val primaryColor = Color(0xFF006633)
    val backgroundColor = Color(0xFFF5F5F5)

    // State dữ liệu
    var billCode by remember { mutableStateOf("") }
    var billDate by remember { mutableStateOf("") }
    val exportItems = remember { mutableStateListOf<EditExportItemState>() }
    var isLoading by remember { mutableStateOf(true) }

    // --- GIẢ LẬP LẤY DỮ LIỆU TỪ ID ---
    LaunchedEffect(billId) {
        // Giả vờ gọi API hoặc DB mất 0.5s
        delay(500)

        // Dữ liệu giả trả về (Giống trong ảnh Thông tin phiếu xuất)
        billCode = "EBI0001"
        billDate = "22/11/2025"

        // Fill danh sách sản phẩm cũ
        exportItems.add(EditExportItemState().apply {
            selectedProduct = editAvailableProducts.find { it.name == "Diet Coke" }
            quantity = "100"
            price = "10000"
        })
        exportItems.add(EditExportItemState().apply {
            selectedProduct = editAvailableProducts.find { it.name == "Pepsi Zero" }
            quantity = "50"
            price = "12000"
        })

        isLoading = false
    }

    // Tính tổng tiền tự động
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
                title = { Text("Chỉnh sửa phiếu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Thanh Bottom Bar chứa nút Lưu
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
                            // TODO: Xử lý Update vào DB
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Lưu thay đổi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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
                    EditProductItemView(
                        itemState = itemState,
                        primaryColor = primaryColor,
                        onDelete = { exportItems.remove(itemState) }
                    )
                }

                Button(
                    onClick = { exportItems.add(EditExportItemState()) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Thêm sản phẩm", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// Component nhập liệu cho Item (Re-use logic from Add screen)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductItemView(
    itemState: EditExportItemState,
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
            // Dropdown chọn SP & Xóa
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                            editAvailableProducts.forEach { product ->
                                DropdownMenuItem(
                                    text = { Text(product.name) },
                                    onClick = {
                                        itemState.selectedProduct = product
                                        if (itemState.price.isEmpty()) {
                                            itemState.price = product.defaultPrice.toInt().toString()
                                        }
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

            // Ảnh preview
            if (itemState.selectedProduct != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).background(Color.LightGray, RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
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