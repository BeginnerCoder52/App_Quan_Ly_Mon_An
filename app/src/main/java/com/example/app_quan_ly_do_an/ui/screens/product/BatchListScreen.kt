package com.example.app_quan_ly_do_an.ui.screens.product


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable

import androidx.navigation.compose.rememberNavController

import androidx.navigation.NavController
import com.example.app_quan_ly_do_an.data.model.Batch
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem


@Composable
fun BatchListScreen(
    productId: String?,
    navController: NavController,
    onBack: () -> Unit = { navController.popBackStack() }
) {
    // Sample data để test UI
    val sampleBatches = listOf(
        Batch("LOT0001", "22/11/2025", 100),
        Batch("LOT0002", "22/11/2025", 100),
        Batch("LOT0003", "22/11/2025", 100),
        Batch("LOT0004", "22/11/2025", 100),
        Batch("LOT0005", "22/11/2025", 100)
    )

    Scaffold(
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
                            IconButton(onClick = {}) { Icon(Icons.Default.Search, null) }
                            IconButton(onClick = {}) { Icon(Icons.Default.Sort, null) }
                            IconButton(onClick = {}) { Icon(Icons.Default.Tune, null) }
                            IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null) }
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
                            Text("25 lô hàng", fontSize = 12.sp, color = Color.Gray)
                        }
                        Text("184", fontSize = 22.sp, fontWeight = FontWeight.Bold)
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
                items(sampleBatches) { batch ->
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
    val navController = androidx.navigation.compose.rememberNavController()

    BatchListScreen(
        productId = "SP000001",
        navController = navController
    )
}
