package com.snacklapaz.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.DeliveryDining
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import com.snacklapaz.app.ui.cart.CartViewModel
import com.snacklapaz.app.ui.components.EmptyState
import com.snacklapaz.app.ui.components.ProductCard
import com.snacklapaz.app.ui.components.ProductDetailsDialog
import com.snacklapaz.app.ui.components.ProductCardSkeleton
import com.snacklapaz.app.ui.components.SnackImagePreloader
import com.snacklapaz.app.ui.components.SnackTextField
import com.snacklapaz.app.ui.home.model.Category
import com.snacklapaz.app.ui.home.model.Product
import com.snacklapaz.app.ui.home.model.galleryImages
import com.snacklapaz.app.ui.home.model.recommendationsFor
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangeLight
import com.snacklapaz.app.ui.theme.OrangeDeep
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.OrangeSoft
import com.snacklapaz.app.ui.theme.White
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.ui.res.painterResource
import com.snacklapaz.app.R

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
        val currentProduct = products.firstOrNull { it.id == product.id } ?: product
        ProductDetailsDialog(
            product = currentProduct,
            suggestions = products.recommendationsFor(currentProduct),
            onDismiss = { selectedProduct = null },
            onFavoriteClick = { onFavoriteToggle(currentProduct.id) },
            onAddToCartClick = {
                onAddToCart(currentProduct)
                selectedProduct = null
            },
            onSuggestionClick = { selectedProduct = it },
            onSuggestionAddClick = onAddToCart
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
    SnackImagePreloader(models = products.flatMap { it.galleryImages() })

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
            .background(
                Brush.horizontalGradient(
                    colors = listOf(OrangeDeep, OrangePrimary)
                )
            )
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = White.copy(alpha = 0.16f),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_icon),
                        contentDescription = "Snack La Paz",
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = "Snack La Paz",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "Comidas e bebidas bolivianas",
                    color = White.copy(alpha = 0.88f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
        shape = RoundedCornerShape(16.dp),
        color = White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(128.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(138.dp)
                    .clip(CircleShape)
                    .background(OrangeSoft)
            )
            Column {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.DeliveryDining,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Entrega rápida",
                            color = OrangePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Text(
                        text = "Peça salteñas, broaster, mocochinche e doces bolivianos sem sair de casa.",
                        color = GrayDark,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth(0.78f)
                            .padding(top = 8.dp)
                    )
                    Text(
                        text = "Cardápio preparado para hoje",
                        color = GrayMedium,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
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
