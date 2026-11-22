package com.example.e_commercevr.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commercevr.data.repository.ProductRepository
import com.example.e_commercevr.ui.components.ProductItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val isGuest: Boolean = true,
    val userName: String = "Pengunjung",
    val selectedCategory: String = "Semua",
    val arFeaturedProducts: List<ProductItem> = emptyList(),
    val popularProducts: List<ProductItem> = emptyList(),
    val cartCount: Int = 0,
    val errorMessage: String? = null
)

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ProductRepository(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repository.products.collect { products ->
                val filtered = if (_uiState.value.selectedCategory == "Semua") {
                    products
                } else {
                    products.filter { it.category.equals(_uiState.value.selectedCategory, ignoreCase = true) }
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        arFeaturedProducts = filtered.filter { p -> p.isArSupported }.take(3),
                        popularProducts = filtered
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.fetchAndSaveProducts()
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadProducts()
    }

    fun toggleAuthMode() {
        _uiState.update {
            if (it.isGuest) {
                it.copy(isGuest = false, userName = "Aura User")
            } else {
                it.copy(isGuest = true, userName = "Pengunjung")
            }
        }
    }

    fun addToCart(product: ProductItem) {
        _uiState.update { it.copy(cartCount = it.cartCount + 1) }
    }
}
