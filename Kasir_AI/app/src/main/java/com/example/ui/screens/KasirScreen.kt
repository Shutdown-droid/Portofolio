package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OrderItem
import com.example.ui.components.DynamicReceiptCard
import com.example.ui.components.GlassCard
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.NeonCyan

@Composable
fun KasirScreen(
    orderItems: List<OrderItem>,
    orderTotal: Int,
    aiNotification: String?,
    onAddShortcut: (String) -> Unit,
    onUpdateQuantity: (Long, Int) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onPrintReceipt: () -> Unit,
    onResetOrder: () -> Unit,
    onDownloadPdf: () -> Unit = {},
    onSharePdf: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val shortcutsScroll = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(bottom = 120.dp) // Leave room for AI input bar and bottom nav
    ) {
        // AI Temporary Notification Banner
        AnimatedVisibility(
            visible = aiNotification != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clip(CircleShape)
                    .background(Color(0x3306B6D4))
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
                    .padding(horizontal = 14.dp, vertical = 7.dp)
                    .testTag("ai_notification_banner"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = CyanGlow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = aiNotification ?: "",
                        color = CyanGlow,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Quick Warung Item Shortcuts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(shortcutsScroll)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val quickItems = listOf(
                "+ Nasi Ayam",
                "+ Nasi Telor",
                "+ Es Jeruk",
                "+ Es Teh",
                "+ Gorengan",
                "+ Kopi Tubruk",
                "+ Bakso"
            )

            quickItems.forEach { shortcut ->
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.10f))
                        .border(1.dp, Color.White.copy(alpha = 0.20f), CircleShape)
                        .clickable { onAddShortcut(shortcut) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = shortcut,
                        color = Color.White.copy(alpha = 0.90f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 3. Dynamic AI Receipt Card with subtle greenish glass tint
        DynamicReceiptCard(
            items = orderItems,
            totalAmount = orderTotal,
            onUpdateQuantity = onUpdateQuantity,
            onDeleteItem = onDeleteItem,
            onPrintReceipt = onPrintReceipt,
            onDownloadPdf = onDownloadPdf,
            onSharePdf = onSharePdf
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Settlement Bar & Reset Action
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = Color.White.copy(alpha = 0.08f),
            borderColor = Color.White.copy(alpha = 0.15f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Metode Bayar:",
                        color = Color.White.copy(alpha = 0.70f),
                        fontSize = 11.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0x2806B6D4))
                            .border(1.dp, NeonCyan.copy(alpha = 0.4f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "QRIS Ready",
                            color = NeonCyan,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.10f))
                            .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Tunai",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = onResetOrder)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset Struk",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Reset",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
