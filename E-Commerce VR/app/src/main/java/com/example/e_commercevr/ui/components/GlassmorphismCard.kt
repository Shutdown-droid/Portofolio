package com.example.e_commercevr.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.e_commercevr.ui.theme.AuraColors
import com.example.e_commercevr.ui.theme.AuraShapes

fun Modifier.glassShadow(
    color: Color = AuraColors.GlassShadowColor,
    borderRadius: Dp = 24.dp,
    blurRadius: Dp = 24.dp,
    offsetY: Dp = 8.dp,
    offsetX: Dp = 0.dp
) = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = color.toArgb()

        if (blurRadius > 0.dp) {
            frameworkPaint.maskFilter = android.graphics.BlurMaskFilter(
                blurRadius.toPx(),
                android.graphics.BlurMaskFilter.Blur.NORMAL
            )
        }

        canvas.drawRoundRect(
            left = offsetX.toPx(),
            top = offsetY.toPx(),
            right = size.width + offsetX.toPx(),
            bottom = size.height + offsetY.toPx(),
            radiusX = borderRadius.toPx(),
            radiusY = borderRadius.toPx(),
            paint = paint
        )
    }
}

@Composable
fun GlassmorphismCard(
    modifier: Modifier = Modifier,
    shape: Shape = AuraShapes.Large,
    fillColor: Color = AuraColors.GlassFill,
    strokeColor: Color = AuraColors.GlassStroke,
    strokeWidth: Dp = 1.5.dp,
    shadowColor: Color = AuraColors.GlassShadowColor,
    shadowBlur: Dp = 24.dp,
    shadowOffsetY: Dp = 8.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val baseModifier = modifier
        .glassShadow(
            color = shadowColor,
            blurRadius = shadowBlur,
            offsetY = shadowOffsetY
        )
        .clip(shape)
        .background(fillColor)
        .border(width = strokeWidth, color = strokeColor, shape = shape)
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(color = AuraColors.Primary.copy(alpha = 0.20f)),
                    onClick = onClick
                )
            } else Modifier
        )

    Box(
        modifier = baseModifier,
        content = content
    )
}
