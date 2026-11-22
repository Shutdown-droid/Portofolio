package com.example.e_commercevr.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commercevr.ui.theme.AuraColors
import com.example.e_commercevr.ui.theme.AuraShapes

@Composable
fun CategoryHorizontalScroll(
    categories: List<String>,
    selectedCategory: String,
    onSelect: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = categories,
            key = { category -> category }
        ) { category ->
            val isSelected = category == selectedCategory
            GlassmorphismCard(
                modifier = Modifier.height(36.dp),
                shape = AuraShapes.Pill,
                fillColor = if (isSelected) AuraColors.Primary else AuraColors.GlassFill,
                strokeColor = if (isSelected) AuraColors.Primary else AuraColors.GlassStroke,
                onClick = { onSelect(category) }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else AuraColors.Primary
                    )
                }
            }
        }
    }
}
