package com.example.app_quan_ly_do_an.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_quan_ly_do_an.ui.theme.App_Quan_Ly_Do_AnTheme

@Composable
fun AddProductScreen(
    onBack: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ---------- HEADER ----------
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }

                    Text(
                        text = "Hàng hóa mới",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    TextButton(onClick = { /* TODO save */ }) {
                        Text(
                            "Lưu",
                            color = Color(0xFF0E8A38),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ---------- SCROLL CONTENT ----------
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {

                item {
                    Spacer(Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color(0xFF0E8A38), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }

                            ProductTextField(
                                label = "Mã hàng",
                                trailingIcon = Icons.Default.QrCodeScanner
                            )

                            ProductTextField(
                                label = "Mã vạch",
                                trailingIcon = Icons.Default.QrCodeScanner
                            )

                            ProductTextField(
                                label = "Tên hàng",
                                required = true
                            )

                            ProductDropdownField(
                                label = "Nhóm hàng",
                                required = true,
                                onClick = {
                                    // TODO: mở màn chọn nhóm hàng
                                }
                            )

                            ProductTextField(label = "Đơn vị tính")
                            ProductTextField(label = "Giá bán")
                            ProductTextField(label = "Mức tồn kho tối thiểu")
                        }
                    }
                }
            }
        }
    }
}

// ---------- TEXT FIELD ----------
@Composable
fun ProductTextField(
    label: String,
    required: Boolean = false,
    trailingIcon: ImageVector? = null
) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        label = {
            Row {
                Text(label)
                if (required) Text(" *", color = Color.Red)
            }
        },
        trailingIcon = {
            trailingIcon?.let {
                Icon(it, contentDescription = null)
            }
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0E8A38),
            focusedLabelColor = Color(0xFF0E8A38),
            cursorColor = Color(0xFF0E8A38)
        )
    )
}

// ---------- DROPDOWN GIẢ (CLICK ĐƯỢC) ----------
@Composable
fun ProductDropdownField(
    label: String,
    required: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            label = {
                Row {
                    Text(label)
                    if (required) Text(" *", color = Color.Red)
                }
            },
            trailingIcon = {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0E8A38),
                focusedLabelColor = Color(0xFF0E8A38)
            )
        )
    }
}

// ---------- PREVIEW ----------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddProductScreenPreview() {
    App_Quan_Ly_Do_AnTheme {
        AddProductScreen(onBack = {})
    }
}
