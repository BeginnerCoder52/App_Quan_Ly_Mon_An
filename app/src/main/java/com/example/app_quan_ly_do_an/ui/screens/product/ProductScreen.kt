package com.example.app_quan_ly_do_an.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.navigation.NavController
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem

// HIEN'S CODE BEGIN
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage // Thư viện load ảnh
import com.example.app_quan_ly_do_an.data.model.Product
import com.example.app_quan_ly_do_an.ui.viewmodel.product.ProductViewModel
// HIEN'S CODE END

// Enum sắp xếp
enum class SortOption {
    NameAZ,
    NameZA,
    PriceAscending,
    PriceDescending,
    StockAscending,
    StockDescending
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    navController: NavController,
    // HIEN'S CODE BEGIN
    // Inject ViewModel
    viewModel: ProductViewModel = viewModel()
    // HIEN'S CODE END
) {
    // HIEN'S CODE BEGIN
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    // HIEN'S CODE END

    var searchQuery by remember { mutableStateOf("") }
    var showSortSheet by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf(SortOption.NameAZ) }

    // HIEN'S CODE BEGIN
    val filteredProducts = remember(products, searchQuery, selectedSort) {
        var list = if (searchQuery.isEmpty()) {
            products
        } else {
            products.filter {
                it.productName.contains(searchQuery, ignoreCase = true) ||
                        it.productCode.contains(searchQuery, ignoreCase = true)
            }
        }

        list = when (selectedSort) {
            SortOption.NameAZ -> list.sortedBy { it.productName }
            SortOption.NameZA -> list.sortedByDescending { it.productName }
            SortOption.PriceAscending -> list.sortedBy { it.sellPrice }
            SortOption.PriceDescending -> list.sortedByDescending { it.sellPrice }
            SortOption.StockAscending -> list.sortedBy { it.totalStock }
            SortOption.StockDescending -> list.sortedByDescending { it.totalStock }
        }
        list
    }
    // HIEN'S CODE END

    if (showSortSheet) {
        ModalBottomSheet(onDismissRequest = { showSortSheet = false }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Sắp xếp theo", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))

                SortOptionItem("Tên: A-Z", selectedSort == SortOption.NameAZ) { selectedSort = SortOption.NameAZ; showSortSheet = false }
                SortOptionItem("Tên: Z-A", selectedSort == SortOption.NameZA) { selectedSort = SortOption.NameZA; showSortSheet = false }
                SortOptionItem("Giá: Tăng dần", selectedSort == SortOption.PriceAscending) { selectedSort = SortOption.PriceAscending; showSortSheet = false }
                SortOptionItem("Giá: Giảm dần", selectedSort == SortOption.PriceDescending) { selectedSort = SortOption.PriceDescending; showSortSheet = false }
                SortOptionItem("Tồn kho: Tăng dần", selectedSort == SortOption.StockAscending) { selectedSort = SortOption.StockAscending; showSortSheet = false }
                SortOptionItem("Tồn kho: Giảm dần", selectedSort == SortOption.StockDescending) { selectedSort = SortOption.StockDescending; showSortSheet = false }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    Scaffold(
        // HIEN'S CODE BEGIN
        // --- NÚT THÊM HÀNG HÓA (+) ---
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavigationItem.AddProduct.route) },
                containerColor = Color(0xFF006633),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm hàng")
            }
        }
        // HIEN'S CODE END
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            // --- HEADER: Search & Sort ---
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
                        Text("Hàng hóa", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                        // HIEN'S CODE BEGIN
                        Text(
                            "Tổng: ${products.size} mã",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        // HIEN'S CODE END
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Tìm tên, mã hàng...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF006633),
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

            // --- PRODUCT LIST ---
            // HIEN'S CODE BEGIN
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF006633))
                }
            } else if (filteredProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không tìm thấy sản phẩm nào", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
                ) {
                    items(filteredProducts) { product ->
                        ProductItemRow(
                            product = product,
                            onClick = {
                                navController.navigate(NavigationItem.ProductDetail.createRoute(product.productId))
                            }
                        )
                    }
                }
            }
            // HIEN'S CODE END
        }
    }
}

@Composable
fun SortOptionItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Color(0xFF006633) else Color.Black)
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF006633))
        }
    }
}

// HIEN'S CODE BEGIN
@Composable
fun ProductItemRow(product: Product, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.productImage.ifEmpty { null },
                contentDescription = product.productName,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEEEEEE)),
                contentScale = ContentScale.Crop,
                error = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery),
                placeholder = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Mã: ${product.productCode}", color = Color.Gray, fontSize = 13.sp)
                Text(
                    text = "Tồn kho: ${product.totalStock} ${product.unit}",
                    color = if (product.totalStock <= product.minStock) Color.Red else Color(0xFF0E8A38),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "${"%,.0f".format(product.sellPrice)} đ",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black
            )
        }
    }
}
// HIEN'S CODE END