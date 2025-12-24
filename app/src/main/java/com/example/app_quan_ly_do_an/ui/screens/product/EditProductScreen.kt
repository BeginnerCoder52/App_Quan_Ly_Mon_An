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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.app_quan_ly_do_an.data.mock.MockProductData
import com.example.app_quan_ly_do_an.ui.theme.App_Quan_Ly_Do_AnTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: String?, // TODO: sẽ dùng để load data từ database
    onBack: () -> Unit,
    onSave: () -> Unit = {}
) {
    // Mock data - trong thực tế sẽ load từ database theo productId
    var productCode by remember { mutableStateOf("SP000001") }
    var barcode by remember { mutableStateOf("SP000001") }
    var productName by remember { mutableStateOf("Diet Coke") }
    var category by remember { mutableStateOf("Nước giải khát") }
    var unit by remember { mutableStateOf("Lon") }
    var price by remember { mutableStateOf("8000") }
    var minStock by remember { mutableStateOf("10") }

    // State for category dropdown
    var expandedCategory by remember { mutableStateOf(false) }
    val categories = MockProductData.getAllCategories()

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->

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

                    TextButton(onClick = {
                        // Validate required fields
                        if (productName.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Vui lòng nhập tên hàng",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            return@TextButton
                        }
                        if (category.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Vui lòng chọn nhóm hàng",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            return@TextButton
                        }

                        // TODO: save to database
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "✓ Cập nhật hàng hóa thành công",
                                duration = SnackbarDuration.Short
                            )
                            kotlinx.coroutines.delay(500)
                            onSave()
                        }
                    }) {
                        Text(
                            "Lưu",
                            color = Color(0xFF0E8A38),
                            fontWeight = FontWeight.SemiBold
                        )
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

                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color(0xFF0E8A38), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }

                            EditProductTextField(
                                label = "Mã hàng",
                                value = productCode,
                                onValueChange = { productCode = it },
                                trailingIcon = Icons.Default.QrCodeScanner
                            )

                            EditProductTextField(
                                label = "Mã vạch",
                                value = barcode,
                                onValueChange = { barcode = it },
                                trailingIcon = Icons.Default.QrCodeScanner
                            )

                            EditProductTextField(
                                label = "Tên hàng",
                                value = productName,
                                onValueChange = { productName = it },
                                required = true
                            )

                            // Category Dropdown (Real)
                            ExposedDropdownMenuBox(
                                expanded = expandedCategory,
                                onExpandedChange = { expandedCategory = !expandedCategory }
                            ) {
                                OutlinedTextField(
                                    value = category,
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
                                    categories.forEach { categoryItem ->
                                        DropdownMenuItem(
                                            text = { Text(categoryItem) },
                                            onClick = {
                                                category = categoryItem
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
                                value = price,
                                onValueChange = { price = it }
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

// ---------- TEXT FIELD ----------
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

// ---------- DROPDOWN GIẢ (CLICK ĐƯỢC) ----------
@Composable
fun EditProductDropdownField(
    label: String,
    value: String,
    required: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            label = {
                Row {
                    Text(label)
                    if (required) Text(" *", color = Color.Red)
                }
            },
            trailingIcon = {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0E8A38),
                focusedLabelColor = Color(0xFF0E8A38)
            )
        )
    }
}

// ---------- PREVIEW ----------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProductScreenPreview() {
    App_Quan_Ly_Do_AnTheme {
        EditProductScreen(
            productId = "SP000001",
            onBack = {}
        )
    }
}

