package com.snacklapaz.app.ui.home

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

class HomeViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    var products by mutableStateOf<List<Product>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val remoteCategories = repository.getCategories()
                val remoteProducts = repository.getProducts()
                categories = remoteCategories.ifEmpty { sampleCategories }
                products = remoteProducts.ifEmpty { allSampleProducts }
            } catch (e: Exception) {
                errorMessage = "Não foi possível carregar os produtos. Tente novamente."
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
