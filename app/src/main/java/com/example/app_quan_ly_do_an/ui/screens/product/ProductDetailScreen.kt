@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.app_quan_ly_do_an.ui.screens.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreHoriz
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

import com.example.app_quan_ly_do_an.ui.components.DetailRow
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_quan_ly_do_an.ui.viewmodel.product.ProductDetailViewModel

@Composable
fun ProductDetailScreen(
    productId: String?,
    navController: NavController,
    onBack: () -> Unit = {},
    viewModel: ProductDetailViewModel = viewModel()
) {
    LaunchedEffect(productId) {
        if (productId != null) {
            viewModel.loadProduct(productId)
        }
    }
    val product by viewModel.product.collectAsState()
    // State for delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        if (product == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF0E8A38))
            }
        } else {
            val item = product!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(paddingValues)
            ) {

                // ---------------- HEADER (KHÔNG CUỘN) ----------------
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null)
                            }
                            Text(
                                "Chi tiết hàng hóa",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                              )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.MoreHoriz, contentDescription = "Xóa hàng hóa")
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ---------------- NỘI DUNG CUỘN ----------------
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 60.dp)
                ) {

                    // CARD thông tin cơ bản
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 1.dp,
                            color = Color.White
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Thông tin cơ bản",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Sửa",
                                        color = Color(0xFF0E8A38),
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.clickable {
                                            navController.navigate(
                                                NavigationItem.EditProduct.createRoute(
                                                    productId ?: ""
                                                )
                                            )
                                        }
                                    )
                                }

                                Spacer(Modifier.height(14.dp))

                                // Ảnh sản phẩm dùng Coil
                                AsyncImage(
                                    model = item.productImage.ifEmpty { null },
                                    contentDescription = item.productName,
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFEEEEEE)),
                                    contentScale = ContentScale.Crop,
                                    error = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery),
                                    placeholder = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery)
                                )

                                Spacer(Modifier.height(12.dp))

                                Text(
                                    item.productName,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(Modifier.height(16.dp))

                                DetailRow("Mã hàng", item.productCode)
                                Divider()
                                DetailRow("Giá bán", "${"%,.0f".format(item.sellPrice)} đ")
                                Divider()
                                DetailRow("Đơn vị tính", item.unit)
                                Divider()
                                DetailRow("Nhóm hàng", item.productCategory)
                                Divider()
                                DetailRow("Mức tồn tối thiểu", item.minStock.toString())
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                    }

                    // Nút xem danh sách lô hàng (CUỘN ĐƯỢC)
                    item {
                        Button(
                            onClick = {
                                navController.navigate(
                                    NavigationItem.BatchList.createRoute(productId ?: "")
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0E8A38)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Xem danh sách lô hàng", fontSize = 17.sp)
                        }
                    }

                    item { Spacer(Modifier.height(40.dp)) }
                }
            }

            // Delete Confirmation Dialog
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F)
                        )
                    },
                    title = {
                        Text(
                            text = "Xóa hàng hóa",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text("Bạn có chắc chắn muốn xóa hàng hóa này không? Hành động này không thể hoàn tác.")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                // TODO: Delete from database
                                if (productId != null) {
                                    // Gọi ViewModel để xóa
                                    viewModel.deleteProduct(productId) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("✓ Đã xóa thành công")
                                            onBack() // Quay lại màn hình trước
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFFD32F2F)
                            )
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
        }
    }
}
