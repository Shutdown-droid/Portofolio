package com.example.e_commercevr.ar.ui

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commercevr.ui.components.GlassmorphismCard
import com.example.e_commercevr.ui.theme.AuraColors
import com.example.e_commercevr.ui.theme.AuraShapes

@Composable
fun ArOverlayComposeView(
    trackingMode: Any,
    objectScale: Float,
    isFlashOn: Boolean,
    fps: Float,
    trackingStatusText: String,
    capturedBitmap: Bitmap?,
    productName: String = "Produk AR",
    productPrice: Long = 0L,
    onScaleChange: (Float) -> Unit,
    onToggleFlash: () -> Unit,
    onSwitchCamera: () -> Unit,
    onCaptureClick: () -> Unit,
    onShareCapturedBitmap: (Bitmap) -> Unit,
    onSaveCapturedBitmap: (Bitmap) -> Unit,
    onDismissCapturedDialog: () -> Unit,
    onCloseClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // 1. TOP BAR SYMMETRICAL ROW (Close + Status Pill)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AuraColors.GlassFill)
                    .border(1.dp, AuraColors.GlassStroke, CircleShape)
                    .clickable { onCloseClick() },
                contentAlignment = Alignment.Center
            ) {
                Text("✕", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .background(AuraColors.GlassFill, AuraShapes.Pill)
                    .border(1.dp, AuraColors.GlassStroke, AuraShapes.Pill),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = trackingStatusText,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }

        // 2. VERTICAL SCALE SLIDER (Kanan Tengah)
        GlassmorphismCard(
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp).width(50.dp).height(160.dp),
            shape = AuraShapes.Pill,
            fillColor = AuraColors.GlassFill
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("＋", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onScaleChange((objectScale + 0.1f).coerceAtMost(1.5f)) })
                Text("${(objectScale * 100).toInt()}%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                Text("－", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onScaleChange((objectScale - 0.1f).coerceAtLeast(0.5f)) })
            }
        }

        // 3. SHUTTER CAPTURE (Tengah Bawah)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(72.dp)
                .clip(CircleShape)
                .border(3.dp, Color.White, CircleShape)
                .clickable { onCaptureClick() }
                .padding(4.dp)
                .background(Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }

        // 4. PRODUCT INFO CARD (Di atas Shutter)
        GlassmorphismCard(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 120.dp).fillMaxWidth(0.9f).height(72.dp),
            shape = AuraShapes.Large,
            fillColor = AuraColors.GlassFillHeavy
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(productName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    val formattedPrice = "Rp " + productPrice.toString().reversed().chunked(3).joinToString(".").reversed()
                    Text(formattedPrice, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AuraColors.Accent)
                }
                GlassmorphismCard(
                    modifier = Modifier.height(40.dp).width(100.dp),
                    shape = AuraShapes.Medium,
                    fillColor = AuraColors.Accent,
                    strokeColor = Color.Transparent,
                    onClick = { /* Logika tambah ke keranjang */ }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tambah", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
