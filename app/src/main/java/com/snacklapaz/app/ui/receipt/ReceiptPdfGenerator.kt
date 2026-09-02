package com.snacklapaz.app.ui.receipt

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.snacklapaz.app.ui.cart.model.OrderSummary
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Gera um PDF simples (uma página) com os dados do recibo, usando
 * android.graphics.pdf.PdfDocument (nativo do Android, sem depender de
 * bibliotecas externas). Salva na pasta de documentos do próprio app.
 */
object ReceiptPdfGenerator {

    private const val PAGE_WIDTH = 595 // A4 em pontos (72dpi)
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f

    fun generate(context: Context, order: OrderSummary): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = android.graphics.Color.rgb(239, 87, 7) // laranja da marca
            textSize = 22f
            isFakeBoldText = true
        }
        val subtitlePaint = Paint().apply {
            color = android.graphics.Color.DKGRAY
            textSize = 11f
        }
        val labelPaint = Paint().apply {
            color = android.graphics.Color.DKGRAY
            textSize = 12f
        }
        val valuePaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 12f
        }
        val boldPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 13f
            isFakeBoldText = true
        }
        val totalPaint = Paint().apply {
            color = android.graphics.Color.rgb(239, 87, 7)
            textSize = 16f
            isFakeBoldText = true
        }
        val linePaint = Paint().apply {
            color = android.graphics.Color.LTGRAY
            strokeWidth = 1f
        }

        var y = MARGIN + 20f

        canvas.drawText("Snack La Paz", MARGIN, y, titlePaint)
        y += 18f
        canvas.drawText("Comprovante de pedido", MARGIN, y, subtitlePaint)
        y += 20f
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        y += 24f

        val sdf = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR"))

        fun infoLine(label: String, value: String) {
            canvas.drawText("$label: $value", MARGIN, y, labelPaint)
            y += 18f
        }

        infoLine("Pedido nº", order.orderNumber)
        infoLine("Data", sdf.format(Date(order.dateTimeMillis)))
        infoLine("Cliente", order.address.fullName)
        infoLine("Telefone", order.address.phone)
        infoLine("Endereço", order.address.formatted())
        infoLine("Pagamento", order.paymentMethod)

        y += 8f
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        y += 22f

        canvas.drawText("Itens", MARGIN, y, boldPaint)
        y += 20f

        order.items.forEach { item ->
            val lineTotal = "Bs %.2f".format(item.unitPrice * item.quantity)
            canvas.drawText("${item.quantity}x ${item.name}", MARGIN, y, valuePaint)
            canvas.drawText(lineTotal, PAGE_WIDTH - MARGIN - 60f, y, valuePaint)
            y += 18f
        }

        y += 8f
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        y += 22f

        fun valueLine(label: String, value: Double) {
            canvas.drawText(label, MARGIN, y, labelPaint)
            canvas.drawText("Bs %.2f".format(value), PAGE_WIDTH - MARGIN - 60f, y, valuePaint)
            y += 18f
        }

        valueLine("Subtotal", order.subtotal)
        valueLine("Entrega", order.deliveryFee)
        if (order.discount > 0) valueLine("Desconto", -order.discount)

        y += 6f
        canvas.drawText("Total", MARGIN, y, totalPaint)
        canvas.drawText("Bs %.2f".format(order.total), PAGE_WIDTH - MARGIN - 70f, y, totalPaint)
        y += 30f

        canvas.drawText(
            "Este recibo é apenas um comprovante do pedido e não uma nota fiscal.",
            MARGIN, y, subtitlePaint
        )

        document.finishPage(page)

        val folder = File(context.getExternalFilesDir(null), "recibos").apply { mkdirs() }
        val file = File(folder, "recibo_${order.orderNumber}.pdf")
        FileOutputStream(file).use { out -> document.writeTo(out) }
        document.close()

        return file
    }

    fun openPdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }
}