package com.example.app_quan_ly_do_an.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.app_quan_ly_do_an.data.model.FoodItem
@Composable
fun ProductItemPlaceHolder(item: FoodItem)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color(0xFFECECEC), shape = RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        )
        {
            Text("IMG", fontSize = 10.sp, color = Color.DarkGray)
        }
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier
            .weight(1f)
        ) {
            Text(item.name, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.id, fontSize = 12.sp, color = Color.Gray)
        }
        Column(horizontalAlignment = Alignment.End)
        {
            Text(item.price.toString(), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Tồn: ${item.quantity}", color = Color.Gray, fontSize = 12.sp)
        }

    }

}