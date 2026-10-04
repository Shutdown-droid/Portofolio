package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AiInputBar
import com.example.ui.components.CyberMeshBackground
import com.example.ui.components.FloatingBottomNav
import com.example.ui.components.HeaderSection
import com.example.ui.components.QrisSmartToast
import com.example.ui.components.ReactCodeDialog
import com.example.ui.components.ThermalReceiptDialog
import com.example.ui.screens.KasirScreen
import com.example.ui.screens.LaporanScreen
import com.example.ui.theme.WarungPosTheme
import com.example.util.SpeechManager
import com.example.viewmodel.WarungPosViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WarungPosTheme {
                WarungPosApp()
            }
        }
    }
}

@Composable
fun WarungPosApp(
    viewModel: WarungPosViewModel = viewModel()
) {
    val context = LocalContext.current
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val dailyTotal by viewModel.dailyTotal.collectAsStateWithLifecycle()
    val showQrisToast by viewModel.showQrisToast.collectAsStateWithLifecycle()
    val qrisAmount by viewModel.qrisAmount.collectAsStateWithLifecycle()
    val orderItems by viewModel.orderItems.collectAsStateWithLifecycle()
    val aiNotification by viewModel.aiNotification.collectAsStateWithLifecycle()
    val isListening by viewModel.isListening.collectAsStateWithLifecycle()
    val isPrinting by viewModel.isPrinting.collectAsStateWithLifecycle()
    val showPrintDialog by viewModel.showPrintDialog.collectAsStateWithLifecycle()
    val showReactCodeDialog by viewModel.showReactCodeDialog.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val qrisRevenue by viewModel.qrisRevenue.collectAsStateWithLifecycle()
    val cashRevenue by viewModel.cashRevenue.collectAsStateWithLifecycle()

    val currentOrderTotal = viewModel.calculateOrderTotal()

    // Speech Recognition Manager
    val speechManager = remember {
        SpeechManager(
            context = context,
            onResult = { spokenText ->
                viewModel.processVoiceCommand(spokenText)
            },
            onError = { _ ->
                viewModel.setListeningState(false)
            },
            onListeningStateChanged = { listening ->
                viewModel.setListeningState(listening)
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            speechManager.stopListening()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            speechManager.startListening()
        } else {
            // Graceful fallback to voice simulation when permission denied
            viewModel.triggerVoiceAI()
        }
    }

    // Handle system back navigation
    BackHandler(enabled = activeTab != "kasir") {
        viewModel.setActiveTab("kasir")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 2026 Vibrant Cybernetic Mesh Gradient Background
            CyberMeshBackground()

            // Main Vertical Content Flow
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
            ) {
                // Spacer to accommodate floating QRIS toast if visible
                if (showQrisToast) {
                    Spacer(modifier = Modifier.height(110.dp))
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // 2. HEADER SECTION (Dynamic storeName & dynamic total from cart items)
                HeaderSection(
                    dailyTotal = currentOrderTotal,
                    storeName = viewModel.storeName,
                    onTestQrisClick = { viewModel.triggerQrisSimulation(20000) },
                    onOpenReactCode = { viewModel.setShowReactCodeDialog(true) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. DYNAMIC CONTENT AREA
                Box(modifier = Modifier.weight(1f)) {
                    if (activeTab == "kasir") {
                        KasirScreen(
                            orderItems = orderItems,
                            orderTotal = currentOrderTotal,
                            aiNotification = aiNotification,
                            onAddShortcut = { viewModel.parseAndAddOrder(it.replace("+ ", "1 ")) },
                            onUpdateQuantity = { id, delta -> viewModel.updateItemQuantity(id, delta) },
                            onDeleteItem = { viewModel.removeOrderItem(it) },
                            onPrintReceipt = { viewModel.startPrintReceipt() },
                            onResetOrder = { viewModel.resetDefaultOrder() },
                            onDownloadPdf = { viewModel.downloadReceiptAsPdf(context) },
                            onSharePdf = { viewModel.shareReceiptAsPdf(context) }
                        )
                    } else {
                        LaporanScreen(
                            dailyTotal = dailyTotal,
                            qrisRevenue = qrisRevenue,
                            cashRevenue = cashRevenue,
                            transactions = transactions,
                            popularMenu = viewModel.popularMenu,
                            storeName = viewModel.storeName,
                            onTriggerQrisTest = { viewModel.triggerQrisSimulation(25000) },
                            onOpenReactCode = { viewModel.setShowReactCodeDialog(true) },
                            onDeleteTransaction = { viewModel.deleteTransaction(it) }
                        )
                    }
                }
            }

            // 1. QRIS SMART TOAST (ANIMATED NOTIFICATION AT TOP)
            QrisSmartToast(
                visible = showQrisToast,
                amount = qrisAmount,
                onMergeClick = { viewModel.mergeQrisPayment() },
                onNewIncomeClick = { viewModel.recordNewQrisIncome() },
                onDismiss = { viewModel.dismissQrisToast() },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 8.dp)
            )

            // Bottom Overlay Area: AI Input Bar & Floating Navigation
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 4. AI INPUT AREA (Right above floating navigation)
                if (activeTab == "kasir") {
                    AiInputBar(
                        isListening = isListening,
                        onVoiceClick = {
                            val permissionCheck = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            )
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                if (isListening) {
                                    speechManager.stopListening()
                                } else {
                                    speechManager.startListening()
                                }
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        onSaveOrder = { viewModel.parseAndAddOrder(it) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                // 5. FLOATING BOTTOM NAVIGATION
                FloatingBottomNav(
                    activeTab = activeTab,
                    onTabSelected = { viewModel.setActiveTab(it) },
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Dialogs
            if (showPrintDialog) {
                ThermalReceiptDialog(
                    items = orderItems,
                    totalAmount = currentOrderTotal,
                    storeName = viewModel.storeName,
                    isPrinting = isPrinting,
                    onDownloadPdf = { viewModel.downloadReceiptAsPdf(context) },
                    onSharePdf = { viewModel.shareReceiptAsPdf(context) },
                    onDismiss = { viewModel.dismissPrintDialog() }
                )
            }

            if (showReactCodeDialog) {
                ReactCodeDialog(
                    onDismiss = { viewModel.setShowReactCodeDialog(false) }
                )
            }
        }
    }
}

