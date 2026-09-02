package com.snacklapaz.app.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snacklapaz.app.ui.cart.CartViewModel
import com.snacklapaz.app.ui.components.EmptyState
import com.snacklapaz.app.ui.components.ProductCard
import com.snacklapaz.app.ui.components.ProductDetailsDialog
import com.snacklapaz.app.ui.components.ProductCardSkeleton
import com.snacklapaz.app.ui.components.SnackTextField
import com.snacklapaz.app.ui.home.model.Category
import com.snacklapaz.app.ui.home.model.Product
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.White

@Composable
fun SearchScreen(
    cartViewModel: CartViewModel,
    searchViewModel: SearchViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    selectedProduct?.let { product ->
        ProductDetailsDialog(
            product = product,
            onDismiss = { selectedProduct = null },
            onAddToCartClick = {
                cartViewModel.addToCart(product)
                selectedProduct = null
            }
        )
    }

    val filtered by remember(query, selectedCategoryId, searchViewModel.products) {
        derivedStateOf {
            searchViewModel.products.filter { product ->
                val matchesQuery = query.isBlank() ||
                        product.name.contains(query, ignoreCase = true)
                val matchesCategory = selectedCategoryId == null ||
                        product.categoryId == selectedCategoryId
                matchesQuery && matchesCategory
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        Text(
            text = "Buscar",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            color = GrayDark,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp)
        )

        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
            SnackTextField(
                value = query,
                onValueChange = { query = it },
                label = "Buscar salteñas, api, anticuchos...",
                leadingIcon = Icons.Filled.Search
            )
        }

        Spacer_12dp()

        if (searchViewModel.categories.isNotEmpty()) {
            CategoryFilterRow(
                categories = searchViewModel.categories,
                selectedCategoryId = selectedCategoryId,
                onCategoryClick = { categoryId ->
                    selectedCategoryId = if (selectedCategoryId == categoryId) null else categoryId
                }
            )
        }

        Spacer_12dp()

        when {
            searchViewModel.isLoading -> SearchLoadingState()
            searchViewModel.errorMessage != null -> EmptyState(
                icon = Icons.Filled.ErrorOutline,
                title = "Não foi possível carregar",
                description = searchViewModel.errorMessage.orEmpty(),
                actionLabel = "Tentar novamente",
                onActionClick = { searchViewModel.loadData() }
            )
            filtered.isEmpty() -> EmptyState(
                icon = Icons.Filled.SearchOff,
                title = "Nenhum produto encontrado",
                description = "Tente buscar por outro nome ou escolher outra categoria."
            )
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filtered, key = { it.id }) { product ->
                        ProductCard(
                            imageUrl = product.imageUrl,
                            name = product.name,
                            price = "Bs ${"%.2f".format(product.price)}",
                            rating = product.rating,
                            isFavorite = product.isFavorite,
                            onFavoriteClick = { searchViewModel.toggleFavorite(product.id) },
                            onAddToCartClick = { cartViewModel.addToCart(product) },
                            onClick = { selectedProduct = product }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchLoadingState() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(6) {
            ProductCardSkeleton()
        }
    }
}

@Composable
private fun Spacer_12dp() {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(vertical = 6.dp))
}

@Composable
private fun CategoryFilterRow(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategoryClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category.id == selectedCategoryId
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) OrangePrimary else White,
                shadowElevation = if (isSelected) 0.dp else 1.dp,
                modifier = Modifier.clickable { onCategoryClick(category.id) }
            ) {
                Text(
                    text = category.name,
                    color = if (isSelected) White else GrayDark,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}
