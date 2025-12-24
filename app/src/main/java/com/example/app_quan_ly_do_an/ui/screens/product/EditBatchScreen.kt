package com.example.app_quan_ly_do_an.ui.screens.product

import androidx.compose.foundation.background
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
fun EditBatchScreen(
    batchId: String?, // TODO: sẽ dùng để load data từ database
    onBack: () -> Unit,
    onSave: () -> Unit = {}
) {
    // Load batch data from mock
    val batch = MockProductData.getBatchByCode(batchId ?: "LOT0001")

    // State for editable fields
    var batchCode by remember { mutableStateOf(batch?.batchCode ?: "") }
    var importDate by remember { mutableStateOf(batch?.importDate ?: "") }
    var expiryDate by remember { mutableStateOf(batch?.expiryDate ?: "") }
    var importPrice by remember { mutableStateOf(batch?.importPrice?.toInt()?.toString() ?: "") }
    var initialQuantity by remember { mutableStateOf(batch?.initialQuantity?.toString() ?: "") }
    var currentQuantity by remember { mutableStateOf(batch?.quantity?.toString() ?: "") }
    var storageLocation by remember { mutableStateOf(batch?.storageLocation ?: "") }

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
                        text = "Sửa lô hàng",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    TextButton(onClick = {
                        // Validate required fields
                        if (batchCode.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Vui lòng nhập mã lô hàng",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            return@TextButton
                        }
                        if (importDate.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Vui lòng nhập ngày nhập",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            return@TextButton
                        }

                        // TODO: save to database
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "✓ Cập nhật lô hàng thành công",
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

                            // Product name (read-only)
                            Text(
                                text = batch?.productName ?: "Diet Coke",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            EditBatchTextField(
                                label = "Mã lô hàng",
                                value = batchCode,
                                onValueChange = { batchCode = it },
                                required = true
                            )

                            EditBatchDateField(
                                label = "Ngày nhập",
                                value = importDate,
                                onValueChange = { importDate = it },
                                required = true
                            )

                            EditBatchDateField(
                                label = "Hạn sử dụng",
                                value = expiryDate,
                                onValueChange = { expiryDate = it }
                            )

                            EditBatchTextField(
                                label = "Giá nhập",
                                value = importPrice,
                                onValueChange = { importPrice = it }
                            )

                            EditBatchTextField(
                                label = "Số lượng nhập",
                                value = initialQuantity,
                                onValueChange = { initialQuantity = it }
                            )

                            EditBatchTextField(
                                label = "Số lượng tồn thực tế",
                                value = currentQuantity,
                                onValueChange = { currentQuantity = it }
                            )

                            EditBatchTextField(
                                label = "Vị trí lưu trữ",
                                value = storageLocation,
                                onValueChange = { storageLocation = it }
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
fun EditBatchTextField(
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

// ---------- DATE FIELD ----------
@Composable
fun EditBatchDateField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    required: Boolean = false
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
            Icon(Icons.Default.CalendarToday, contentDescription = null)
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0E8A38),
            focusedLabelColor = Color(0xFF0E8A38),
            cursorColor = Color(0xFF0E8A38)
        ),
        placeholder = { Text("dd/mm/yyyy") }
    )
}

// ---------- PREVIEW ----------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditBatchScreenPreview() {
    App_Quan_Ly_Do_AnTheme {
        EditBatchScreen(
            batchId = "LOT0001",
            onBack = {}
        )
    }
}

