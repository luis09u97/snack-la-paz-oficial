package com.snacklapaz.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.snacklapaz.app.ui.theme.OrangeLight
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.OrangeSoft
import com.snacklapaz.app.ui.theme.White
import androidx.compose.material.icons.filled.ErrorOutline

@Composable
fun HomeScreen(
    cartViewModel: CartViewModel,
    homeViewModel: HomeViewModel = viewModel()
) {
    var searchText by remember { mutableStateOf("") }

    when {
        homeViewModel.isLoading -> HomeLoadingState()
        homeViewModel.errorMessage != null -> HomeErrorState(
            message = homeViewModel.errorMessage!!,
            onRetry = { homeViewModel.loadData() }
        )
        else -> HomeContent(
            categories = homeViewModel.categories,
            products = homeViewModel.products,
            searchText = searchText,
            onSearchChange = { searchText = it },
            onFavoriteToggle = { id -> homeViewModel.toggleFavorite(id) },
            onAddToCart = { product -> cartViewModel.addToCart(product) }
        )
    }
}

@Composable
private fun HomeLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        repeat(4) {
            ProductCardSkeleton(modifier = Modifier.padding(bottom = 16.dp))
        }
    }
}

@Composable
private fun HomeErrorState(message: String, onRetry: () -> Unit) {
    EmptyState(
        icon = Icons.Filled.ErrorOutline,
        title = "Não foi possível carregar",
        description = message,
        actionLabel = "Tentar novamente",
        onActionClick = onRetry
    )
}

@Composable
private fun HomeContent(
    categories: List<Category>,
    products: List<Product>,
    searchText: String,
    onSearchChange: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }

    selectedProduct?.let { product ->
        ProductDetailsDialog(
            product = product,
            onDismiss = { selectedProduct = null },
            onAddToCartClick = {
                onAddToCart(product)
                selectedProduct = null
            }
        )
    }

    // Enquanto não existir uma coluna "destaque" no banco, usamos os
    // primeiros produtos cadastrados como "Destaques" e o resto como
    // "Populares" — puramente visual, não afeta o banco de dados.
    val filteredProducts = if (selectedCategoryId == null) {
        products
    } else {
        products.filter { it.categoryId == selectedCategoryId }
    }
    val featured = filteredProducts.take(7)
    val popular = filteredProducts.drop(7)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { HomeHeader() }

        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                SnackTextField(
                    value = searchText,
                    onValueChange = onSearchChange,
                    label = "Buscar salteñas, api, anticuchos...",
                    leadingIcon = Icons.Filled.Search
                )
            }
        }

        item { PromoBanner() }

        if (categories.isNotEmpty()) {
            item {
                SectionTitle(title = "Categorias")
                CategoriesRow(
                    categories = categories,
                    selectedCategoryId = selectedCategoryId,
                    onCategoryClick = { categoryId ->
                        selectedCategoryId = if (selectedCategoryId == categoryId) null else categoryId
                    }
                )
            }
        }

        if (featured.isNotEmpty()) {
            item {
                SectionTitle(title = selectedCategoryTitle(categories, selectedCategoryId) ?: "Destaques")
                FeaturedRow(
                    products = featured,
                    onFavoriteToggle = onFavoriteToggle,
                    onAddToCart = onAddToCart,
                    onProductClick = { selectedProduct = it }
                )
            }
        }

        if (popular.isNotEmpty()) {
            item { SectionTitle(title = "Populares") }

            val rows = popular.chunked(2)
            items(rows.size) { rowIndex ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rows[rowIndex].forEach { product ->
                        ProductCard(
                            imageUrl = product.imageUrl,
                            name = product.name,
                            price = "Bs ${"%.2f".format(product.price)}",
                            rating = product.rating,
                            isFavorite = product.isFavorite,
                            onFavoriteClick = { onFavoriteToggle(product.id) },
                            onAddToCartClick = { onAddToCart(product) },
                            onClick = { selectedProduct = product },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rows[rowIndex].size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        if (filteredProducts.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Filled.SearchOff,
                    title = "Cardápio vazio",
                    description = "Cadastre produtos ativos no Supabase para exibir o catálogo."
                )
            }
        }
    }
}

private fun selectedCategoryTitle(categories: List<Category>, selectedCategoryId: String?): String? {
    return categories.firstOrNull { it.id == selectedCategoryId }?.name
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(OrangePrimary)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Olá! 👋",
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "O que vamos saborear hoje?",
                color = White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }

        Surface(
            shape = CircleShape,
            color = White.copy(alpha = 0.2f),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notificações",
                    tint = White
                )
            }
        }
    }
}

@Composable
private fun PromoBanner() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = OrangeSoft,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(120.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    text = "🇧🇴 Sabores autênticos",
                    color = OrangePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Text(
                    text = "Frete grátis no seu primeiro pedido",
                    color = GrayDark,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = GrayDark,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun CategoriesRow(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategoryClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(categories) { category ->
            CategoryItem(
                category = category,
                isSelected = category.id == selectedCategoryId,
                onClick = { onCategoryClick(category.id) }
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick)
    ) {
        Surface(
            shape = CircleShape,
            color = if (isSelected) OrangePrimary else OrangeLight,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = if (isSelected) White else OrangePrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = category.name,
            fontSize = 12.sp,
            color = if (isSelected) OrangePrimary else GrayDark,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}

@Composable
private fun FeaturedRow(
    products: List<Product>,
    onFavoriteToggle: (String) -> Unit,
    onAddToCart: (Product) -> Unit,
    onProductClick: (Product) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCard(
                imageUrl = product.imageUrl,
                name = product.name,
                price = "Bs ${"%.2f".format(product.price)}",
                rating = product.rating,
                isFavorite = product.isFavorite,
                onFavoriteClick = { onFavoriteToggle(product.id) },
                onAddToCartClick = { onAddToCart(product) },
                onClick = { onProductClick(product) },
                modifier = Modifier.width(160.dp)
            )
        }
    }
}
