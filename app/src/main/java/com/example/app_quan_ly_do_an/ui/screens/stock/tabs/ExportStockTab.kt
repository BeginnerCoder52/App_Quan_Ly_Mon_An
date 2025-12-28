package com.example.app_quan_ly_do_an.ui.screens.stock.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app_quan_ly_do_an.data.model.ExportBill
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem
import com.example.app_quan_ly_do_an.ui.viewmodel.export_bill.ExportBillListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Enum sắp xếp cho Phiếu xuất
enum class ExportSortOption {
    DateNewest,
    DateOldest,
    AmountHighest,
    AmountLowest,
    CodeAZ,
    CodeZA
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportStockTab(
    navController: NavController,
    viewModel: ExportBillListViewModel = viewModel()
) {
    // Màu chủ đạo
    val primaryColor = Color(0xFF006633)
    val backgroundColor = Color(0xFFF5F5F5)

    // Lắng nghe dữ liệu thật từ ViewModel
    val realBills by viewModel.bills.collectAsState()

    // State cho tìm kiếm và sắp xếp
    var searchQuery by remember { mutableStateOf("") }
    var showSortSheet by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf(ExportSortOption.DateNewest) }

    // Logic lọc và sắp xếp dữ liệu
    val filteredBills = remember(realBills, searchQuery, selectedSort) {
        var list = if (searchQuery.isEmpty()) {
            realBills
        } else {
            realBills.filter {
                it.exportBillCode.contains(searchQuery, ignoreCase = true)
            }
        }

        list = when (selectedSort) {
            ExportSortOption.DateNewest -> list.sortedByDescending { it.date }
            ExportSortOption.DateOldest -> list.sortedBy { it.date }
            ExportSortOption.AmountHighest -> list.sortedByDescending { it.totalAmount }
            ExportSortOption.AmountLowest -> list.sortedBy { it.totalAmount }
            ExportSortOption.CodeAZ -> list.sortedBy { it.exportBillCode }
            ExportSortOption.CodeZA -> list.sortedByDescending { it.exportBillCode }
        }
        list
    }

    // Hàm format ngày tháng (Date -> String)
    fun formatDate(date: Date?): String {
        if (date == null) return "Chưa rõ"
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

    // Bottom Sheet sắp xếp
    if (showSortSheet) {
        ModalBottomSheet(onDismissRequest = { showSortSheet = false }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Sắp xếp theo", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))

                SortItem("Mới nhất", selectedSort == ExportSortOption.DateNewest) { selectedSort = ExportSortOption.DateNewest; showSortSheet = false }
                SortItem("Cũ nhất", selectedSort == ExportSortOption.DateOldest) { selectedSort = ExportSortOption.DateOldest; showSortSheet = false }
                SortItem("Giá trị: Cao nhất", selectedSort == ExportSortOption.AmountHighest) { selectedSort = ExportSortOption.AmountHighest; showSortSheet = false }
                SortItem("Giá trị: Thấp nhất", selectedSort == ExportSortOption.AmountLowest) { selectedSort = ExportSortOption.AmountLowest; showSortSheet = false }
                SortItem("Mã phiếu: A-Z", selectedSort == ExportSortOption.CodeAZ) { selectedSort = ExportSortOption.CodeAZ; showSortSheet = false }
                SortItem("Mã phiếu: Z-A", selectedSort == ExportSortOption.CodeZA) { selectedSort = ExportSortOption.CodeZA; showSortSheet = false }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavigationItem.AddExportBill.route) },
                containerColor = primaryColor,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            // --- HEADER SECTION (Giống ProductScreen) ---
            Surface(
                shadowElevation = 2.dp,
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Phiếu xuất", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Tổng: ${filteredBills.size} phiếu",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Tìm mã phiếu xuất...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = Color.LightGray
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(onClick = { showSortSheet = true }) {
                            Icon(Icons.Default.SwapVert, contentDescription = "Sort")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- LIST SECTION ---
            if (filteredBills.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không tìm thấy phiếu xuất nào", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredBills) { bill ->
                        ExportBillItem(
                            bill = bill,
                            dateString = formatDate(bill.date),
                            primaryColor = primaryColor,
                            onClick = {
                                navController.navigate(NavigationItem.ExportBillDetail.createRoute(bill.exportBillId))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExportBillItem(
    bill: ExportBill,
    dateString: String,
    primaryColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = bill.exportBillCode,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ngày xuất: $dateString",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }

                Text(
                    text = "${"%,.0f".format(bill.totalAmount)} đ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = primaryColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Receipt,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Nhấp để xem chi tiết",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
