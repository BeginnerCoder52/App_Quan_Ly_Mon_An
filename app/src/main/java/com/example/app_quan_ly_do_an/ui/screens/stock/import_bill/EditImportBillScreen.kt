package com.example.app_quan_ly_do_an.ui.screens.stock.import_bill

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

// --- DÙNG LẠI DATA TỪ ADD SCREEN HOẶC KHAI BÁO MỚI ---
data class EditImportProductOption(val id: String, val name: String, val defaultPrice: Double)

val editImportAvailableProducts = listOf(
    EditImportProductOption("1", "Diet Coke", 10000.0),
    EditImportProductOption("2", "Pepsi Zero", 12000.0),
    EditImportProductOption("3", "Fanta Cam", 11000.0),
    EditImportProductOption("4", "7Up", 10500.0),
)

// Class State cho từng dòng sản phẩm khi sửa
class EditImportItemState {
    val id: Long = System.currentTimeMillis()
    var selectedProduct by mutableStateOf<EditImportProductOption?>(null)
    var quantity by mutableStateOf("")
    var price by mutableStateOf("")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditImportBillScreen(
    navController: NavController,
    billId: String?,
    onBack: () -> Unit,
    bottomPadding: Dp = 0.dp
) {
    val primaryColor = Color(0xFF006633)
    val backgroundColor = Color(0xFFF5F5F5)

    // State dữ liệu
    var billCode by remember { mutableStateOf("") }
    var supplier by remember { mutableStateOf("") } // Đặc thù phiếu nhập
    var billDate by remember { mutableStateOf("") }

    val importItems = remember { mutableStateListOf<EditImportItemState>() }
    var isLoading by remember { mutableStateOf(true) }

    // --- GIẢ LẬP LOAD DỮ LIỆU TỪ SERVER/DB ---
    LaunchedEffect(billId) {
        delay(500) // Giả lập mạng lag 0.5s

        // Fill dữ liệu giả
        billCode = "PN00${billId ?: "X"}"
        supplier = "Công ty CP Food"
        billDate = "07/12/2025"

        // Fill danh sách sản phẩm cũ
        importItems.add(EditImportItemState().apply {
            selectedProduct = editImportAvailableProducts.find { it.name == "Diet Coke" }
            quantity = "100"
            price = "9500" // Giá nhập có thể khác giá bán
        })
        importItems.add(EditImportItemState().apply {
            selectedProduct = editImportAvailableProducts.find { it.name == "Fanta Cam" }
            quantity = "200"
            price = "10500"
        })

        isLoading = false
    }

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
                title = { Text("Sửa phiếu nhập", fontWeight = FontWeight.Bold) },
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
                            // TODO: Logic Update Database
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
                        // Mã phiếu
                        OutlinedTextField(
                            value = billCode,
                            onValueChange = { billCode = it },
                            label = { Text("Mã phiếu nhập") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        // Nhà cung cấp (Khác biệt so với Export)
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

                // --- DANH SÁCH SẢN PHẨM ---
                importItems.forEach { itemState ->
                    EditImportProductItemView(
                        itemState = itemState,
                        primaryColor = primaryColor,
                        onDelete = { importItems.remove(itemState) }
                    )
                }

                Button(
                    onClick = { importItems.add(EditImportItemState()) },
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

// Component Item nhập liệu (Logic giống hệt EditExport nhưng dùng data của Import)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditImportProductItemView(
    itemState: EditImportItemState,
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
            // Header: Dropdown & Xóa
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
                            editImportAvailableProducts.forEach { product ->
                                DropdownMenuItem(
                                    text = { Text(product.name) },
                                    onClick = {
                                        itemState.selectedProduct = product
                                        // Tự điền giá nếu chưa có
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

            // Ảnh Preview
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