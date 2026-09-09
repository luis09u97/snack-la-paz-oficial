package com.snacklapaz.app.ui.components

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 28.dp)
            ) {
                ProductHero(
                    product = product,
                    images = images,
                    onDismiss = onDismiss,
                    onFavoriteClick = onFavoriteClick,
                    onShareClick = { shareProduct(context = context, product = product) }
                )

                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = GrayDark,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
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

                    Text(
                        text = "Bs ${"%.2f".format(product.price)}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    PaymentMethods()
                    DeliveryInfo()

                    HorizontalDivider(color = GrayLight, modifier = Modifier.padding(vertical = 18.dp))

                    ProductInfoSection(
                        icon = Icons.Filled.Info,
                        title = "Descrição",
                        body = product.description.ifBlank {
                            "Produto tradicional boliviano preparado pelo Snack La Paz."
                        }
                    )

                    ProductInfoSection(
                        icon = Icons.Filled.Restaurant,
                        title = "Ingredientes",
                        body = product.ingredients.ifBlank { "Ingredientes em atualização." }
                    )

                    SnackPrimaryButton(
                        text = "Adicionar ao carrinho",
                        onClick = onAddToCartClick,
                        modifier = Modifier.padding(top = 10.dp, bottom = 22.dp)
                    )

                    if (suggestions.isNotEmpty()) {
                        Text(
                            text = "Produtos que combinam",
                            style = MaterialTheme.typography.titleMedium,
                            color = GrayDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        SuggestionGrid(
                            suggestions = suggestions,
                            onSuggestionClick = onSuggestionClick,
                            onSuggestionAddClick = onSuggestionAddClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentMethods() {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Payments,
                contentDescription = null,
                tint = OrangePrimary,
                modifier = Modifier.size(17.dp)
            )
            Text(
                text = "Meios de pagamento",
                style = MaterialTheme.typography.labelLarge,
                color = OrangePrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 6.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            PaymentLogoChip(
                logoUrl = "https://cdn.simpleicons.org/pix/EF5707",
                contentDescription = "Pix"
            )
            PaymentLogoChip(
                logoUrl = "https://cdn.simpleicons.org/nubank/EF5707",
                contentDescription = "Nubank"
            )
            PaymentLogoChip(
                logoUrl = "https://cdn.simpleicons.org/mercadopago/EF5707",
                contentDescription = "Mercado Pago"
            )
            PaymentLogoChip(
                logoUrl = "https://cdn.simpleicons.org/picpay/EF5707",
                contentDescription = "PicPay"
            )
            PaymentLogoChip(
                logoUrl = "https://cdn.simpleicons.org/visa/EF5707",
                contentDescription = "Visa"
            )
        }
    }
}

@Composable
private fun PaymentLogoChip(
    logoUrl: String,
    contentDescription: String
) {
    Surface(
        shape = CircleShape,
        color = White,
        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.22f)),
        modifier = Modifier.size(38.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            SnackAsyncImage(
                model = logoUrl,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                showBackground = false,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun DeliveryInfo() {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(top = 18.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.LocalShipping,
            contentDescription = null,
            tint = OrangePrimary,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = "Entrega disponível hoje",
                color = OrangePrimary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pedido preparado pelo Snack La Paz e enviado para seu endereço.",
                color = GrayMedium,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 3.dp)
            )
            Text(
                text = "Estoque disponível",
                color = GrayDark,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun ProductInfoSection(
    icon: ImageVector,
    title: String,
    body: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = OrangeLight.copy(alpha = 0.72f),
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = OrangePrimary,
                    modifier = Modifier.size(20.dp)
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
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }
}

@Composable
private fun SuggestionGrid(
    suggestions: List<Product>,
    onSuggestionClick: (Product) -> Unit,
    onSuggestionAddClick: (Product) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        suggestions.chunked(2).forEach { rowSuggestions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowSuggestions.forEach { suggestion ->
                    ProductCard(
                        imageUrl = suggestion.imageUrl,
                        name = suggestion.name,
                        price = "Bs ${"%.2f".format(suggestion.price)}",
                        rating = suggestion.rating,
                        isFavorite = suggestion.isFavorite,
                        onFavoriteClick = { },
                        onAddToCartClick = { onSuggestionAddClick(suggestion) },
                        onClick = { onSuggestionClick(suggestion) },
                        borderColor = OrangePrimary.copy(alpha = 0.55f),
                        borderWidth = 1.4.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(324.dp)
                    )
                }
                if (rowSuggestions.size == 1) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .height(324.dp)
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
                horizontalArrangement = Arrangement.spacedBy(7.dp)
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
            horizontalArrangement = Arrangement.spacedBy(10.dp)
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
    icon: ImageVector,
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
