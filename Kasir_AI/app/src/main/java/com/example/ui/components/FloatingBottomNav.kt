package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FloatingBottomNav(
    activeTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .shadow(
                elevation = 20.dp,
                shape = CircleShape,
                ambientColor = Color(0x900A0F1D),
                spotColor = Color(0xA01E1B4B)
            )
            .clip(CircleShape)
            .background(Color(0xE60C0B24)) // Dark solid overlay + glass tint to prevent content bleed
            .border(1.2.dp, Color.White.copy(alpha = 0.25f), CircleShape)
            .padding(5.dp)
            .testTag("floating_bottom_nav")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🏠 Kasir item
            val isKasirActive = activeTab == "kasir"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (isKasirActive) Color.White.copy(alpha = 0.22f)
                        else Color.Transparent
                    )
                    .border(
                        1.dp,
                        if (isKasirActive) Color.White.copy(alpha = 0.4f)
                        else Color.Transparent,
                        CircleShape
                    )
                    .clickable { onTabSelected("kasir") }
                    .testTag("nav_kasir_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Kasir",
                        tint = if (isKasirActive) Color.White else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🏠 Kasir",
                        color = if (isKasirActive) Color.White else Color.White.copy(alpha = 0.6f),
                        fontSize = 12.5.sp,
                        fontWeight = if (isKasirActive) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }

            // 📊 Laporan item
            val isLaporanActive = activeTab == "laporan"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (isLaporanActive) Color.White.copy(alpha = 0.22f)
                        else Color.Transparent
                    )
                    .border(
                        1.dp,
                        if (isLaporanActive) Color.White.copy(alpha = 0.4f)
                        else Color.Transparent,
                        CircleShape
                    )
                    .clickable { onTabSelected("laporan") }
                    .testTag("nav_laporan_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "Laporan",
                        tint = if (isLaporanActive) Color.White else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "📊 Laporan",
                        color = if (isLaporanActive) Color.White else Color.White.copy(alpha = 0.6f),
                        fontSize = 12.5.sp,
                        fontWeight = if (isLaporanActive) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
