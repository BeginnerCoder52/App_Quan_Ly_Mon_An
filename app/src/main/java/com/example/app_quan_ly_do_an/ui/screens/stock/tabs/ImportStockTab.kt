package com.example.app_quan_ly_do_an.ui.screens.stock.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.app_quan_ly_do_an.data.model.ImportBill
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem

@Composable
fun ImportStockTab(navController: NavController) {
    // Dữ liệu giả để hiển thị
    val sampleBills = listOf(
        ImportBill("1", "PN005", "Công ty CP Food", "07/12/2025", 5500000.0),
        ImportBill("2", "PN004", "Vinamilk", "05/12/2025", 2100000.0),
        ImportBill("3", "PN003", "Chợ Đầu Mối", "01/12/2025", 890000.0)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(sampleBills) { bill ->
            ImportBillItem(bill = bill, onClick = {
                navController.navigate(NavigationItem.ImportBillDetail.createRoute(bill.id))
            })
        }
    }
}

@Composable
fun ImportBillItem(bill: ImportBill, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = bill.code, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = bill.status, color = Color(0xFF4CAF50), style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = bill.supplierName, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = bill.date, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "${"%,.0f".format(bill.totalAmount)} đ", // Format tiền tệ
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}