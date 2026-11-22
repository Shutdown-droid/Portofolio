package com.example.e_commercevr.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.e_commercevr.ui.theme.AuraColors
import com.example.e_commercevr.ui.theme.AuraShapes
import java.text.NumberFormat
import java.util.Locale

data class ProductItem(
    val id: String,
    val title: String,
    val category: String,
    val price: Long,
    val originalPrice: Long? = null,
    val imageUrl: String,
    val isArSupported: Boolean = true,
    val model3dUrl: String = "",
    val rating: Double = 4.8,
    val totalReviews: Int = 0,
    val sold: Int = 0,
    val location: String = "",
    val variants: List<String> = emptyList(),
    val specs: List<Pair<String, String>> = emptyList()
)

@Composable
fun ProductCard(
    product: ProductItem,
    onClick: () -> Unit,
    onQuickAdd: () -> Unit,
    modifier: Modifier = Modifier,
    isBanner: Boolean = false
) {
    val rupiahFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    var loadFailed by remember(product.imageUrl) { mutableStateOf(false) }

    if (isBanner) {
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

        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(AuraShapes.Large)
                .background(AuraColors.GlassFillHeavy)
                .border(1.dp, AuraColors.GlassStroke, AuraShapes.Large)
                .clickable(onClick = onClick)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
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
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = AuraColors.Primary
                        )
                    }
                } else {
                    AsyncImage(
                        model = imageRequest,
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        onError = { loadFailed = true }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.55f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    if (product.isArSupported) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(AuraShapes.Pill)
                                .background(AuraColors.Primary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(AuraColors.Accent)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Text(
                        text = product.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = product.rating.toString(),
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        if (product.totalReviews > 0) {
                            Text(
                                text = " (${product.totalReviews})",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.70f)
                            )
                        }
                        if (product.sold > 0) {
                            Text(
                                text = "  •  ${product.sold} terjual",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.70f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = rupiahFormat.format(product.price).replace("Rp", "Rp "),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.W800,
                            color = Color.White
                        )

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AuraColors.Accent)
                                .clickable(onClick = onQuickAdd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    } else {
        GlassmorphismCard(
            modifier = modifier
                .height(236.dp),
            shape = AuraShapes.Medium,
            fillColor = AuraColors.GlassFillElevated,
            strokeColor = AuraColors.GlassStroke,
            onClick = onClick
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.40f))
                        .border(1.dp, AuraColors.Primary.copy(alpha = 0.10f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    HybridProductImage(product = product)

                    if (product.isArSupported) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clip(AuraShapes.Pill)
                                .background(AuraColors.Primary)
                                .padding(horizontal = 7.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(AuraColors.Accent)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "AR",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = product.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AuraColors.Primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = AuraColors.Accent
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = product.rating.toString(),
                        fontSize = 11.sp,
                        color = AuraColors.Primary.copy(alpha = 0.70f)
                    )
                    if (product.totalReviews > 0) {
                        Text(
                            text = " (${product.totalReviews})",
                            fontSize = 11.sp,
                            color = AuraColors.Primary.copy(alpha = 0.45f)
                        )
                    }
                    if (product.sold > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${product.sold} terjual",
                            fontSize = 11.sp,
                            color = AuraColors.Primary.copy(alpha = 0.50f)
                        )
                    }
                }

                if (product.location.isNotBlank()) {
                    Text(
                        text = product.location,
                        fontSize = 11.sp,
                        color = AuraColors.Primary.copy(alpha = 0.45f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rupiahFormat.format(product.price).replace("Rp", "Rp "),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraColors.Accent
                    )

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.80f))
                            .border(1.dp, AuraColors.Primary.copy(alpha = 0.20f), CircleShape)
                            .clickable(onClick = onQuickAdd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = AuraColors.Primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HybridProductImage(
    product: ProductItem,
    modifier: Modifier = Modifier
) {
    var loadState by remember(product.imageUrl) { mutableIntStateOf(0) }

    val showFallback = product.imageUrl.isBlank() || loadState == 2

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (showFallback) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AuraColors.Secondary.copy(alpha = 0.30f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.category.take(1).uppercase(),
                    fontSize = 28.sp,
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
                contentScale = ContentScale.Fit,
                onSuccess = {
                    loadState = 1
                    android.util.Log.i("IMG-OK", product.title)
                },
                onError = {
                    loadState = 2
                    android.util.Log.w("IMG-FAIL", "${product.title} | ${product.imageUrl}")
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            )
        }
    }
}
