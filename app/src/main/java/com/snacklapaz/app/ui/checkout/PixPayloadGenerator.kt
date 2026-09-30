package com.snacklapaz.app.ui.checkout

import java.text.Normalizer
import java.util.Locale

object PixPayloadGenerator {
    private const val PIX_KEY = "+5511963641616"
    private const val MERCHANT_NAME = "Luis Fernando Quispe Mamani"
    private const val MERCHANT_CITY = "Sao Paulo"

    fun create(amount: Double, txId: String): String {
        val merchantAccount = field("00", "br.gov.bcb.pix") +
            field("01", PIX_KEY) +
            field("02", "Pedido Snack La Paz")

        val additionalData = field("05", normalize(txId).take(25).ifBlank { "***" })
        val withoutCrc = field("00", "01") +
            field("26", merchantAccount) +
            field("52", "0000") +
            field("53", "986") +
            field("54", String.format(Locale.US, "%.2f", amount)) +
            field("58", "BR") +
            field("59", normalize(MERCHANT_NAME).take(25)) +
            field("60", normalize(MERCHANT_CITY).take(15)) +
            field("62", additionalData) +
            "6304"

        return withoutCrc + crc16(withoutCrc)
    }

    private fun field(id: String, value: String): String {
        return id + value.length.toString().padStart(2, '0') + value
    }

    private fun normalize(value: String): String {
        val withoutAccent = Normalizer.normalize(value, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
        return withoutAccent.uppercase(Locale.ROOT)
            .replace("[^A-Z0-9 .*+\\-/]".toRegex(), "")
            .trim()
    }

    private fun crc16(payload: String): String {
        var crc = 0xFFFF
        payload.toByteArray(Charsets.UTF_8).forEach { byte ->
            crc = crc xor ((byte.toInt() and 0xFF) shl 8)
            repeat(8) {
                crc = if ((crc and 0x8000) != 0) {
                    (crc shl 1) xor 0x1021
                } else {
                    crc shl 1
                }
                crc = crc and 0xFFFF
            }
        }
        return crc.toString(16).uppercase(Locale.ROOT).padStart(4, '0')
    }
}
