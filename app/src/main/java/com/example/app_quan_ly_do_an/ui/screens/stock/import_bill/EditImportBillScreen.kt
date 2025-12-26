package com.example.app_quan_ly_do_an.ui.screens.stock.import_bill

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
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
import coil.compose.AsyncImage
import com.example.app_quan_ly_do_an.data.model.Product
import com.example.app_quan_ly_do_an.ui.viewmodel.import_bill.EditImportBillViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditImportBillScreen(
    navController: NavController,
    billId: String?,
    onBack: () -> Unit,
    bottomPadding: Dp = 0.dp,
    viewModel: EditImportBillViewModel = viewModel()
) {
    val primaryColor = Color(0xFF006633)
    val backgroundColor = Color(0xFFF5F5F5)
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    // Load data khi vào màn hình
    LaunchedEffect(billId) {
        billId?.let { viewModel.loadBillData(it) }
    }

    // Xử lý kết quả lưu
    LaunchedEffect(saveSuccess) {
        saveSuccess?.let {
            if (it) {
                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                onBack()
            } else {
                Toast.makeText(context, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show()
            }
            viewModel.resetSaveStatus()
        }
    }

    val totalAmount by remember {
        derivedStateOf {
            viewModel.importItems.sumOf { item ->
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
            Surface(shadowElevation = 10.dp, color = Color.White, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Tổng tiền", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("%,.0f đ".format(totalAmount), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.updateImportBill() },
                        enabled = !isSaving && !isLoading,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isSaving) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        else Text("Lưu thay đổi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = primaryColor) }
        } else {
            Column(
                modifier = Modifier.padding(innerPadding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Thông tin cơ bản", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = viewModel.billCode.value,
                            onValueChange = { viewModel.billCode.value = it },
                            label = { Text("Mã phiếu nhập") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = viewModel.supplier.value,
                            onValueChange = { viewModel.supplier.value = it },
                            label = { Text("Nhà cung cấp") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        // Ngày nhập
                        Box(Modifier.fillMaxWidth().clickable {
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
                                label = { Text("Ngày nhập") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = { Icon(Icons.Default.CalendarToday, null) },
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(disabledTextColor = Color.Black, disabledBorderColor = Color.Gray, disabledLabelColor = Color.Gray, disabledTrailingIconColor = Color.Gray)
                            )
                        }
                    }
                }

                Text("Danh sách sản phẩm", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                viewModel.importItems.forEach { itemState ->
                    EditImportProductInputItem(
                        itemState = itemState,
                        availableProducts = products,
                        primaryColor = primaryColor,
                        onDelete = { viewModel.removeImportItem(itemState) }
                    )
                }

                Button(
                    onClick = { viewModel.addImportItem() },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditImportProductInputItem(
    itemState: EditImportBillViewModel.ImportItemState,
    availableProducts: List<Product>,
    primaryColor: Color,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                    text = { Text(product.productName) },
                                    onClick = {
                                        itemState.selectedProduct.value = product
                                        if (itemState.price.value.isEmpty()) itemState.price.value = product.sellPrice.toInt().toString()
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
            }

            if (itemState.selectedProduct.value != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = itemState.selectedProduct.value?.productImage,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).background(Color.LightGray, RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(itemState.selectedProduct.value!!.productName, fontWeight = FontWeight.Bold)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = itemState.quantity.value,
                    onValueChange = { itemState.quantity.value = it },
                    label = { Text("SL") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = itemState.price.value,
                    onValueChange = { itemState.price.value = it },
                    label = { Text("Giá nhập") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // Hạn sử dụng
            Box(Modifier.fillMaxWidth().clickable {
                val calendar = Calendar.getInstance()
                DatePickerDialog(context, { _, y, m, d ->
                    val cal = Calendar.getInstance()
                    cal.set(y, m, d)
                    itemState.expiryDate.value = cal.time
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
            }) {
                OutlinedTextField(
                    value = itemState.expiryDate.value?.let { sdf.format(it) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Hạn sử dụng") },
                    trailingIcon = { Icon(Icons.Default.CalendarToday, null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(disabledTextColor = Color.Black, disabledBorderColor = Color.Gray, disabledLabelColor = Color.Gray, disabledTrailingIconColor = Color.Gray)
                )
            }
        }
    }
}
