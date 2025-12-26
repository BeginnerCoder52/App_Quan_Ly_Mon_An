package com.example.app_quan_ly_do_an.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.app_quan_ly_do_an.data.model.InventoryLot
import com.example.app_quan_ly_do_an.ui.viewmodel.product.EditBatchViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBatchScreen(
    batchId: String?,
    onBack: () -> Unit,
    viewModel: EditBatchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // State for editable fields
    var batchCode by remember { mutableStateOf("") }
    var importDate by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var importPrice by remember { mutableStateOf("") }
    var initialQuantity by remember { mutableStateOf("") }
    var currentQuantity by remember { mutableStateOf("") }
    var storageLocation by remember { mutableStateOf("") }

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Load data when batchId changes
    LaunchedEffect(batchId) {
        if (batchId != null) {
            viewModel.loadBatchData(batchId)
        }
    }

    // Sync state with loaded data
    LaunchedEffect(uiState.lot) {
        uiState.lot?.let { lot ->
            batchCode = lot.lotCode
            importDate = lot.importDate?.let { sdf.format(it) } ?: ""
            expiryDate = lot.expiryDate?.let { sdf.format(it) } ?: ""
            importPrice = lot.importPrice.toInt().toString()
            initialQuantity = lot.initialQuantity.toString()
            currentQuantity = lot.currentQuantity.toString()
            storageLocation = lot.location
        }
    }

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
                            text = "Sửa lô hàng",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (uiState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            TextButton(onClick = {
                                if (batchCode.isEmpty()) {
                                    scope.launch { snackbarHostState.showSnackbar("Vui lòng nhập mã lô hàng") }
                                    return@TextButton
                                }
                                
                                val parsedImportDate = try { sdf.parse(importDate) } catch (e: Exception) { null }
                                val parsedExpiryDate = try { sdf.parse(expiryDate) } catch (e: Exception) { null }

                                if (parsedImportDate == null) {
                                    scope.launch { snackbarHostState.showSnackbar("Ngày nhập sai định dạng") }
                                    return@TextButton
                                }

                                val updatedLot = uiState.lot?.copy(
                                    lotCode = batchCode,
                                    importDate = parsedImportDate,
                                    expiryDate = parsedExpiryDate,
                                    importPrice = importPrice.toDoubleOrNull() ?: 0.0,
                                    initialQuantity = initialQuantity.toIntOrNull() ?: 0,
                                    currentQuantity = currentQuantity.toIntOrNull() ?: 0,
                                    location = storageLocation
                                ) ?: return@TextButton

                                viewModel.updateBatch(
                                    lot = updatedLot,
                                    onSuccess = {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("✓ Cập nhật lô hàng thành công")
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
                                // Product name (read-only)
                                Text(
                                    text = uiState.product?.productName ?: "...",
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
}

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
