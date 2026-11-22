package com.example.e_commercevr.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.e_commercevr.ui.components.GlassmorphismCard
import com.example.e_commercevr.ui.components.ProductDetailBottomBar
import com.example.e_commercevr.ui.theme.AuraColors
import com.example.e_commercevr.ui.theme.AuraShapes
import com.example.e_commercevr.ui.viewmodel.ProductDetailViewModel

fun formatRupiah(v: Long): String =
    "Rp " + v.toString().reversed().chunked(3).joinToString(".").reversed()

private fun variantColor(name: String): Color = when {
    name.contains("krem", ignoreCase = true) || name.contains("cream", true) -> Color(0xFFF3EEDC)
    name.contains("abu", true) || name.contains("gray", true) || name.contains("grey", true) -> Color(0xFFB9C4CE)
    name.contains("coklat", true) || name.contains("brown", true) -> Color(0xFF6B4A32)
    name.contains("hitam", true) || name.contains("black", true) || name.contains("navy", true) -> Color(0xFF2B3467)
    name.contains("biru", true) || name.contains("blue", true) -> Color(0xFFBAD7E9)
    name.contains("putih", true) || name.contains("white", true) -> Color(0xFFFAFAF7)
    else -> AuraColors.Secondary
}

@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: ProductDetailViewModel,
    onTryArClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onBuyNowClick: () -> Unit,
    onShowLoginSheet: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ui by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AuraColors.Background)
            .statusBarsPadding()
    ) {
        if (ui.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Memuat...",
                    color = AuraColors.Primary.copy(alpha = 0.6f)
                )
            }
        } else if (ui.product == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Produk tidak ditemukan.",
                        color = AuraColors.Primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GlassmorphismCard(onClick = onBack) {
                        Text(
                            text = "Kembali",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = AuraColors.Primary
                        )
                    }
                }
            }
        } else {
            val product = ui.product!!
            var loadFailed by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                ) {
                    if (product.imageUrl.isBlank() || loadFailed) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(AuraColors.Secondary.copy(alpha = 0.30f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = product.category.take(1).uppercase(),
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraColors.Primary
                            )
                        }
                    } else {
                        val context = LocalContext.current
                        val imageRequest = remember(product.imageUrl) {
                            val imageUri = when {
                                product.imageUrl.startsWith("asset://") ->
                                    Uri.parse("file:///android_asset/" + product.imageUrl.substringAfter("asset://"))
                                !product.imageUrl.startsWith("http://") && !product.imageUrl.startsWith("https://") && !product.imageUrl.startsWith("file://") ->
                                    Uri.parse("file:///android_asset/" + product.imageUrl)
                                else ->
                                    product.imageUrl
                            }
                            ImageRequest.Builder(context)
                                .data(imageUri)
                                .crossfade(true)
                                .build()
                        }

                        AsyncImage(
                            model = imageRequest,
                            contentDescription = product.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            onError = { loadFailed = true }
                        )
                    }

                    GlassmorphismCard(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .align(Alignment.TopStart),
                        shape = CircleShape,
                        onClick = onBack
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = AuraColors.Primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.category.uppercase(),
                            fontSize = 12.sp,
                            color = AuraColors.Primary.copy(alpha = 0.6f),
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(AuraShapes.Pill)
                                .background(Color.White)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AuraColors.Accent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = product.rating.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AuraColors.Primary
                                )
                                if (product.totalReviews > 0) {
                                    Text(
                                        text = " (${product.totalReviews})",
                                        fontSize = 12.sp,
                                        color = AuraColors.Primary.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = product.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraColors.Primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = formatRupiah(product.price),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AuraColors.Accent
                        )
                        if (product.originalPrice != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formatRupiah(product.originalPrice),
                                fontSize = 14.sp,
                                color = AuraColors.Primary.copy(alpha = 0.5f),
                                style = TextStyle(textDecoration = TextDecoration.LineThrough)
                            )
                        }
                    }

                    if (product.sold > 0 || product.location.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        val soldText = if (product.sold > 0) "${product.sold} terjual" else ""
                        val sep = if (product.sold > 0 && product.location.isNotBlank()) " • " else ""
                        Text(
                            text = "$soldText$sep${product.location}",
                            fontSize = 12.sp,
                            color = AuraColors.Primary.copy(alpha = 0.55f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Temukan ${product.title} dari kategori ${product.category} yang dirancang untuk kenyamanan dan estetika modern. ${if (product.isArSupported) "Coba langsung di ruanganmu melalui fitur AR untuk memastikan ukuran dan warnanya cocok." else "Produk ini tersedia dalam berbagai varian."}",
                        fontSize = 14.sp,
                        color = AuraColors.Primary.copy(alpha = 0.75f),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AuraColors.Primary.copy(alpha = 0.10f))
                    )

                    if (product.variants.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Pilihan Varian",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AuraColors.Primary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${product.variants.size} Pilihan",
                                fontSize = 12.sp,
                                color = AuraColors.Primary.copy(alpha = 0.50f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        val selectedIndex = remember { mutableIntStateOf(0) }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(product.variants) { index, variant ->
                                val isSelected = index == selectedIndex.intValue
                                Box(
                                    modifier = Modifier
                                        .clip(AuraShapes.Pill)
                                        .border(
                                            1.5.dp,
                                            if (isSelected) AuraColors.Primary else AuraColors.Primary.copy(alpha = 0.20f),
                                            AuraShapes.Pill
                                        )
                                        .clickable { selectedIndex.intValue = index }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(14.dp)
                                                .border(1.dp, AuraColors.Primary.copy(alpha = 0.25f), CircleShape)
                                                .background(variantColor(variant), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = variant,
                                            fontSize = 13.sp,
                                            color = AuraColors.Primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (product.specs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Rincian Ukuran",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AuraColors.Primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(product.specs) { (label, value) ->
                                GlassmorphismCard(
                                    modifier = Modifier.width(110.dp),
                                    shape = AuraShapes.Medium,
                                    fillColor = AuraColors.GlassFillHeavy
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            color = AuraColors.Primary.copy(alpha = 0.50f)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = value,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AuraColors.Primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }

            ProductDetailBottomBar(
                isGuest = ui.isGuest,
                onTryArClick = onTryArClick,
                onAddToCartClick = {
                    if (ui.isGuest) onShowLoginSheet() else onAddToCartClick()
                },
                onBuyNowClick = {
                    if (ui.isGuest) onShowLoginSheet() else onBuyNowClick()
                },
                onShowLoginBottomSheet = onShowLoginSheet,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
