package com.snacklapaz.app.ui.checkout

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QrCodeGenerator {
    fun createBitmap(content: String, size: Int = 720): Bitmap {
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.MARGIN to 1
        )
        val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val dark = Color(0xFF24140C).toArgb()
        val light = Color.White.toArgb()

        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (matrix[x, y]) dark else light)
            }
        }
        return bitmap
    }
}
