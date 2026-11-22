package com.example.e_commercevr.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commercevr.ui.theme.AuraColors
import com.example.e_commercevr.ui.theme.AuraShapes

@Composable
fun ProductDetailBottomBar(
    isGuest: Boolean,
    onTryArClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onBuyNowClick: () -> Unit,
    onShowLoginBottomSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        GlassmorphismCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = AuraShapes.Large,
            fillColor = AuraColors.GlassFill,
            strokeColor = AuraColors.GlassStroke,
            strokeWidth = 1.5.dp,
            shadowColor = AuraColors.GlassShadowColor,
            shadowBlur = 24.dp,
            shadowOffsetY = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InteractiveScaleButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AuraColors.GlassFillHeavy)
                        .border(1.5.dp, AuraColors.Primary.copy(alpha = 0.30f), RoundedCornerShape(16.dp)),
                    rippleColor = AuraColors.Primary.copy(alpha = 0.10f),
                    onClick = onTryArClick
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Coba AR",
                            tint = AuraColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Coba AR",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = AuraColors.Primary
                        )
                    }
                }

                InteractiveScaleButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AuraColors.GlassFillHeavy)
                        .border(1.5.dp, AuraColors.Primary.copy(alpha = 0.30f), RoundedCornerShape(16.dp)),
                    rippleColor = AuraColors.Primary.copy(alpha = 0.10f),
                    onClick = {
                        if (isGuest) {
                            onShowLoginBottomSheet()
                        } else {
                            onAddToCartClick()
                        }
                    }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Tambah Keranjang",
                            tint = AuraColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Keranjang",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = AuraColors.Primary
                        )
                    }
                }

                InteractiveScaleButton(
                    modifier = Modifier
                        .weight(1.1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AuraColors.Accent),
                    rippleColor = Color.White.copy(alpha = 0.30f),
                    onClick = {
                        if (isGuest) {
                            onShowLoginBottomSheet()
                        } else {
                            onBuyNowClick()
                        }
                    }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Beli Sekarang",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Beli",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveScaleButton(
    modifier: Modifier = Modifier,
    rippleColor: Color = Color.White.copy(alpha = 0.20f),
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
        label = "pressScaleAnimation"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(color = rippleColor),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
