package com.example.e_commercevr.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commercevr.ui.theme.AuraColors
import com.example.e_commercevr.ui.theme.AuraShapes

@Composable
fun SearchBarPill(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {},
    onArShortcutClick: () -> Unit = {}
) {
    GlassmorphismCard(
        modifier = modifier.height(48.dp),
        shape = AuraShapes.Pill,
        fillColor = AuraColors.GlassFill,
        onClick = onSearchClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = AuraColors.Primary.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Cari produk favoritmu...",
                fontSize = 14.sp,
                color = AuraColors.Primary.copy(alpha = 0.5f)
            )
        }
    }
}
