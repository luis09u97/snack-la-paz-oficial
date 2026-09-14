package com.snacklapaz.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import coil.decode.SvgDecoder
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
    contentScale: ContentScale = ContentScale.Crop,
    showBackground: Boolean = true,
    fallbackModels: List<String> = emptyList()
) {
    val imageModels = remember(model, fallbackModels) {
        (listOfNotNull(model?.takeIf { it.isNotBlank() }) + fallbackModels)
            .filter { it.isNotBlank() }
            .distinct()
    }
    var currentModelIndex by remember(imageModels) { mutableIntStateOf(0) }

    val imageModifier = if (showBackground) {
        modifier.background(OrangeLight.copy(alpha = 0.26f))
    } else {
        modifier
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = imageModifier
    ) {
        if (imageModels.isEmpty()) {
            ImagePlaceholder(contentDescription = contentDescription)
        } else {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageModels[currentModelIndex])
                    .decoderFactory(SvgDecoder.Factory())
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                loading = { ImagePlaceholder(contentDescription = contentDescription) },
                error = {
                    if (currentModelIndex < imageModels.lastIndex) {
                        LaunchedEffect(imageModels[currentModelIndex]) {
                            currentModelIndex += 1
                        }
                    }
                    ImagePlaceholder(contentDescription = contentDescription)
                },
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
