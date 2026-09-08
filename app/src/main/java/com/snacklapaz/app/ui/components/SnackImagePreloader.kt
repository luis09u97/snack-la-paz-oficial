package com.snacklapaz.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
fun SnackImagePreloader(models: List<String>) {
    val context = LocalContext.current
    val cacheKeys = models
        .filter { it.isNotBlank() }
        .distinct()
        .joinToString("|")

    DisposableEffect(cacheKeys) {
        val requests = models
            .filter { it.isNotBlank() }
            .distinct()
            .map { model ->
                ImageRequest.Builder(context)
                    .data(model)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .build()
            }

        val disposables = requests.map { request -> context.imageLoader.enqueue(request) }
        onDispose {
            disposables.forEach { it.dispose() }
        }
    }
}
