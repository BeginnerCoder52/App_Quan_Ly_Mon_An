package com.example.app_quan_ly_do_an.ui.screens.stock.export_bill

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app_quan_ly_do_an.data.model.Product
import com.example.app_quan_ly_do_an.ui.viewmodel.export_bill.AddExportBillViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExportBillScreen(
    navController: NavController,
    onBack: () -> Unit,
    bottomPadding: Dp = 0.dp,
    viewModel: AddExportBillViewModel = viewModel() // Inject ViewModel
) {
    val primaryColor = Color(0xFFD32F2F)
    val backgroundColor = Color(0xFFF5F5F5)
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Collect State từ ViewModel
    val products by viewModel.products.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveResult by viewModel.saveResult.collectAsState()

    // Xử lý kết quả Lưu
    LaunchedEffect(saveResult) {
        when (val result = saveResult) {
            is AddExportBillViewModel.SaveResult.Success -> {
                Toast.makeText(context, "Xuất kho thành công!", Toast.LENGTH_SHORT).show()
                onBack()
            }
            is AddExportBillViewModel.SaveResult.Error -> {
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                viewModel.resetState() // Reset để tránh Toast hiện lại khi xoay màn hình
            }
            else -> {}
        }
    }

    val totalAmount by remember {
        derivedStateOf {
            viewModel.exportItems.sumOf { item ->
                val q = item.quantity.value.toDoubleOrNull() ?: 0.0
                val p = item.price.value.toDoubleOrNull() ?: 0.0
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
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = primaryColor)
                    } else {
                        TextButton(onClick = { viewModel.saveExportBill() }) {
                            Text("Lưu", color = primaryColor, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 10.dp, color = Color.White, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Tổng tiền", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("%,.0f đ".format(totalAmount), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.saveExportBill() },
                        enabled = !isSaving && viewModel.exportItems.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Lưu phiếu xuất", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Thông tin phiếu xuất", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = viewModel.billCode.value,
                        onValueChange = { viewModel.billCode.value = it },
                        label = { Text("Mã phiếu xuất") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // DATE PICKER
                    Box(modifier = Modifier.fillMaxWidth().clickable {
                        val calendar = Calendar.getInstance()
                        calendar.time = viewModel.billDate.value
                        DatePickerDialog(context, { _, y, m, d ->
                            val cal = Calendar.getInstance()
                            cal.set(y, m, d)
                            viewModel.billDate.value = cal.time
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                    }) {
                        OutlinedTextField(
                            value = sdf.format(viewModel.billDate.value),
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Ngày xuất") },
                            trailingIcon = { Icon(Icons.Default.CalendarToday, null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledBorderColor = Color.Gray,
                                disabledLabelColor = Color.Gray,
                                disabledTrailingIconColor = Color.Gray
                            )
                        )
                    }
                }
            }

            Text("Danh sách sản phẩm xuất", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            viewModel.exportItems.forEach { itemState ->
                ExportProductInputItem(
                    itemState = itemState,
                    availableProducts = products, // Truyền list sản phẩm thật vào
                    onDelete = { viewModel.removeExportItem(itemState) }
                )
            }

            Button(
                onClick = { viewModel.addExportItem() },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportProductInputItem(
    itemState: AddExportBillViewModel.ExportItemState, // Dùng State của ViewModel
    availableProducts: List<Product>,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = itemState.selectedProduct.value?.productName ?: "",
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Chọn sản phẩm") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            availableProducts.forEach { product ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(product.productName, fontWeight = FontWeight.Bold)
                                            Text("Tồn kho: ${product.totalStock}", fontSize = 12.sp, color = Color.Gray)
                                        }
                                    },
                                    onClick = {
                                        itemState.selectedProduct.value = product
                                        itemState.price.value = product.sellPrice.toInt().toString()
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hiển thị Tồn kho để người dùng biết
            if (itemState.selectedProduct.value != null) {
                Text(
                    text = "Tồn kho hiện tại: ${itemState.selectedProduct.value!!.totalStock}",
                    color = Color(0xFF006633),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = itemState.quantity.value,
                    onValueChange = { itemState.quantity.value = it },
                    label = { Text("SL Xuất") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = itemState.price.value,
                    onValueChange = { itemState.price.value = it },
                    label = { Text("Đơn giá bán") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}