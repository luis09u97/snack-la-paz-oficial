package com.snacklapaz.app.ui.orders.model

enum class OrderStatus(val label: String) {
    RECEIVED("Pedido recebido"),
    CONFIRMED("Confirmado"),
    PREPARING("Em preparação"),
    READY("Pronto"),
    OUT_FOR_DELIVERY("Saiu para entrega"),
    DELIVERED("Entregue")
}