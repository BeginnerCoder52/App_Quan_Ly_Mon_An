package com.example.app_quan_ly_do_an.ui.screens.product


import  androidx.compose.runtime.Composable
import  androidx.compose.ui.tooling.preview.Preview

import androidx.compose.material3.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import com.example.app_quan_ly_do_an.data.mock.MockProductData
import com.example.app_quan_ly_do_an.ui.components.ProductItemPlaceHolder
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchProductScreen(
    onBack: () -> Unit,
    onProductClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Filter products based on search query (by name or product ID)
    val filteredProducts = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            emptyList()
        } else {
            MockProductData.sampleFoodItems.filter { product ->
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.id.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Search Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Tìm kiếm hàng hóa",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Search TextField
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    placeholder = { Text("Tên, mã hàng, mã vạch") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    },
                    trailingIcon = {
                        Row {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                                }
                            }
                            IconButton(onClick = { /* QR Scanner - to be implemented */ }) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", tint = Color.Gray)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0E8A38),
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                // Cancel button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onBack) {
                        Text("Hủy", color = Color(0xFF0E8A38), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Search Results
        if (searchQuery.isEmpty()) {
            // Empty state - show hint
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Nhập tên hoặc mã sản phẩm để tìm kiếm",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        } else if (filteredProducts.isEmpty()) {
            // No results
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Không tìm thấy sản phẩm",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Thử tìm kiếm với từ khóa khác",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            // Show results
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                items(filteredProducts) { product ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProductClick(product.id) },
                        color = Color.White
                    ) {
                        Column {
                            ProductItemPlaceHolder(product)
                            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(navController: NavController) {
    var showSortBottomSheet by remember { mutableStateOf(false) }
    var showSearchScreen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var sortOption by remember { mutableStateOf<SortOption>(SortOption.None) }

    // Snackbar state for notifications
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Sorted list based on selected sort option
    val sortedFoodItems = remember(sortOption) {
        when (sortOption) {
            SortOption.TimeNewest -> MockProductData.sampleFoodItems.sortedByDescending { it.expiryDate }
            SortOption.TimeOldest -> MockProductData.sampleFoodItems.sortedBy { it.expiryDate }
            SortOption.PriceAscending -> MockProductData.sampleFoodItems.sortedBy { it.price }
            SortOption.PriceDescending -> MockProductData.sampleFoodItems.sortedByDescending { it.price }
            SortOption.NameAZ -> MockProductData.sampleFoodItems.sortedBy { it.name }
            SortOption.NameZA -> MockProductData.sampleFoodItems.sortedByDescending { it.name }
            SortOption.StockAscending -> MockProductData.sampleFoodItems.sortedBy { it.quantity }
            SortOption.StockDescending -> MockProductData.sampleFoodItems.sortedByDescending { it.quantity }
            else -> MockProductData.sampleFoodItems
        }
    }

    // Show search screen if enabled
    if (showSearchScreen) {
        SearchProductScreen(
            onBack = { showSearchScreen = false },
            onProductClick = { productId ->
                showSearchScreen = false
                navController.navigate(NavigationItem.ProductDetail.createRoute(productId))
            }
        )
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(NavigationItem.AddProduct.route)
                },
                containerColor = Color(0xFF0E8A38),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm sản phẩm")
            }
        },
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                shadowElevation = 2.dp
            ) {

                    Column {

                        //Title và action icons:

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text("Hàng hóa", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Row {
                                IconButton(onClick = { showSearchScreen = true }) {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                }
                                IconButton(onClick = { showSortBottomSheet = true })
                                {
                                    Icon(Icons.Default.SwapVert, contentDescription = null)
                                }
                            }
                        }

                        //Tổng tồn:

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        )
                        {
                            Column{
                                Text("Tổng tồn", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                                Text("${MockProductData.getTotalProducts()} hàng hóa", color = Color.Gray, fontSize = 13.sp)
                            }
                            Text("${MockProductData.getTotalStock()}", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                        }
                    }

            }
            Spacer(modifier = Modifier.height(15.dp))


            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                shadowElevation = 1.dp
            ){
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp)
                )
                {
                    items(sortedFoodItems) { item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(
                                        NavigationItem.ProductDetail.createRoute(item.id)
                                    )
                                }
                        ) {
                            ProductItemPlaceHolder(item)
                            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                        }
                    }

                    item{ Spacer(modifier = Modifier.height(100.dp))}

                }
            }
        }

        // Bottom Sheet for Sorting
        if (showSortBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSortBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Sắp xếp hàng hóa",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                    // Sorting options - Thời gian
                    SortOptionItem("Thời gian", isHeader = true) { }
                    SortOptionItem(
                        text = "Mới nhất",
                        isSelected = sortOption == SortOption.TimeNewest
                    ) {
                        sortOption = SortOption.TimeNewest
                        showSortBottomSheet = false
                    }
                    SortOptionItem(
                        text = "Cũ nhất",
                        isSelected = sortOption == SortOption.TimeOldest
                    ) {
                        sortOption = SortOption.TimeOldest
                        showSortBottomSheet = false
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                    // Giá
                    SortOptionItem("Giá", isHeader = true) { }
                    SortOptionItem(
                        text = "Tăng dần",
                        isSelected = sortOption == SortOption.PriceAscending
                    ) {
                        sortOption = SortOption.PriceAscending
                        showSortBottomSheet = false
                    }
                    SortOptionItem(
                        text = "Giảm dần",
                        isSelected = sortOption == SortOption.PriceDescending
                    ) {
                        sortOption = SortOption.PriceDescending
                        showSortBottomSheet = false
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                    // Tên hàng
                    SortOptionItem("Tên hàng", isHeader = true) { }
                    SortOptionItem(
                        text = "A - Z",
                        isSelected = sortOption == SortOption.NameAZ
                    ) {
                        sortOption = SortOption.NameAZ
                        showSortBottomSheet = false
                    }
                    SortOptionItem(
                        text = "Z - A",
                        isSelected = sortOption == SortOption.NameZA
                    ) {
                        sortOption = SortOption.NameZA
                        showSortBottomSheet = false
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                    // Tồn kho
                    SortOptionItem("Tồn kho", isHeader = true) { }
                    SortOptionItem(
                        text = "Tăng dần",
                        isSelected = sortOption == SortOption.StockAscending
                    ) {
                        sortOption = SortOption.StockAscending
                        showSortBottomSheet = false
                    }
                    SortOptionItem(
                        text = "Giảm dần",
                        isSelected = sortOption == SortOption.StockDescending
                    ) {
                        sortOption = SortOption.StockDescending
                        showSortBottomSheet = false
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                    // Khách hàng đặt (placeholder - can be implemented later)
                    SortOptionItem("Khách hàng đặt", isHeader = true) { }
                    SortOptionItem("Tăng dần") {
                        showSortBottomSheet = false
                    }
                    SortOptionItem("Giảm dần") {
                        showSortBottomSheet = false
                    }
                }
            }
        }
    }

}

// Enum for sort options
enum class SortOption {
    None,
    TimeNewest,
    TimeOldest,
    PriceAscending,
    PriceDescending,
    NameAZ,
    NameZA,
    StockAscending,
    StockDescending
}

@Composable
fun SortOptionItem(
    text: String,
    isHeader: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!isHeader) onClick() }
            .background(if (isSelected) Color(0xFFE8F5E9) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = if (isHeader) 16.sp else 16.sp,
            fontWeight = if (isHeader) FontWeight.SemiBold else FontWeight.Normal,
            color = when {
                isSelected -> Color(0xFF0E8A38)
                isHeader -> Color.Black
                else -> Color.DarkGray
            }
        )

        if (isSelected && !isHeader) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color(0xFF0E8A38),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductScreenPreview() {
    ProductScreen(navController = rememberNavController())
}
