package com.example.app_quan_ly_do_an.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

// HIEN'S CODE BEGIN
// 1. THÊM IMPORT ĐỂ SỬA LỖI Unresolved reference
import androidx.navigation.NavController
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem

// 2. THÊM @OptIn ĐỂ SỬA LỖI Experimental Material API
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    // 3. THÊM THAM SỐ navController ĐỂ SỬA LỖI navigate
    navController: NavController
) {
// HIEN'S CODE END
    val primaryColor = Color(0xFF006633)
    val backgroundColor = Color(0xFFF5F5F5)

    Scaffold(
        containerColor = backgroundColor, // Di chuyển màu nền vào đây cho chuẩn
        // HIEN'S CODE BEGIN
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Bây giờ navController và NavigationItem đã được hiểu
                    navController.navigate(NavigationItem.AddProduct.route)
                },
                containerColor = Color(0xFF006633),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm hàng")
            }
        },
        // HIEN'S CODE END
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
            // .background(Color(0xFFF5F5F5)) // Đã chuyển lên containerColor của Scaffold
        ) {
            // --- HEADER PROFILE ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        color = primaryColor,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar Placeholder
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Quản Lý Kho",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Cửa hàng thực phẩm sạch",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- STATS CARDS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatsCard(
                    title = "Sắp hết hạn",
                    value = "5",
                    icon = Icons.Default.Warning,
                    color = Color(0xFFE53935),
                    modifier = Modifier.weight(1f)
                )
                StatsCard(
                    title = "Cần nhập thêm",
                    value = "12",
                    icon = Icons.Default.Inventory,
                    color = Color(0xFFFFB300),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- MENU OPTIONS ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Text(
                        "Quản lý cửa hàng",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    ProfileOptionItem(icon = Icons.Default.Category, title = "Quản lý Nhóm hàng")
                    ProfileOptionItem(icon = Icons.Default.LocalShipping, title = "Quản lý Nhà cung cấp")
                    ProfileOptionItem(icon = Icons.Default.QrCode, title = "Cấu hình Barcode GS1")
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "Hệ thống",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    ProfileOptionItem(
                        icon = Icons.Default.CloudSync,
                        title = "Đồng bộ Firebase",
                        subtitle = "Trạng thái: Online"
                    )
                    ProfileOptionItem(icon = Icons.Default.Settings, title = "Cài đặt chung")
                    ProfileOptionItem(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        title = "Đăng xuất",
                        isDestructive = true
                    )
                }
                item { Spacer(modifier = Modifier.height(50.dp)) }
            }
        }
    }
}

// ... (GIỮ NGUYÊN CÁC HÀM StatsCard, ProfileOptionItem CŨ Ở DƯỚI) ...
@Composable
fun StatsCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 2.dp) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = title, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ProfileOptionItem(icon: ImageVector, title: String, subtitle: String? = null, isDestructive: Boolean = false) {
    Surface(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { }, shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 0.5.dp) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(color = if (isDestructive) Color(0xFFFFEBEE) else Color(0xFFE8F5E9), shape = CircleShape), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = if (isDestructive) Color.Red else Color(0xFF006633))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = if (isDestructive) Color.Red else Color.Black)
                if (subtitle != null) { Text(text = subtitle, fontSize = 12.sp, color = Color.Gray) }
            }
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
}