package com.snacklapaz.app.ui.cart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snacklapaz.app.data.OrderRepository
import com.snacklapaz.app.ui.cart.model.CartItem
import com.snacklapaz.app.ui.cart.model.DeliveryAddress
import com.snacklapaz.app.ui.cart.model.OrderSummary
import com.snacklapaz.app.ui.home.model.Product
import kotlinx.coroutines.launch

/**
 * Guarda o estado do carrinho. Uma única instância é criada no NavGraph
 * e compartilhada entre a Home (que adiciona produtos) e a tela de
 * Carrinho (que exibe/edita), então elas sempre veem o mesmo estado.
 *
 * placeOrder() agora grava o pedido de verdade no Supabase, através do
 * OrderRepository.
 */
class CartViewModel : ViewModel() {

    private val orderRepository = OrderRepository()

    var items by mutableStateOf<List<CartItem>>(emptyList())
        private set

    var draftAddress by mutableStateOf(DeliveryAddress())
        private set

    var draftPaymentMethod by mutableStateOf("Pix")
        private set

    var draftCashAmount by mutableStateOf("")
        private set

    var addToCartAnimationKey by mutableStateOf(0)
        private set

    var isPlacingOrder by mutableStateOf(false)
        private set

    var placeOrderError by mutableStateOf<String?>(null)
        private set

    private val baseDeliveryFee = 5.0

    val subtotal: Double
        get() = items.sumOf { it.unitPrice * it.quantity }

    val deliveryFee: Double
        get() = if (items.isEmpty()) 0.0 else deliveryFeeFor(draftAddress)

    val discount: Double
        get() = 0.0 // reservado para cupons/promoções futuras

    val total: Double
        get() = subtotal + deliveryFee - discount

    fun deliveryFeeFor(address: DeliveryAddress): Double {
        if (items.isEmpty()) return 0.0
        val neighborhood = address.neighborhood.trim().lowercase()
        return when {
            neighborhood.isBlank() -> baseDeliveryFee
            neighborhood in setOf("centro", "vila", "bairro central") -> 4.0
            neighborhood.contains("zona sul") || neighborhood.contains("zona norte") -> 7.0
            neighborhood.contains("zona leste") || neighborhood.contains("zona oeste") -> 8.0
            else -> baseDeliveryFee + 2.0
        }
    }

    fun totalFor(address: DeliveryAddress): Double {
        return subtotal + deliveryFeeFor(address) - discount
    }

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
        addToCartAnimationKey += 1
    }

    fun updateDraftAddress(address: DeliveryAddress) {
        draftAddress = address
    }

    fun updateDraftPaymentMethod(method: String) {
        draftPaymentMethod = method
    }

    fun updateDraftCashAmount(amount: String) {
        draftCashAmount = amount
    }

    fun clearDraftAddress() {
        draftAddress = DeliveryAddress()
        draftPaymentMethod = "Pix"
        draftCashAmount = ""
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
     * Confirma o pedido de verdade: grava no Supabase (endereço, pedido e
     * itens) e só então limpa o carrinho. Chama onSuccess com o número do
     * pedido gerado, pra navegação.
     */
    fun placeOrder(address: DeliveryAddress, paymentMethod: String, onSuccess: (String) -> Unit) {
        placeOrderError = null
        isPlacingOrder = true
        viewModelScope.launch {
            try {
                val idPedido = orderRepository.placeOrder(
                    address = address,
                    items = items,
                    deliveryFee = deliveryFeeFor(address),
                    total = totalFor(address),
                    paymentMethod = paymentMethod
                )
                lastOrder = OrderSummary(
                    orderNumber = idPedido.toString(),
                    items = items,
                    subtotal = subtotal,
                    deliveryFee = deliveryFeeFor(address),
                    discount = discount,
                    total = totalFor(address),
                    address = address,
                    paymentMethod = paymentMethod,
                    dateTimeMillis = System.currentTimeMillis()
                )
                clearCart()
                clearDraftAddress()
                onSuccess(idPedido.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                placeOrderError = "Não foi possível confirmar o pedido. Tente de novo."
            } finally {
                isPlacingOrder = false
            }
        }
    }
}
