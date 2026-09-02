package com.snacklapaz.app.ui.cart.model

data class CartItem(
    val productId: String,
    val name: String,
    val imageUrl: String,
    val unitPrice: Double,
    val quantity: Int
)