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
import com.example.app_quan_ly_do_an.ui.viewmodel.product.EditProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: String?,
    onBack: () -> Unit,
    viewModel: EditProductViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // State for input fields
    var productCode by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var sellPrice by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var productImage by remember { mutableStateOf("") }

    // UI state
    var showImageUrlInput by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Load data when productId changes
    LaunchedEffect(productId) {
        if (productId != null) {
            viewModel.loadProductData(productId)
        }
    }

    // Sync input fields with loaded product data
    LaunchedEffect(uiState.product) {
        uiState.product?.let { product ->
            productCode = product.productCode
            productName = product.productName
            categoryName = product.productCategory
            unit = product.unit
            sellPrice = product.sellPrice.toInt().toString()
            minStock = product.minStock.toString()
            productImage = product.productImage
            if (product.productImage.isNotEmpty()) {
                showImageUrlInput = true
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF0E8A38))
            }
        } else {
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
                            text = "Sửa hàng hóa",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (uiState.isSaving) {
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

                                val updatedProduct = uiState.product?.copy(
                                    productCode = productCode,
                                    productName = productName,
                                    productCategory = categoryName,
                                    unit = unit,
                                    sellPrice = sellPrice.toDoubleOrNull() ?: 0.0,
                                    minStock = minStock.toIntOrNull() ?: 0,
                                    productImage = productImage
                                ) ?: return@TextButton

                                viewModel.updateProduct(
                                    product = updatedProduct,
                                    onSuccess = {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("✓ Cập nhật hàng hóa thành công")
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
                                }

                                EditProductTextField(
                                    label = "Mã hàng",
                                    value = productCode,
                                    onValueChange = { productCode = it },
                                    trailingIcon = Icons.Default.QrCodeScanner
                                )

                                EditProductTextField(
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
                                        uiState.categories.forEach { cat ->
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

                                EditProductTextField(
                                    label = "Đơn vị tính",
                                    value = unit,
                                    onValueChange = { unit = it }
                                )
                                EditProductTextField(
                                    label = "Giá bán",
                                    value = sellPrice,
                                    onValueChange = { sellPrice = it }
                                )
                                EditProductTextField(
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
}

@Composable
fun EditProductTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    required: Boolean = false,
    trailingIcon: ImageVector? = null
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
                Icon(it, contentDescription = null)
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
