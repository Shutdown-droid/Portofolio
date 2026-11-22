package com.example.e_commercevr.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commercevr.ui.components.*
import com.example.e_commercevr.ui.theme.AuraColors
import com.example.e_commercevr.ui.theme.AuraShapes
import com.example.e_commercevr.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToAR: (String) -> Unit,
    onOpenLoginSheet: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AuraColors.Background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBarPill(
                        modifier = Modifier.weight(1f),
                        onSearchClick = { },
                        onArShortcutClick = { onNavigateToAR("eyewear_1") }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AuraColors.GlassFill)
                            .border(1.dp, AuraColors.GlassStroke, CircleShape)
                            .clickable { onNavigate("chat") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chat",
                            tint = AuraColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AuraColors.GlassFill)
                            .border(1.dp, AuraColors.GlassStroke, CircleShape)
                            .clickable { onNavigate("cart") },
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
            }

            if (uiState.selectedCategory == "Semua") {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionHeader(title = "Promo Pilihan", actionText = "Lihat Semua")
                    uiState.arFeaturedProducts.firstOrNull()?.let { product ->
                        ProductCard(
                            product = product,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            onClick = { onNavigateToDetail(product.id) },
                            onQuickAdd = {
                                if (uiState.isGuest) onOpenLoginSheet() else viewModel.addToCart(product)
                            },
                            isBanner = true
                        )
                    } ?: GlassmorphismCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(120.dp),
                        shape = AuraShapes.Large,
                        fillColor = AuraColors.GlassFill,
                        strokeColor = AuraColors.GlassStroke
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada promo.",
                                fontSize = 13.sp,
                                color = AuraColors.Primary.copy(alpha = 0.6f),
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    CategoryHorizontalScroll(
                        categories = listOf("Semua", "Kacamata", "Furnitur", "Jam", "Tas", "Sepatu", "Elektronik"),
                        selectedCategory = uiState.selectedCategory,
                        onSelect = { viewModel.selectCategory(it) }
                    )
                }

                item {
                    SectionHeader(title = "Populer", actionText = "Semua")
                    if (uiState.popularProducts.isEmpty()) {
                        GlassmorphismCard(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(120.dp),
                            shape = AuraShapes.Large,
                            fillColor = AuraColors.GlassFill,
                            strokeColor = AuraColors.GlassStroke
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Belum ada produk populer.",
                                    fontSize = 13.sp,
                                    color = AuraColors.Primary.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxWidth().height(600.dp)
                        ) {
                            items(uiState.popularProducts) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onNavigateToDetail(product.id) },
                                    onQuickAdd = {
                                        if (uiState.isGuest) onOpenLoginSheet() else viewModel.addToCart(product)
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            } else {
                item {
                    CategoryHorizontalScroll(
                        categories = listOf("Semua", "Kacamata", "Furnitur", "Jam", "Tas", "Sepatu", "Elektronik"),
                        selectedCategory = uiState.selectedCategory,
                        onSelect = { viewModel.selectCategory(it) }
                    )
                }

                item {
                    val filtered = uiState.popularProducts.filter {
                        it.category.equals(uiState.selectedCategory, ignoreCase = true)
                    }
                    if (filtered.isEmpty()) {
                        GlassmorphismCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .height(140.dp),
                            shape = AuraShapes.Large,
                            fillColor = AuraColors.GlassFill,
                            strokeColor = AuraColors.GlassStroke
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Belum ada produk di kategori ${uiState.selectedCategory}.",
                                    fontSize = 13.sp,
                                    color = AuraColors.Primary.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxWidth().height(600.dp)
                        ) {
                            items(filtered) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onNavigateToDetail(product.id) },
                                    onQuickAdd = {
                                        if (uiState.isGuest) onOpenLoginSheet() else viewModel.addToCart(product)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        BottomNavigationGlass(
            currentRoute = "home",
            onNavigate = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
