package com.example.app_quan_ly_do_an.ui.screens.stock.import_bill

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
import com.example.app_quan_ly_do_an.ui.viewmodel.import_bill.ImportBillDetailViewModel
import com.example.app_quan_ly_do_an.ui.viewmodel.import_bill.ImportDetailUiItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportBillDetailScreen(
    navController: NavController,
    billId: String?,
    viewModel: ImportBillDetailViewModel = viewModel()
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
                Toast.makeText(context, "Xóa phiếu nhập thành công!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else {
                Toast.makeText(context, "Xóa thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show()
            }
            viewModel.resetDeleteStatus()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa phiếu nhập này? Số lượng tồn kho của các sản phẩm liên quan sẽ bị trừ đi tương ứng.") },
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
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    fun formatDate(date: Date?): String {
        return if (date == null) "..." else
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thông tin phiếu nhập", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Xóa phiếu nhập", color = Color.Red) },
                                onClick = {
                                    showMenu = false
                                    showDeleteDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
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
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Thông tin cơ bản", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(
                                    "Sửa",
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        navController.navigate(NavigationItem.EditImportBill.createRoute(billId ?: "0"))
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoField(label = "Mã phiếu nhập", value = bill?.importBillIdCode ?: "...", showCopyIcon = true, primaryColor = primaryColor)
                            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))
                            InfoField(label = "Nhà cung cấp", value = bill?.supplier ?: "...", primaryColor = primaryColor)
                            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))
                            InfoField(label = "Ngày nhập", value = formatDate(bill?.date), primaryColor = primaryColor)
                            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))
                            InfoField(label = "Tổng tiền thanh toán", value = "${"%,.0f".format(bill?.totalAmount ?: 0.0)} đ", primaryColor = primaryColor)
                        }
                    }
                }
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Danh sách hàng hóa", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
                            if (details.isEmpty()) {
                                Text("Không có sản phẩm nào", modifier = Modifier.padding(8.dp))
                            } else {
                                details.forEachIndexed { index, item ->
                                    ImportProductDetailItem(item)
                                    if (index < details.size - 1) {
                                        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoField(label: String, value: String, showCopyIcon: Boolean = false, isTotal: Boolean = false, primaryColor: Color) {
    Column {
        Text(text = label, color = Color.Gray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, fontWeight = FontWeight.SemiBold, fontSize = if (isTotal) 18.sp else 16.sp, color = if (isTotal) primaryColor else Color.Black)
            if (showCopyIcon) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = primaryColor, modifier = Modifier.size(16.dp).clickable { })
            }
        }
    }
}

@Composable
fun ImportProductDetailItem(item: ImportDetailUiItem) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFEEEEEE)), contentAlignment = Alignment.Center) {
            Text("IMG", fontSize = 10.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.productName, fontWeight = FontWeight.Medium, fontSize = 15.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Số lượng: ${item.quantity}", color = Color.Gray, fontSize = 13.sp)
        }
        Text(text = "${"%,.0f".format(item.importPrice)} đ", color = Color.Gray, fontSize = 13.sp)
    }
}
