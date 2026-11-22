package com.example.e_commercevr.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.e_commercevr.data.repository.ProductRepository
import com.example.e_commercevr.ui.components.ProductItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = true,
    val product: ProductItem? = null,
    val isGuest: Boolean = true
)

class ProductDetailViewModel(
    application: Application,
    private val productId: String
) : AndroidViewModel(application) {

    private val repository = ProductRepository(application)
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.products.collect { list ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        product = list.find { p -> p.id == productId }
                    )
                }
            }
        }
    }

    companion object {
        fun factory(app: Application, id: String) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ProductDetailViewModel(app, id) as T
        }
    }
}
