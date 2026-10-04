package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BgIndigoDark
import com.example.ui.theme.BgPurpleDark
import com.example.ui.theme.BgTealDark
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassWhiteMedium
import com.example.ui.theme.NeonCyan

@Composable
fun CyberMeshBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Base mesh gradient: Indigo -> Purple -> Teal
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        BgIndigoDark,
                        BgPurpleDark,
                        Color(0xFF130924),
                        BgTealDark
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )
            )

            // Top-left luminous cyan/blue orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x3B3B82F6),
                        Color(0x1806B6D4),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.1f, size.height * 0.15f),
                    radius = size.width * 0.65f * pulse
                ),
                center = Offset(size.width * 0.1f, size.height * 0.15f),
                radius = size.width * 0.65f * pulse
            )

            // Right-middle luminous purple/magenta orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x40A855F7),
                        Color(0x1AD946EF),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.9f, size.height * 0.45f),
                    radius = size.width * 0.7f * (2f - pulse)
                ),
                center = Offset(size.width * 0.9f, size.height * 0.45f),
                radius = size.width * 0.7f * (2f - pulse)
            )

            // Bottom-left luminous emerald/teal orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x3510B981),
                        Color(0x150F766E),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.35f, size.height * 0.85f),
                    radius = size.width * 0.75f * pulse
                ),
                center = Offset(size.width * 0.35f, size.height * 0.85f),
                radius = size.width * 0.75f * pulse
            )
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(32.dp),
    backgroundColor: Color = GlassWhiteMedium,
    borderColor: Color = GlassBorderSubtle,
    tintGradient: Brush? = null,
    elevation: Dp = 12.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color(0x601F2687),
                spotColor = Color(0x7006B6D4)
            )
            .clip(shape)
            .background(
                brush = tintGradient ?: Brush.linearGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = backgroundColor.alpha * 0.65f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(400f, 800f)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.45f),
                        borderColor.copy(alpha = 0.15f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(300f, 600f)
                ),
                shape = shape
            ),
        content = content
    )
}

@Composable
fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(alpha = 0.25f),
    dashLength: Float = 14f,
    gapLength: Float = 10f
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = size.height,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f)
        )
    }
}

@Composable
fun PulsingMicButton(
    isListening: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier.size(46.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing ring
        Box(
            modifier = Modifier
                .size(46.dp)
                .scale(if (isListening) pulseScale * 1.15f else pulseScale)
                .clip(CircleShape)
                .background(
                    if (isListening) Color(0xFFEF4444).copy(alpha = pulseAlpha)
                    else CyanGlow.copy(alpha = pulseAlpha)
                )
        )

        // Core glowing button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isListening) {
                        Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFDC2626)))
                    } else {
                        Brush.linearGradient(listOf(NeonCyan, ElectricBlue))
                    }
                )
                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                .clickable(onClick = onClick)
                .testTag("voice_mic_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "AI Voice Order",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
