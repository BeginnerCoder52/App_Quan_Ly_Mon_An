package com.example.app_quan_ly_do_an.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FilterChipIconOnly()
{
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFE6F3ED)
    )
    {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Icon(
                Icons.Default.FilterList,
                contentDescription = null,
                tint = Color(0xFF0E8A38)
            )
        }
    }
}