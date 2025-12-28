package com.example.app_quan_ly_do_an.ui.screens.stock.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app_quan_ly_do_an.data.model.ImportBill
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_quan_ly_do_an.ui.viewmodel.import_bill.ImportBillListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Enum sắp xếp cho Phiếu nhập
enum class ImportSortOption {
    DateNewest,
    DateOldest,
    AmountHighest,
    AmountLowest,
    SupplierAZ,
    SupplierZA
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportStockTab(
    navController: NavController,
    viewModel: ImportBillListViewModel = viewModel()
) {
    val primaryColor = Color(0xFF006633)
    val backgroundColor = Color(0xFFF5F5F5)

    val realBills by viewModel.bills.collectAsState()

    // State cho tìm kiếm và sắp xếp
    var searchQuery by remember { mutableStateOf("") }
    var showSortSheet by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf(ImportSortOption.DateNewest) }

    // Logic lọc và sắp xếp dữ liệu
    val filteredBills = remember(realBills, searchQuery, selectedSort) {
        var list = if (searchQuery.isEmpty()) {
            realBills
        } else {
            realBills.filter {
                it.importBillIdCode.contains(searchQuery, ignoreCase = true) ||
                        it.supplier.contains(searchQuery, ignoreCase = true)
            }
        }

        list = when (selectedSort) {
            ImportSortOption.DateNewest -> list.sortedByDescending { it.date }
            ImportSortOption.DateOldest -> list.sortedBy { it.date }
            ImportSortOption.AmountHighest -> list.sortedByDescending { it.totalAmount }
            ImportSortOption.AmountLowest -> list.sortedBy { it.totalAmount }
            ImportSortOption.SupplierAZ -> list.sortedBy { it.supplier }
            ImportSortOption.SupplierZA -> list.sortedByDescending { it.supplier }
        }
        list
    }

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

                SortItem("Mới nhất", selectedSort == ImportSortOption.DateNewest) { selectedSort = ImportSortOption.DateNewest; showSortSheet = false }
                SortItem("Cũ nhất", selectedSort == ImportSortOption.DateOldest) { selectedSort = ImportSortOption.DateOldest; showSortSheet = false }
                SortItem("Giá trị: Cao nhất", selectedSort == ImportSortOption.AmountHighest) { selectedSort = ImportSortOption.AmountHighest; showSortSheet = false }
                SortItem("Giá trị: Thấp nhất", selectedSort == ImportSortOption.AmountLowest) { selectedSort = ImportSortOption.AmountLowest; showSortSheet = false }
                SortItem("Nhà cung cấp: A-Z", selectedSort == ImportSortOption.SupplierAZ) { selectedSort = ImportSortOption.SupplierAZ; showSortSheet = false }
                SortItem("Nhà cung cấp: Z-A", selectedSort == ImportSortOption.SupplierZA) { selectedSort = ImportSortOption.SupplierZA; showSortSheet = false }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavigationItem.AddImportBill.route) },
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
                        Text("Phiếu nhập", fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
                            placeholder = { Text("Tìm mã phiếu, nhà cung cấp...") },
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
                    Text("Không tìm thấy phiếu nhập nào", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredBills) { bill ->
                        ImportBillItem(
                            bill = bill,
                            dateString = formatDate(bill.date),
                            primaryColor = primaryColor,
                            onClick = {
                                navController.navigate(NavigationItem.ImportBillDetail.createRoute(bill.importBillId))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SortItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF006633) else Color.Black
        )
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF006633))
        }
    }
}

@Composable
fun ImportBillItem(
    bill: ImportBill,
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
                        text = bill.importBillIdCode,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "NCC: ${bill.supplier}",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
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
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = dateString,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}