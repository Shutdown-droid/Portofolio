package com.example.e_commercevr.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commercevr.ui.components.GlassmorphismCard
import com.example.e_commercevr.ui.components.ProductItem
import com.example.e_commercevr.ui.theme.AuraColors
import com.example.e_commercevr.ui.theme.AuraShapes

enum class ARTrackingMode {
    SURFACE,
    OBJECT,
    FACE
}

@Composable
fun ARViewScreen(
    product: ProductItem,
    onClose: () -> Unit,
    onAddToCart: (ProductItem) -> Unit
) {
    var trackingMode by remember { mutableStateOf(ARTrackingMode.SURFACE) }
    var scale by remember { mutableStateOf(1.0f) }
    var isFlashOn by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        ArCoreSceneViewport(
            mode = trackingMode,
            scale = scale,
            modelUrl = product.model3dUrl
        )

        ArCrosshairGuide(modifier = Modifier.fillMaxSize())

        TopArOverlay(
            trackingMode = trackingMode,
            isFlashOn = isFlashOn,
            onClose = onClose,
            onToggleFlash = { isFlashOn = !isFlashOn },
            onSwitchCamera = {},
            onSwitchMode = { trackingMode = it },
            modifier = Modifier.padding(16.dp)
        )

        VerticalScaleSlider(
            scale = scale,
            onScaleChange = { scale = it },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        )

        BottomArOverlay(
            product = product,
            onCapture = {},
            onAdd = { onAddToCart(product) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

// =========================================================
// KOMPONEN PENDUKUNG AR VIEW
// =========================================================

@Composable
fun ArCoreSceneViewport(
    mode: ARTrackingMode,
    scale: Float,
    modelUrl: String,
    modifier: Modifier = Modifier
) {
    // CATATAN PENTING: Ini adalah placeholder UI.
    // Implementasi ARCore asli (ArFragment/SurfaceView) akan dimasukkan di sini nanti
    // menggunakan AndroidView wrapper. Untuk sekarang, ini mensimulasikan tampilan kamera.
    Box(
        modifier = modifier
            .background(Color(0xFF2C3E50)) // Warna simulasi kamera
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Kamera AR Aktif\n(Model: $modelUrl)",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ArCrosshairGuide(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val lineLength = 20.dp.toPx()

        // Garis tengah vertikal
        drawLine(
            color = Color.White.copy(alpha = 0.6f),
            start = androidx.compose.ui.geometry.Offset(centerX, centerY - lineLength),
            end = androidx.compose.ui.geometry.Offset(centerX, centerY + lineLength),
            strokeWidth = 2.dp.toPx()
        )
        // Garis tengah horizontal
        drawLine(
            color = Color.White.copy(alpha = 0.6f),
            start = androidx.compose.ui.geometry.Offset(centerX - lineLength, centerY),
            end = androidx.compose.ui.geometry.Offset(centerX + lineLength, centerY),
            strokeWidth = 2.dp.toPx()
        )
    }
}

@Composable
fun TopArOverlay(
    trackingMode: ARTrackingMode,
    isFlashOn: Boolean,
    onClose: () -> Unit,
    onToggleFlash: () -> Unit,
    onSwitchCamera: () -> Unit,
    onSwitchMode: (ARTrackingMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tombol Close
        GlassmorphismCard(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            fillColor = AuraColors.GlassFill,
            onClick = onClose
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("✕", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Tombol Flash & Switch (Placeholder)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GlassmorphismCard(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                fillColor = AuraColors.GlassFill,
                onClick = onToggleFlash
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("⚡", color = if (isFlashOn) AuraColors.Accent else Color.White, fontSize = 20.sp)
                }
            }
            GlassmorphismCard(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                fillColor = AuraColors.GlassFill,
                onClick = onSwitchCamera
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("🔄", color = Color.White, fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
fun VerticalScaleSlider(
    scale: Float,
    onScaleChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphismCard(
        modifier = modifier.width(50.dp).height(200.dp),
        shape = AuraShapes.Pill,
        fillColor = AuraColors.GlassFill
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("＋", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onScaleChange((scale + 0.1f).coerceAtMost(1.5f)) })
            Text("${(scale * 100).toInt()}%", color = Color.White, fontSize = 10.sp)
            Text("－", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onScaleChange((scale - 0.1f).coerceAtLeast(0.5f)) })
        }
    }
}

@Composable
fun BottomArOverlay(
    product: ProductItem,
    onCapture: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tombol Shutter (Capture)
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .border(3.dp, Color.White, CircleShape)
                .clickable { onCapture() }
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

        Spacer(modifier = Modifier.height(16.dp))

        // Kartu Info Produk di Bawah
        GlassmorphismCard(
            modifier = Modifier.fillMaxWidth().height(72.dp),
            shape = AuraShapes.Large,
            fillColor = AuraColors.GlassFillHeavy
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(product.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Text("Rp ${product.price}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AuraColors.Accent)
                }
                GlassmorphismCard(
                    modifier = Modifier.height(40.dp).width(100.dp),
                    shape = AuraShapes.Medium,
                    fillColor = AuraColors.Accent,
                    strokeColor = Color.Transparent,
                    onClick = onAdd
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tambah", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
