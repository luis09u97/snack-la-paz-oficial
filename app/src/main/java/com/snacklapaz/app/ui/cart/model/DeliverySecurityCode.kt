package com.snacklapaz.app.ui.cart.model

object DeliverySecurityCode {
    fun fromOrderNumber(orderNumber: String): String {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val seedText = orderNumber.ifBlank { "SNACKLAPAZ" }
        var seed = seedText.fold(0x45D9F3B) { acc, char ->
            ((acc * 31) xor char.code) and 0x7FFFFFFF
        }

        return buildString {
            repeat(6) {
                seed = ((seed * 1103515245) + 12345) and 0x7FFFFFFF
                append(alphabet[seed % alphabet.length])
            }
        }
    }
}
