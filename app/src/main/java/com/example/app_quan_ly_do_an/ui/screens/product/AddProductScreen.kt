package com.example.app_quan_ly_do_an.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_quan_ly_do_an.data.model.Product
import com.example.app_quan_ly_do_an.ui.viewmodel.product.AddProductViewModel
import kotlinx.coroutines.launch
// HIEN'S CODE BEGIN
import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.app_quan_ly_do_an.ui.components.BarcodeScanner
// HIEN'S CODE END

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onBack: () -> Unit,
    viewModel: AddProductViewModel = viewModel()
) {
    // State for all input fields
    var productCode by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var sellPrice by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var productImage by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") } // Biến này lưu mã vạch

    // UI state
    var showImageUrlInput by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }

    //HIEN'S CODE BEGIN
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    //HIEN'S CODE END

    val categories by viewModel.categories.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // HIEN'S CODE BEGIN
    val context = LocalContext.current
    var showCameraDialog by remember { mutableStateOf(false) }

    // Launcher xin quyền Camera
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCameraDialog = true
        } else {
            Toast.makeText(context, "Cần quyền Camera để quét mã", Toast.LENGTH_SHORT).show()
        }
    }
    // HIEN'S CODE END

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->

        // HIEN'S CODE BEGIN
        // --- DIALOG CAMERA (Full Screen) ---
        if (showCameraDialog) {
            Dialog(
                onDismissRequest = { showCameraDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    BarcodeScanner(onBarcodeDetected = { code ->
                        // HIEN'S CODE BEGIN
                        // LOGIC MỚI: Chỉ điền mã vạch, không làm gì khác
                        barcode = code
                        showCameraDialog = false

                        scope.launch {
                            snackbarHostState.showSnackbar("Đã quét mã: $code")
                        }
                        // HIEN'S CODE END
                    })

                    // Nút đóng camera
                    IconButton(
                        onClick = { showCameraDialog = false },
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
            }
        }
        // HIEN'S CODE END

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ---------- HEADER ----------
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }

                    Text(
                        text = "Hàng hóa mới",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF0E8A38),
                            strokeWidth = 2.dp
                        )
                    } else {
                        TextButton(onClick = {
                            if (productName.isEmpty()) {
                                scope.launch { snackbarHostState.showSnackbar("Vui lòng nhập tên hàng") }
                                return@TextButton
                            }
                            if (categoryName.isEmpty()) {
                                scope.launch { snackbarHostState.showSnackbar("Vui lòng chọn nhóm hàng") }
                                return@TextButton
                            }

                            val newProduct = Product(
                                productCode = productCode,
                                productName = productName,
                                productCategory = categoryName,
                                unit = unit,
                                sellPrice = sellPrice.toDoubleOrNull() ?: 0.0,
                                minStock = minStock.toIntOrNull() ?: 0,
                                productImage = productImage
                            )

                            viewModel.saveProduct(
                                product = newProduct,
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("✓ Thêm hàng hóa thành công")
                                        kotlinx.coroutines.delay(500)
                                        onBack()
                                    }
                                },
                                onFailure = { error ->
                                    scope.launch { snackbarHostState.showSnackbar("Lỗi: $error") }
                                }
                            )
                        }) {
                            Text(
                                "Lưu",
                                color = Color(0xFF0E8A38),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // ---------- SCROLL CONTENT ----------
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {

                item {
                    Spacer(Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            // Image Section
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(Color(0xFF0E8A38), CircleShape)
                                        .clickable { showImageUrlInput = !showImageUrlInput },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                                if (showImageUrlInput) {
                                    Spacer(Modifier.width(12.dp))
                                    OutlinedTextField(
                                        value = productImage,
                                        onValueChange = { productImage = it },
                                        modifier = Modifier.weight(1f),
                                        label = { Text("Link ảnh sản phẩm") },
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                }

                                // HIEN'S CODE BEGIN
                                // Nút Quét Barcode LỚN
                                Button(
                                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E8A38)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Quét mã vạch (Hỗ trợ HURA)")
                                }
                                // HIEN'S CODE END

                                ProductTextField(label = "Mã hàng (SKU)", value = productCode, onValueChange = { productCode = it })

                                // HIEN'S CODE BEGIN
                                // Ô Mã vạch có icon quét nhỏ bên phải
                                ProductTextField(
                                    label = "Mã vạch (Barcode)",
                                    value = barcode,
                                    onValueChange = { barcode = it },
                                    trailingIcon = Icons.Default.QrCodeScanner, // Icon trang trí
                                    // Khi bấm icon nhỏ cũng mở cam
                                    onTrailingIconClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                                )
                                // HIEN'S CODE END
                            }

                            ProductTextField(
                                label = "Mã hàng",
                                value = productCode,
                                onValueChange = { productCode = it },
                                trailingIcon = Icons.Default.QrCodeScanner
                            )

                            ProductTextField(
                                label = "Tên hàng",
                                value = productName,
                                onValueChange = { productName = it },
                                required = true
                            )

                            // Category Dropdown
                            ExposedDropdownMenuBox(
                                expanded = expandedCategory,
                                onExpandedChange = { expandedCategory = !expandedCategory }
                            ) {
                                OutlinedTextField(
                                    value = categoryName,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    label = {
                                        Row {
                                            Text("Nhóm hàng")
                                            Text(" *", color = Color.Red)
                                        }
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF0E8A38),
                                        focusedLabelColor = Color(0xFF0E8A38)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedCategory,
                                    onDismissRequest = { expandedCategory = false }
                                ) {
                                    categories.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat.categoryName) },
                                            onClick = {
                                                categoryName = cat.categoryName
                                                expandedCategory = false
                                            }
                                        )
                                    }
                                }
                            }

                            ProductTextField(
                                label = "Đơn vị tính",
                                value = unit,
                                onValueChange = { unit = it }
                            )
                            ProductTextField(
                                label = "Giá bán",
                                value = sellPrice,
                                onValueChange = { sellPrice = it }
                            )
                            ProductTextField(
                                label = "Mức tồn kho tối thiểu",
                                value = minStock,
                                onValueChange = { minStock = it }
                            )
                        }
                    }
                }
            }
        }
    }
}

// HIEN'S CODE BEGIN
// Cập nhật hàm ProductTextField để hỗ trợ click vào icon đuôi (nếu cần)
@Composable
fun ProductTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    required: Boolean = false,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null // Tham số mới
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Row {
                Text(label)
                if (required) Text(" *", color = Color.Red)
            }
        },
        trailingIcon = {
            trailingIcon?.let {
                IconButton(onClick = { onTrailingIconClick?.invoke() }) {
                    Icon(it, contentDescription = null)
                }
            }
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0E8A38),
            focusedLabelColor = Color(0xFF0E8A38),
            cursorColor = Color(0xFF0E8A38)
        )
    )
}
// HIEN'S CODE END