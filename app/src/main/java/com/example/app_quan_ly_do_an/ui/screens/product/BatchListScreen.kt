package com.example.app_quan_ly_do_an.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.app_quan_ly_do_an.data.model.InventoryLot
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem
import com.example.app_quan_ly_do_an.ui.viewmodel.product.BatchListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchListScreen(
    productId: String?,
    navController: NavController,
    viewModel: BatchListViewModel = viewModel(),
    onBack: () -> Unit = { navController.popBackStack() }
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        if (productId != null) {
            viewModel.loadBatches(productId)
        }
    }

    // Filter batches based on search query
    val filteredBatches = remember(searchQuery, uiState.batches) {
        if (searchQuery.isEmpty()) {
            uiState.batches
        } else {
            uiState.batches.filter { lot ->
                lot.lotCode.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    fun formatDate(date: Date?): String {
        return if (date == null) "..." else
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF0E8A38))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
            ) {
                // HEADER
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isSearching) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    placeholder = { Text("Tìm mã lô hàng...") },
                                    singleLine = true,
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            searchQuery = ""
                                            isSearching = false
                                        }) {
                                            Icon(Icons.Default.Close, contentDescription = "Close search")
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF0E8A38),
                                        focusedLabelColor = Color(0xFF0E8A38),
                                        cursorColor = Color(0xFF0E8A38)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = onBack) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                                    }
                                    Text(
                                        "Danh sách lô hàng",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row {
                                    IconButton(onClick = { isSearching = true }) {
                                        Icon(Icons.Default.Search, null)
                                    }
                                }
                            }
                        }

                        // Product info
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ảnh sản phẩm dùng Coil
                            AsyncImage(
                                model = uiState.product?.productImage?.ifEmpty { null },
                                contentDescription = uiState.product?.productName,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEEEEEE)),
                                contentScale = ContentScale.Crop,
                                error = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery),
                                placeholder = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    uiState.product?.productName ?: "...",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "Mã hàng: ${uiState.product?.productCode ?: "..."}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        // Total
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Tổng tồn", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                                Text("${filteredBatches.size} lô hàng", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text(
                                "${filteredBatches.sumOf { it.currentQuantity }}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // LIST OF BATCHES
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(filteredBatches) { lot ->
                        BatchItem(
                            lot = lot,
                            dateString = formatDate(lot.importDate),
                            onClick = {
                                navController.navigate(
                                    NavigationItem.BatchDetail.createRoute(lot.lotId)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BatchItem(lot: InventoryLot, dateString: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(lot.lotCode, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Ngày nhập: $dateString", fontSize = 13.sp, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text("Tồn: ${lot.currentQuantity}", fontSize = 13.sp)
        }
    }
}
