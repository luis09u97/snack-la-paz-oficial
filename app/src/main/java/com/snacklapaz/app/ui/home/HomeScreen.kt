package com.snacklapaz.app.ui.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.layout.ContentScale
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
            Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
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
                            reviewCount = product.reviewCount,
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(136.dp)
            .background(OrangePrimary)
    ) {
        Surface(
            shape = CircleShape,
            color = White.copy(alpha = 0.18f),
            border = BorderStroke(1.dp, White.copy(alpha = 0.18f)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 20.dp, end = 18.dp)
                .size(52.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notificações",
                    tint = White,
                    modifier = Modifier.size(27.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 82.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_full),
                contentDescription = "Snack La Paz",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
            )
            Text(
                text = "Comidas e bebidas bolivianas",
                color = White.copy(alpha = 0.94f),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PromoBanner() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, OrangeLight.copy(alpha = 0.9f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .height(154.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(142.dp)
                    .clip(CircleShape)
                    .background(OrangeSoft)
            )
            Surface(
                shape = CircleShape,
                color = OrangePrimary,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 34.dp)
                    .size(74.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.DeliveryDining,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(39.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = OrangeLight,
                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.18f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeliveryDining,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Entrega rápida",
                                color = OrangePrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    }
                }
                Text(
                    text = "Peça hoje sem sair de casa",
                    color = GrayDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = "Salteñas, broaster, mocochinche e doces bolivianos preparados pelo Snack La Paz.",
                    color = GrayDark,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth(0.68f)
                        .padding(top = 6.dp)
                )
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = CreamBackground,
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeliveryDining,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Cardápio preparado para hoje",
                            color = GrayMedium,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
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
                reviewCount = product.reviewCount,
                isFavorite = product.isFavorite,
                onFavoriteClick = { onFavoriteToggle(product.id) },
                onAddToCartClick = { onAddToCart(product) },
                onClick = { onProductClick(product) },
                modifier = Modifier.width(160.dp)
            )
        }
    }
}
