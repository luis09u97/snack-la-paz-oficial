package com.snacklapaz.app.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snacklapaz.app.data.ProductRepository
import com.snacklapaz.app.ui.home.model.Category
import com.snacklapaz.app.ui.home.model.Product
import com.snacklapaz.app.ui.home.model.allSampleProducts
import com.snacklapaz.app.ui.home.model.sampleCategories
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    var categories by mutableStateOf<List<Category>>(sampleCategories)
        private set

    var products by mutableStateOf<List<Product>>(allSampleProducts)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            isLoading = products.isEmpty()
            errorMessage = null
            try {
                val remoteCategories = repository.getCategories()
                val remoteProducts = repository.getProducts()
                if (remoteCategories.isNotEmpty()) {
                    categories = remoteCategories
                }
                if (remoteProducts.isNotEmpty()) {
                    products = remoteProducts
                }
            } catch (e: Exception) {
                if (products.isEmpty()) {
                    errorMessage = "Nao foi possivel carregar a busca. Tente novamente."
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleFavorite(productId: String) {
        products = products.map {
            if (it.id == productId) it.copy(isFavorite = !it.isFavorite) else it
        }
    }
}
