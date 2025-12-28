package com.example.app_quan_ly_do_an.ui.screens.profile

import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.app_quan_ly_do_an.ui.navigation.NavigationItem
import androidx.compose.ui.platform.LocalContext
import com.example.app_quan_ly_do_an.data.repository.AuthRepository
import com.example.app_quan_ly_do_an.data.model.User
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ProfileScreen(navController: NavController) {
    val primaryColor = Color(0xFF006633)
    val backgroundColor = Color(0xFFF5F5F5)
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }

    // State để lưu thông tin user
    var currentUser by remember { mutableStateOf<User?>(null) }
    var isLoadingUser by remember { mutableStateOf(true) }

    // State để hiển thị dialog xác nhận đăng xuất
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Lấy thông tin user khi màn hình được load
    LaunchedEffect(Unit) {
        isLoadingUser = true
        currentUser = authRepository.getCurrentUser()
        isLoadingUser = false
    }

    // Dialog xác nhận đăng xuất
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Color(0xFF0E8A38)
                )
            },
            title = {
                Text(
                    text = "Đăng xuất",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        // Đăng xuất khỏi Firebase
                        authRepository.logout()
                        Toast.makeText(context, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show()
                        // Quay về trang Login và xóa toàn bộ back stack
                        navController.navigate(NavigationItem.Login.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                ) {
                    Text("Đăng xuất", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Hủy", color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- HEADER PROFILE ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(color = primaryColor, shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isLoadingUser) {
                    // Hiển thị loading khi đang tải thông tin user
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = primaryColor, modifier = Modifier.size(50.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        // Hiển thị tên từ Firebase Auth
                        Text(
                            text = currentUser?.displayName ?: "Người dùng",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        // Hiển thị email từ Firebase Auth
                        Text(
                            text = currentUser?.email ?: "",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- STATS CARDS ---
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatsCard(title = "Sắp hết hạn", value = "5", icon = Icons.Default.Warning, color = Color(0xFFE53935), modifier = Modifier.weight(1f))
                StatsCard(title = "Cần nhập thêm", value = "12", icon = Icons.Default.Inventory, color = Color(0xFFFFB300), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- MENU OPTIONS ---
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                item {
                    Text("Quản lý cửa hàng", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                }

                item {
                    ProfileOptionItem(icon = Icons.Default.Category, title = "Quản lý Nhóm hàng") {
                        Toast.makeText(context, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
                    }
                    ProfileOptionItem(icon = Icons.Default.LocalShipping, title = "Quản lý Nhà cung cấp") {
                        Toast.makeText(context, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
                    }
                    ProfileOptionItem(icon = Icons.Default.QrCode, title = "Cấu hình Barcode GS1") {
                        Toast.makeText(context, "Đã lưu cấu hình mặc định", Toast.LENGTH_SHORT).show()
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Hệ thống", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                }

                item {
                    // HIEN'S CODE BEGIN
                    // Liên kết mục này với Màn hình Thông báo (Notification)
                    ProfileOptionItem(
                        icon = Icons.Default.Notifications,
                        title = "Nhật ký hoạt động",
                        subtitle = "Xem lịch sử nhập/xuất"
                    ) {
                        navController.navigate(NavigationItem.Notification.route)
                    }
                    // HIEN'S CODE END

                    ProfileOptionItem(icon = Icons.Default.Settings, title = "Cài đặt chung") {
                        Toast.makeText(context, "Mở cài đặt...", Toast.LENGTH_SHORT).show()
                    }

                    ProfileOptionItem(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        title = "Đăng xuất",
                        isDestructive = true
                    ) {
                        // Hiển thị dialog xác nhận
                        showLogoutDialog = true
                    }
                }
                item { Spacer(modifier = Modifier.height(50.dp)) }
            }
        }
    }
}

// ... (StatsCard giữ nguyên) ...
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

// HIEN'S CODE BEGIN
// Cập nhật hàm này thêm tham số onClick để xử lý sự kiện
@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isDestructive: Boolean = false,
    onClick: () -> Unit // Thêm callback click
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }, // Gọi callback khi bấm
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 0.5.dp
    ) {
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
// HIEN'S CODE END