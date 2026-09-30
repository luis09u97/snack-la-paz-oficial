package com.snacklapaz.app.ui.cart.model

data class DeliveryAddress(
    val fullName: String = "",
    val phone: String = "",
    val street: String = "",
    val number: String = "",
    val neighborhood: String = "",
    val complement: String = "",
    val cep: String = "",
    val city: String = "São Paulo",
    val state: String = "SP"
) {
    fun formatted(): String {
        val base = "$street, $number - $neighborhood, $city/$state"
        return if (complement.isNotBlank()) "$base ($complement)" else base
    }
}

/**
 * "Retrato" do pedido no momento em que foi confirmado — guardado à
 * parte porque o carrinho é limpo logo após a confirmação. É isso que
 * alimenta o Recibo e (futuramente) o Acompanhamento do pedido.
 */
data class OrderSummary(
    val orderNumber: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val discount: Double,
    val total: Double,
    val address: DeliveryAddress,
    val paymentMethod: String = "Dinheiro na entrega",
    val deliverySecurityCode: String = DeliverySecurityCode.fromOrderNumber(orderNumber),
    val dateTimeMillis: Long,
    val status: String = "RECEBIDO"
)
