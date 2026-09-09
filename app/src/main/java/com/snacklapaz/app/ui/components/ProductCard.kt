package com.snacklapaz.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.theme.ErrorRed
import com.snacklapaz.app.ui.theme.GrayBorder
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayLight
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangeLight
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.White

/**
 * Card de produto padrão usado na Home, busca, categorias, etc.
 * Contém imagem, nome, preço, avaliação, botão de favorito (animado)
 * e botão rápido de adicionar ao carrinho.
 */
@Composable
fun ProductCard(
    imageUrl: String?,
    name: String,
    price: String,
    rating: Float,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = GrayBorder.copy(alpha = 0.65f),
    borderWidth: androidx.compose.ui.unit.Dp = 1.dp
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = White,
        shadowElevation = 1.dp,
        border = BorderStroke(borderWidth, borderColor),
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f)
            ) {
                SnackAsyncImage(
                    model = imageUrl,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.05f)
                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                        .background(GrayLight)
                )

                FavoriteToggleButton(
                    isFavorite = isFavorite,
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = GrayDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer_4dp()

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = OrangeLight.copy(alpha = 0.75f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Avaliação",
                                tint = OrangePrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = " $rating",
                                style = MaterialTheme.typography.labelSmall,
                                color = GrayDark
                            )
                        }
                    }
                }

                Spacer_4dp()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = price,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary
                    )

                    AddToCartButton(onClick = onAddToCartClick)
                }
            }
        }
    }
}

@Composable
private fun Spacer_4dp() {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun FavoriteToggleButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "favoriteScale"
    )

    Surface(
        shape = CircleShape,
        color = White,
        shadowElevation = 2.dp,
        modifier = modifier
            .size(32.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                tint = if (isFavorite) ErrorRed else GrayMedium,
                modifier = Modifier
                    .size(18.dp)
                    .scale(scale)
            )
        }
    }
}

@Composable
private fun AddToCartButton(onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = OrangePrimary,
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.AddShoppingCart,
                contentDescription = "Adicionar ao carrinho",
                tint = White,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}
