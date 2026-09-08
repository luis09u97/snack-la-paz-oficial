package com.snacklapaz.app.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.snacklapaz.app.ui.home.model.Product
import com.snacklapaz.app.ui.home.model.galleryImages
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayLight
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangeLight
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.White
import kotlin.math.absoluteValue

@Composable
fun ProductDetailsDialog(
    product: Product,
    suggestions: List<Product>,
    onDismiss: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onSuggestionClick: (Product) -> Unit,
    onSuggestionAddClick: (Product) -> Unit
) {
    val context = LocalContext.current
    val images = product.galleryImages()
    SnackImagePreloader(models = images + suggestions.map { it.imageUrl })

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = CreamBackground,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 104.dp)
                ) {
                    ProductHero(
                        product = product,
                        images = images,
                        onDismiss = onDismiss,
                        onFavoriteClick = onFavoriteClick,
                        onShareClick = {
                            shareProduct(
                                context = context,
                                product = product
                            )
                        }
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = GrayDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Bs ${"%.2f".format(product.price)}",
                                style = MaterialTheme.typography.titleLarge,
                                color = OrangePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Avaliação",
                                tint = OrangePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = " ${product.rating}  •  Produto boliviano",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrayMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        DetailSection(
                            icon = Icons.Filled.Info,
                            title = "Descrição",
                            body = product.description.ifBlank {
                                "Produto tradicional boliviano preparado pelo Snack La Paz."
                            }
                        )

                        DetailSection(
                            icon = Icons.Filled.Restaurant,
                            title = "Ingredientes",
                            body = product.ingredients.ifBlank { "Ingredientes em atualização." }
                        )

                        DetailSection(
                            icon = Icons.Filled.ShoppingCart,
                            title = "Sugestão",
                            body = "Combine com uma bebida gelada ou uma porção extra para deixar o pedido mais completo."
                        )

                        if (suggestions.isNotEmpty()) {
                            Text(
                                text = "Complete seu pedido",
                                style = MaterialTheme.typography.titleMedium,
                                color = GrayDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp, bottom = 10.dp)
                            )
                            LazyRow(
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                            ) {
                                items(suggestions, key = { it.id }) { suggestion ->
                                    ProductCard(
                                        imageUrl = suggestion.imageUrl,
                                        name = suggestion.name,
                                        price = "Bs ${"%.2f".format(suggestion.price)}",
                                        rating = suggestion.rating,
                                        isFavorite = suggestion.isFavorite,
                                        onFavoriteClick = { },
                                        onAddToCartClick = { onSuggestionAddClick(suggestion) },
                                        onClick = { onSuggestionClick(suggestion) },
                                        modifier = Modifier.size(width = 162.dp, height = 236.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Surface(
                    color = White,
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    SnackPrimaryButton(
                        text = "Adicionar ao carrinho",
                        onClick = onAddToCartClick,
                        modifier = Modifier
                            .padding(PaddingValues(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 18.dp))
                            .navigationBarsPadding()
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductHero(
    product: Product,
    images: List<String>,
    onDismiss: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.05f)
                .background(GrayLight)
        ) { page ->
            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                .absoluteValue

            SnackAsyncImage(
                model = images[page],
                contentDescription = "${product.name} imagem ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val scale = 1f - (pageOffset.coerceIn(0f, 1f) * 0.08f)
                        scaleX = scale
                        scaleY = scale
                        alpha = 1f - (pageOffset.coerceIn(0f, 1f) * 0.18f)
                    }
            )
        }

        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(7.dp)
            ) {
                repeat(images.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(width = if (selected) 22.dp else 7.dp, height = 7.dp)
                            .clip(CircleShape)
                            .background(if (selected) OrangePrimary else White.copy(alpha = 0.78f))
                            .alpha(if (selected) 1f else 0.88f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(14.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)
        ) {
            HeroActionButton(
                icon = if (product.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (product.isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                onClick = onFavoriteClick
            )
            HeroActionButton(
                icon = Icons.Filled.Share,
                contentDescription = "Compartilhar produto",
                onClick = onShareClick
            )
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .statusBarsPadding()
                .padding(14.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(White.copy(alpha = 0.92f))
                .size(42.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Fechar",
                tint = GrayDark
            )
        }
    }
}

@Composable
private fun HeroActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(CircleShape)
            .background(White.copy(alpha = 0.92f))
            .size(42.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = OrangePrimary
        )
    }
}

private fun shareProduct(
    context: android.content.Context,
    product: Product
) {
    val shareText = """
        Olha que delícia do Snack La Paz: ${product.name}
        ${product.description}
        
        Preço: Bs ${"%.2f".format(product.price)}
        Peça comida boliviana pelo Snack La Paz.
    """.trimIndent()

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Snack La Paz - ${product.name}")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    context.startActivity(
        Intent.createChooser(sendIntent, "Compartilhar produto")
    )
}

@Composable
private fun DetailSection(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Surface(
                shape = CircleShape,
                color = OrangeLight,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = OrangePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = GrayDark,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
