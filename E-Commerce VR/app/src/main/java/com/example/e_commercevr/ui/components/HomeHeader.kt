package com.example.e_commercevr.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commercevr.ui.theme.AuraColors

@Composable
fun HomeHeader(
    userName: String,
    cartCount: Int,
    onOpenChat: () -> Unit,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Halo, Jelajahi!",
                fontSize = 12.sp,
                color = AuraColors.Primary.copy(alpha = 0.7f)
            )
            Text(
                text = userName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AuraColors.Primary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassmorphismCard(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                onClick = onOpenChat
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Chat",
                        tint = AuraColors.Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Box(
                modifier = Modifier.size(40.dp)
            ) {
                GlassmorphismCard(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    onClick = onOpenCart
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = AuraColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (cartCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopEnd)
                            .background(color = AuraColors.Accent, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (cartCount > 99) "99+" else cartCount.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
