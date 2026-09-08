package com.snacklapaz.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.request.CachePolicy
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangeLight

@Composable
fun SnackAsyncImage(
    model: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.background(OrangeLight.copy(alpha = 0.26f))
    ) {
        if (model.isNullOrBlank()) {
            ImagePlaceholder(contentDescription = contentDescription)
        } else {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(model)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                loading = { ImagePlaceholder(contentDescription = contentDescription) },
                error = { ImagePlaceholder(contentDescription = contentDescription) },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ImagePlaceholder(contentDescription: String?) {
    Icon(
        imageVector = Icons.Filled.Restaurant,
        contentDescription = contentDescription,
        tint = GrayMedium.copy(alpha = 0.62f),
        modifier = Modifier.size(44.dp)
    )
}
