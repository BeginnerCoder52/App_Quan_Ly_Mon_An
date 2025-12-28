package com.example.app_quan_ly_do_an.ui.screens.stock.export_bill

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem
import com.example.app_quan_ly_do_an.ui.viewmodel.export_bill.ExportBillDetailViewModel
import com.example.app_quan_ly_do_an.ui.viewmodel.export_bill.ExportDetailUiItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportBillDetailScreen(
    navController: NavController,
    billId: String?,
    viewModel: ExportBillDetailViewModel = viewModel()
) {
    val primaryColor = Color(0xFF006633)
    val backgroundColor = Color(0xFFF5F5F5)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(billId) {
        billId?.let { viewModel.loadBillDetails(it) }
    }

    LaunchedEffect(uiState.deleteSuccess) {
        uiState.deleteSuccess?.let { success ->
            if (success) {
                Toast.makeText(context, "Xóa phiếu xuất thành công!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else {
                Toast.makeText(context, "Xóa thất bại!", Toast.LENGTH_SHORT).show()
            }
            viewModel.resetDeleteStatus()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc muốn xóa phiếu xuất này? Số lượng sản phẩm sẽ được cộng hoàn lại vào kho.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        billId?.let { viewModel.deleteBill(it) }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Xóa", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Hủy") }
            }
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thông tin phiếu xuất", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Xóa phiếu xuất", color = Color.Red) },
                                onClick = {
                                    showMenu = false
                                    showDeleteDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, tint = Color.Red, contentDescription = null) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else {
            val bill = uiState.billInfo
            val details = uiState.details
            LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Thông tin cơ bản", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Sửa", color = primaryColor, fontWeight = FontWeight.Bold, modifier = Modifier.clickable {
                                    navController.navigate(NavigationItem.EditExportBill.createRoute(billId ?: "0"))
                                })
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoField(label = "Mã phiếu xuất", value = bill?.exportBillCode ?: "...", showCopyIcon = true, primaryColor = primaryColor)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                            InfoField(label = "Ngày xuất", value = bill?.date?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) } ?: "...")
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                            InfoField(label = "Tổng tiền", value = "${"%,.0f".format(bill?.totalAmount ?: 0.0)} đ", primaryColor = primaryColor)
                        }
                    }
                }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Danh sách sản phẩm", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
                            details.forEachIndexed { index, item ->
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFEEEEEE)), contentAlignment = Alignment.Center) {
                                        Text("IMG", fontSize = 10.sp, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = item.productName, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                                        Text(text = "Số lượng: ${item.quantity}", color = Color.Gray, fontSize = 13.sp)
                                    }
                                    Text(text = "${"%,.0f".format(item.sellPrice)} đ", color = Color.Gray, fontSize = 13.sp)
                                }
                                if (index < details.size - 1) HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun InfoField(
    label: String,
    value: String,
    showCopyIcon: Boolean = false,
    primaryColor: Color = Color.Black
) {
    Column {
        Text(text = label, color = Color.Gray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black
            )
            if (showCopyIcon) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = primaryColor,
                    modifier = Modifier.size(16.dp).clickable { }
                )
            }
        }
    }
}

