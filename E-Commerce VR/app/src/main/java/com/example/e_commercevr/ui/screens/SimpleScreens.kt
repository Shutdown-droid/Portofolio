package com.example.e_commercevr.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commercevr.ui.theme.AuraColors

@Composable
fun ChatScreen(
    onBackClick: () -> Unit = {}
) {
    SimpleScreenScaffold(
        title = "Chat",
        emptyMessage = "Belum ada percakapan",
        onBack = onBackClick
    )
}

@Composable
fun CartScreen(
    onBackClick: () -> Unit = {}
) {
    SimpleScreenScaffold(
        title = "Keranjang",
        emptyMessage = "Keranjang masih kosong",
        onBack = onBackClick
    )
}

@Composable
fun TransactionScreen(
    onBackClick: () -> Unit = {}
) {
    SimpleScreenScaffold(
        title = "Transaksi",
        emptyMessage = "Belum ada transaksi",
        onBack = onBackClick
    )
}

@Composable
fun TransactionsScreen(
    onBackClick: () -> Unit = {}
) {
    TransactionScreen(onBackClick = onBackClick)
}

@Composable
fun EmptyState(
    pesan: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Info",
            tint = AuraColors.Primary.copy(alpha = 0.4f),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = pesan,
            fontSize = 14.sp,
            color = AuraColors.Primary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SimpleScreenScaffold(
    title: String,
    emptyMessage: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraColors.Background)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AuraColors.Primary,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onBack)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AuraColors.Primary
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(pesan = emptyMessage)
            }
        }
    }
}
