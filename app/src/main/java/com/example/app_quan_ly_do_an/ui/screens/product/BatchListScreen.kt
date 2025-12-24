package com.example.app_quan_ly_do_an.ui.screens.product


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.example.app_quan_ly_do_an.data.model.Batch
import com.example.app_quan_ly_do_an.data.mock.MockProductData
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchListScreen(
    productId: String?, // TODO: sẽ dùng để load data từ database
    navController: NavController,
    onBack: () -> Unit = { navController.popBackStack() }
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Filter batches based on search query
    val filteredBatches = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            MockProductData.sampleBatches
        } else {
            MockProductData.sampleBatches.filter { batch ->
                batch.batchCode.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F5F5),
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                shape = CircleShape,
                containerColor = Color(0xFF0E8A38)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {

            //-----------------------------------------
            // HEADER (Surface bo góc)
            //-----------------------------------------
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            ) {
                Column {

                    // --- Row 1: back + title + actions ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSearching) {
                            // Search TextField
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

                    // --- Row 2: product info ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Placeholder image
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFECECEC), RoundedCornerShape(8.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text("Diet Coke", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                            Text("Mã hàng: SP000001", fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    // --- Row 3: total ---
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
                            "${filteredBatches.sumOf { it.quantity }}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //-----------------------------------------
            // LIST OF BATCHES
            //-----------------------------------------
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(filteredBatches) { batch ->
                    BatchItem(
                        batch = batch,
                        onClick = {
                            navController.navigate(
                                NavigationItem.BatchDetail.createRoute(batch.batchCode)
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun BatchItem(batch: Batch, onClick: () -> Unit) {
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
            Text(batch.batchCode, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Ngày nhập: ${batch.importDate}", fontSize = 13.sp, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text("Tồn: ${batch.quantity}", fontSize = 13.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BatchListScreenPreview() {
    val navController = rememberNavController()

    BatchListScreen(
        productId = "SP000001",
        navController = navController
    )
}


