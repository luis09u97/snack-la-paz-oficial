package com.snacklapaz.app.ui.cart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.snacklapaz.app.ui.cart.model.CartItem
import com.snacklapaz.app.ui.cart.model.DeliveryAddress
import com.snacklapaz.app.ui.cart.model.OrderSummary
import com.snacklapaz.app.ui.home.model.Product

/**
 * Guarda o estado do carrinho. Uma única instância é criada no NavGraph
 * e compartilhada entre a Home (que adiciona produtos) e a tela de
 * Carrinho (que exibe/edita), então elas sempre veem o mesmo estado.
 *
 * Quando integrarmos o Supabase, a lógica de persistência entra aqui,
 * sem precisar mudar as telas que já consomem esse ViewModel.
 */
class CartViewModel : ViewModel() {

    var items by mutableStateOf<List<CartItem>>(emptyList())
        private set

    // Taxa de entrega fixa por enquanto; futuramente pode vir calculada
    // a partir do endereço do usuário.
    private val flatDeliveryFee = 5.0

    val subtotal: Double
        get() = items.sumOf { it.unitPrice * it.quantity }

    val deliveryFee: Double
        get() = if (items.isEmpty()) 0.0 else flatDeliveryFee

    val discount: Double
        get() = 0.0 // reservado para cupons/promoções futuras

    val total: Double
        get() = subtotal + deliveryFee - discount

    fun addToCart(product: Product) {
        val existing = items.find { it.productId == product.id }
        items = if (existing != null) {
            items.map {
                if (it.productId == product.id) it.copy(quantity = it.quantity + 1) else it
            }
        } else {
            items + CartItem(
                productId = product.id,
                name = product.name,
                imageUrl = product.imageUrl,
                unitPrice = product.price,
                quantity = 1
            )
        }
    }

    fun increaseQuantity(productId: String) {
        items = items.map {
            if (it.productId == productId) it.copy(quantity = it.quantity + 1) else it
        }
    }

    fun decreaseQuantity(productId: String) {
        items = items.mapNotNull {
            if (it.productId == productId) {
                if (it.quantity > 1) it.copy(quantity = it.quantity - 1) else null
            } else it
        }
    }

    fun removeItem(productId: String) {
        items = items.filterNot { it.productId == productId }
    }

    fun clearCart() {
        items = emptyList()
    }

    var lastOrder by mutableStateOf<OrderSummary?>(null)
        private set

    /**
     * Confirma o pedido: guarda um retrato completo (itens, valores,
     * endereço) em [lastOrder] e só então limpa o carrinho. Retorna o
     * número do pedido gerado, pra navegação.
     */
    fun placeOrder(address: DeliveryAddress): String {
        val orderNumber = (1000..9999).random().toString()
        lastOrder = OrderSummary(
            orderNumber = orderNumber,
            items = items,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            discount = discount,
            total = total,
            address = address,
            dateTimeMillis = System.currentTimeMillis()
        )
        clearCart()
        return orderNumber
    }
}