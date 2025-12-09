package com.example.app_quan_ly_do_an.ui.screens.product


import  androidx.compose.runtime.Composable
import  androidx.compose.ui.tooling.preview.Preview
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem
import androidx.compose.material3.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout

import com.example.app_quan_ly_do_an.data.model.FoodItem
import  com.example.app_quan_ly_do_an.ui.components.FilterChipWithArrow
import  com.example.app_quan_ly_do_an.ui.components.FilterChipIconOnly
import com.example.app_quan_ly_do_an.ui.components.ProductItemPlaceHolder
@Composable
fun ProductScreen() {
    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = Color(0xFF0E8A38),
                shape = CircleShape

            ) {Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) }
        }
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
                                IconButton(onClick = {}) {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                }
                                IconButton(onClick = {})
                                {
                                    Icon(Icons.Default.Sort, contentDescription = null)
                                }
                                IconButton(onClick = {})
                                {
                                    Icon(Icons.Default.MoreVert, contentDescription = null)
                                }
                            }
                        }
                        //Filter
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        )
                        {
                            FilterChipIconOnly()
                            FilterChipWithArrow("Tất cả các loại hàng")
                            FilterChipWithArrow("Giá bán")
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
                                Text("25 hàng hóa", color = Color.Gray, fontSize = 13.sp)
                            }
                            Text("184", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                        }
                    }

            }
            Spacer(modifier = Modifier.height(15.dp))


            Surface(
                modifier = Modifier.fillMaxSize(),
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
                    items(sampleFoodItems) { item ->
                        ProductItemPlaceHolder(item)
                        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                    }

                    item{ Spacer(modifier = Modifier.height(100.dp))}

                }
            }
        }
    }

}







@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductScreenPreview()
{
    ProductScreen()
}

val sampleFoodItems = listOf(
    FoodItem(
        id = "SP000001",
        name = "Diet Coke",
        expiryDate = "12/2025",
        quantity = 100,
        category = "Nước giải khát",
        price = 10000.0
    ),
    FoodItem(
        id = "SP000002",
        name = "Apple Juice",
        expiryDate = "11/2025",
        quantity = 50,
        category = "Nước trái cây",
        price = 12000.0
    ),
    FoodItem(
        id = "SP000003",
        name = "Coca Cola",
        expiryDate = "10/2025",
        quantity = 200,
        category = "Nước giải khát",
        price = 10000.0
    ),
    FoodItem(
        id = "SP000004",
        name = "Pepsi",
        expiryDate = "12/2024",
        quantity = 80,
        category = "Nước giải khát",
        price = 9000.0
    ),
    FoodItem(
        id = "SP000005",
        name = "Fanta",
        expiryDate = "08/2025",
        quantity = 40,
        category = "Nước giải khát",
        price = 11000.0
    ),
    FoodItem(
        id = "SP000006",
        name = "Sprite",
        expiryDate = "09/2025",
        quantity = 120,
        category = "Nước giải khát",
        price = 10000.0
    ),
    FoodItem(
        id = "SP000007",
        name = "Red Bull",
        expiryDate = "04/2025",
        quantity = 60,
        category = "Năng lượng",
        price = 15000.0
    ),
    FoodItem(
        id = "SP000008",
        name = "Monster Energy",
        expiryDate = "03/2025",
        quantity = 70,
        category = "Năng lượng",
        price = 18000.0
    )
)

