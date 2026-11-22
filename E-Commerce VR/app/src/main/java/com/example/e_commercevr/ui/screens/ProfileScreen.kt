package com.example.e_commercevr.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commercevr.ui.components.GlassmorphismCard
import com.example.e_commercevr.ui.theme.AuraColors
import com.example.e_commercevr.ui.theme.AuraShapes

@Composable
fun ProfileScreen(
    isGuest: Boolean,
    userName: String,
    userEmail: String,
    onLoginClick: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onTransactionsClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AuraColors.Background)
            .statusBarsPadding()
    ) {
        if (isGuest) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = AuraColors.Primary.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Profil Kamu",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AuraColors.Primary
                    )
                    Text(
                        text = "Masuk untuk melihat transaksi, chat, dan riwayat AR",
                        fontSize = 14.sp,
                        color = AuraColors.Primary.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                    GlassmorphismCard(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(48.dp),
                        fillColor = AuraColors.Accent,
                        shape = AuraShapes.Medium,
                        onClick = onLoginClick
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Masuk atau Daftar",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AuraColors.Background)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                GlassmorphismCard(
                    modifier = Modifier.fillMaxWidth(),
                    fillColor = AuraColors.GlassFillHeavy,
                    shape = AuraShapes.Large
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(AuraShapes.Pill)
                                .background(AuraColors.Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = userName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AuraColors.Primary
                            )
                            Text(
                                text = userEmail.ifBlank { "user@aura.com" },
                                fontSize = 12.sp,
                                color = AuraColors.Primary.copy(alpha = 0.55f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    color = AuraColors.Secondary,
                                    shape = AuraShapes.Pill
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Member",
                                color = AuraColors.Primary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                GlassmorphismCard(
                    modifier = Modifier.fillMaxWidth(),
                    fillColor = AuraColors.GlassFill,
                    shape = AuraShapes.Large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        ProfileRowMenu(
                            icon = Icons.Default.List,
                            label = "Transaksi Saya",
                            onClick = onTransactionsClick
                        )
                        ProfileRowMenu(
                            icon = Icons.Default.Email,
                            label = "Chat Penjual",
                            onClick = onChatClick
                        )
                        ProfileRowMenu(
                            icon = Icons.Default.LocationOn,
                            label = "Alamat",
                            onClick = {}
                        )
                        ProfileRowMenu(
                            icon = Icons.Default.Settings,
                            label = "Pengaturan",
                            onClick = {}
                        )
                        ProfileRowMenu(
                            icon = Icons.Default.ExitToApp,
                            label = "Keluar",
                            isAccent = true,
                            onClick = onLogout
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileRowMenu(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isAccent: Boolean = false
) {
    val tintColor = if (isAccent) AuraColors.Accent else AuraColors.Primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tintColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = tintColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = AuraColors.Primary.copy(alpha = 0.40f),
            modifier = Modifier.size(16.dp)
        )
    }
}
