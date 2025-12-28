package com.example.app_quan_ly_do_an.ui.screens.product

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_quan_ly_do_an.data.model.Product
// HIEN'S CODE BEGIN
// 1. THÊM IMPORT NÀY ĐỂ HIỂU ĐƯỢC Category.name
import com.example.app_quan_ly_do_an.data.model.Category
// HIEN'S CODE END
import com.example.app_quan_ly_do_an.ui.components.BarcodeScanner
import com.example.app_quan_ly_do_an.ui.viewmodel.product.AddProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onBack: () -> Unit,
    viewModel: AddProductViewModel = viewModel()
) {
    var productCode by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }

    var category by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }

    var expandedCategory by remember { mutableStateOf(false) }
    val categories by viewModel.categories.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    var showCameraDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) showCameraDialog = true
        else Toast.makeText(context, "Cần quyền Camera", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thêm hàng hóa", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.Close, "Close") }
                },
                actions = {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF0E8A38))
                    } else {
                        TextButton(onClick = {
                            if (productName.isEmpty()) {
                                scope.launch { snackbarHostState.showSnackbar("Vui lòng nhập tên hàng") }
                                return@TextButton
                            }
                            val newProduct = Product(
                                productCode = productCode.ifEmpty { barcode },
                                productName = productName,
                                productCategory = category,
                                unit = unit,
                                sellPrice = price.toDoubleOrNull() ?: 0.0,
                                minStock = minStock.toIntOrNull() ?: 0,
                                productImage = "",
                                totalStock = 0
                            )
                            viewModel.saveProduct(newProduct, {
                                scope.launch { Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show(); onBack() }
                            }, { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } })
                        }) {
                            Text("Lưu", fontWeight = FontWeight.Bold, color = Color(0xFF0E8A38))
                        }
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->

        if (showCameraDialog) {
            Dialog(onDismissRequest = { showCameraDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
                Box(modifier = Modifier.fillMaxSize()) {
                    BarcodeScanner(onBarcodeDetected = { code ->
                        // HIEN'S CODE BEGIN
                        // 2. LOGIC TỰ SINH SKU:
                        barcode = code // Điền mã vạch
                        if (productCode.isEmpty()) {
                            productCode = code // Tự điền SKU bằng mã vạch nếu đang trống
                        }
                        showCameraDialog = false
                        scope.launch { Toast.makeText(context, "Đã quét: $code", Toast.LENGTH_SHORT).show() }
                        // HIEN'S CODE END
                    })
                    IconButton(onClick = { showCameraDialog = false }, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 1.dp) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ProductTextField(
                            label = "Mã vạch / Barcode",
                            value = barcode,
                            onValueChange = { barcode = it },
                            trailingIcon = Icons.Default.QrCodeScanner,
                            onTrailingIconClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                        )

                        ProductTextField(
                            label = "Mã hàng (SKU)",
                            value = productCode,
                            onValueChange = { productCode = it },
                            placeholder = "Tự động lấy theo Barcode nếu trống"
                        )

                        ProductTextField(label = "Tên hàng hóa", value = productName, onValueChange = { productName = it }, required = true)

                        ExposedDropdownMenuBox(
                            expanded = expandedCategory,
                            onExpandedChange = { expandedCategory = !expandedCategory }
                        ) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                label = { Text("Nhóm hàng") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(expanded = expandedCategory, onDismissRequest = { expandedCategory = false }) {
                                categories.forEach { categoryItem ->
                                    DropdownMenuItem(
                                        // HIEN'S CODE BEGIN
                                        // 3. SỬA LỖI FORMAT: Gọi .name trực tiếp vì đã import Class Category
                                        text = { Text(categoryItem.categoryName) },
                                        onClick = {
                                            category = categoryItem.categoryName
                                            expandedCategory = false
                                        }
                                        // HIEN'S CODE END
                                    )
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(1f)) { ProductTextField(label = "Đơn vị", value = unit, onValueChange = { unit = it }) }
                            Box(modifier = Modifier.weight(1f)) { ProductTextField(label = "Giá bán", value = price, onValueChange = { price = it }, keyboardType = KeyboardType.Number) }
                        }
                        ProductTextField(label = "Mức tồn kho tối thiểu", value = minStock, onValueChange = { minStock = it }, keyboardType = KeyboardType.Number)
                    }
                }
            }
        }
    }
}
@Composable
fun ProductTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    required: Boolean = false,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null
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
        placeholder = placeholder?.let { { Text(it, fontSize = 13.sp, color = Color.Gray) } },
        trailingIcon = trailingIcon?.let {
            {
                IconButton(onClick = { onTrailingIconClick?.invoke() }) {
                    Icon(it, contentDescription = null, tint = Color(0xFF006633))
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF006633),
            focusedLabelColor = Color(0xFF006633),
            cursorColor = Color(0xFF006633)
        ),
        singleLine = true
    )
}