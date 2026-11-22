package com.example.e_commercevr.navigation

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.e_commercevr.ar.ARActivity
import com.example.e_commercevr.ui.components.ProductItem
import com.example.e_commercevr.ui.screens.ARViewScreen
import com.example.e_commercevr.ui.screens.HomeScreen
import com.example.e_commercevr.ui.screens.ProductDetailScreen
import com.example.e_commercevr.ui.screens.ProfileScreen
import com.example.e_commercevr.ui.screens.auth.LoginBottomSheet
import com.example.e_commercevr.ui.theme.AuraColors
import com.example.e_commercevr.ui.viewmodel.HomeViewModel
import com.example.e_commercevr.ui.viewmodel.ProductDetailViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object ARView : Screen("ar_view/{productId}") {
        fun createRoute(productId: String) = "ar_view/$productId"
    }
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object Transactions : Screen("transactions")
    object Chat : Screen("chat")
}

@Composable
fun AuraNavGraph(
    navController: NavHostController = rememberNavController()
) {
    var showLoginSheet by remember { mutableStateOf(false) }
    val viewModel: HomeViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val context = LocalContext.current
            HomeScreen(
                viewModel = viewModel,
                onNavigateToDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToAR = { productId ->
                    val intent = Intent(context, ARActivity::class.java).apply {
                        putExtra("modelName", "sofa.glb")
                        putExtra("productName", "Sofa Scandinavian Minimalis")
                        putExtra("productPrice", 4250000L)
                    }
                    context.startActivity(intent)
                },
                onOpenLoginSheet = {
                    showLoginSheet = true
                },
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(
            route = Screen.ARView.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: "dummy_id"

            val dummyProduct = ProductItem(
                id = productId,
                title = "Kacamata Minimalis AR",
                category = "Kacamata",
                price = 450000,
                imageUrl = "https://picsum.photos/200",
                model3dUrl = "glasses_model.glb"
            )

            ARViewScreen(
                product = dummyProduct,
                onClose = { navController.popBackStack() },
                onAddToCart = {
                    showLoginSheet = true
                }
            )
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val context = LocalContext.current
            val app = context.applicationContext as android.app.Application
            val vm: ProductDetailViewModel = viewModel(
                factory = ProductDetailViewModel.factory(app, productId)
            )

            ProductDetailScreen(
                productId = productId,
                viewModel = vm,
                onTryArClick = {
                    val product = vm.uiState.value.product
                    val intent = Intent(context, ARActivity::class.java).apply {
                        putExtra("modelName", product?.model3dUrl ?: "sofa.glb")
                        putExtra("productName", product?.title ?: "Produk AR")
                        putExtra("productPrice", product?.price ?: 0L)
                    }
                    context.startActivity(intent)
                },
                onAddToCartClick = { },
                onBuyNowClick = {
                    navController.navigate("cart")
                },
                onShowLoginSheet = {
                    showLoginSheet = true
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("transactions") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AuraColors.Background)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AuraColors.Primary,
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Transaksi",
                        color = AuraColors.Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada transaksi.",
                        color = AuraColors.Primary.copy(alpha = 0.6f)
                    )
                }
            }
        }

        composable("transaksi") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AuraColors.Background)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AuraColors.Primary,
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Transaksi",
                        color = AuraColors.Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada transaksi.",
                        color = AuraColors.Primary.copy(alpha = 0.6f)
                    )
                }
            }
        }

        composable("cart") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AuraColors.Background)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AuraColors.Primary,
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Keranjang",
                        color = AuraColors.Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Keranjangmu masih kosong.",
                        color = AuraColors.Primary.copy(alpha = 0.6f)
                    )
                }
            }
        }

        composable("chat") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AuraColors.Background)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AuraColors.Primary,
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Chat",
                        color = AuraColors.Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada percakapan.",
                        color = AuraColors.Primary.copy(alpha = 0.6f)
                    )
                }
            }
        }

        composable("profile") {
            ProfileScreen(
                isGuest = uiState.isGuest,
                userName = uiState.userName,
                userEmail = "user@aura.com",
                onLoginClick = { showLoginSheet = true },
                onLogout = {
                    viewModel.toggleAuthMode()
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() },
                onTransactionsClick = { navController.navigate("transactions") },
                onChatClick = { navController.navigate("chat") }
            )
        }
    }

    if (showLoginSheet) {
        LoginBottomSheet(
            onDismissRequest = { showLoginSheet = false },
            onLoginSuccess = { _ ->
                showLoginSheet = false
                viewModel.toggleAuthMode()
            },
            onContinueAsGuest = {
                showLoginSheet = false
            }
        )
    }
}
