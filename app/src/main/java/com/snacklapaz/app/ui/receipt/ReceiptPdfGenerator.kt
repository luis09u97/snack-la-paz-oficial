package com.snacklapaz.app.ui.receipt

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.snacklapaz.app.ui.cart.model.OrderSummary
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Gera um PDF simples (uma página) com os dados do recibo.
 */
object ReceiptPdfGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 34f
    private const val ORANGE = 0xFFEF5707.toInt()
    private const val CREAM = 0xFFFFF8F3.toInt()
    private const val ORANGE_LIGHT = 0xFFFFE0C7.toInt()
    private const val GREEN_LIGHT = 0xFFE6F4EA.toInt()
    private const val GREEN = 0xFF2E7D32.toInt()
    private const val DARK = 0xFF4A4A4A.toInt()
    private const val MEDIUM = 0xFF777777.toInt()
    private const val BORDER = 0xFFE0E0E0.toInt()

    fun generate(context: Context, order: OrderSummary): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = paint(ORANGE, 30f, bold = true)
        val subtitlePaint = paint(MEDIUM, 14f)
        val sectionPaint = paint(DARK, 16f, bold = true)
        val labelPaint = paint(MEDIUM, 12f, bold = true)
        val valuePaint = paint(DARK, 14f)
        val itemPaint = paint(DARK, 15f, bold = true)
        val smallPaint = paint(MEDIUM, 11f)
        val totalLabelPaint = paint(DARK, 21f, bold = true)
        val totalValuePaint = paint(ORANGE, 24f, bold = true)
        val codePaint = paint(ORANGE, 32f, bold = true).apply { letterSpacing = 0.08f }
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BORDER
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
        }

        var y = MARGIN

        fun roundedRect(top: Float, height: Float, color: Int, stroke: Boolean = false) {
            fillPaint.color = color
            val rect = RectF(MARGIN, top, PAGE_WIDTH - MARGIN, top + height)
            canvas.drawRoundRect(rect, 18f, 18f, fillPaint)
            if (stroke) canvas.drawRoundRect(rect, 18f, 18f, strokePaint)
        }

        roundedRect(y, 86f, CREAM, stroke = true)
        canvas.drawText("Snack La Paz", MARGIN + 20f, y + 34f, titlePaint)
        canvas.drawText("Comprovante de pedido", MARGIN + 20f, y + 58f, subtitlePaint)
        canvas.drawText("Pedido nº ${order.orderNumber}", PAGE_WIDTH - MARGIN - 124f, y + 34f, valuePaint)
        y += 102f

        val sdf = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR"))

        roundedRect(y, 68f, GREEN_LIGHT)
        canvas.drawText("Pedido confirmado", MARGIN + 18f, y + 27f, paint(GREEN, 17f, bold = true))
        canvas.drawText(sdf.format(Date(order.dateTimeMillis)), MARGIN + 18f, y + 50f, valuePaint)
        y += 84f

        roundedRect(y, 92f, ORANGE_LIGHT)
        canvas.drawText("Código de segurança da entrega", MARGIN + 18f, y + 27f, sectionPaint)
        canvas.drawText(order.deliverySecurityCode, MARGIN + 18f, y + 64f, codePaint)
        canvas.drawText("Mostre somente quando receber o pedido.", MARGIN + 210f, y + 56f, smallPaint)
        y += 110f

        roundedRect(y, 128f, CREAM, stroke = true)
        canvas.drawText("Dados da entrega", MARGIN + 18f, y + 28f, sectionPaint)
        y += 50f
        y = infoLine(canvas, "Cliente", order.address.fullName, y, labelPaint, valuePaint)
        y = infoLine(canvas, "Telefone", order.address.phone, y, labelPaint, valuePaint)
        y = infoLine(canvas, "Pagamento", order.paymentMethod, y, labelPaint, valuePaint)
        y = infoLine(canvas, "Endereço", order.address.formatted(), y, labelPaint, valuePaint)
        y += 22f

        val itemsHeight = 54f + (order.items.size * 30f)
        roundedRect(y, itemsHeight, CREAM, stroke = true)
        canvas.drawText("Itens do pedido", MARGIN + 18f, y + 28f, sectionPaint)
        y += 54f

        order.items.forEach { item ->
            val lineTotal = "Bs %.2f".format(item.unitPrice * item.quantity)
            canvas.drawText("${item.quantity}x ${item.name}", MARGIN + 18f, y, itemPaint)
            canvas.drawText(lineTotal, PAGE_WIDTH - MARGIN - 92f, y, valuePaint)
            y += 30f
        }

        y += 18f

        roundedRect(y, 126f, ORANGE_LIGHT)
        y += 32f
        y = valueLine(canvas, "Subtotal", order.subtotal, y, valuePaint, valuePaint)
        y = valueLine(canvas, "Entrega", order.deliveryFee, y, valuePaint, valuePaint)
        if (order.discount > 0) y = valueLine(canvas, "Desconto", -order.discount, y, valuePaint, valuePaint)
        y += 8f
        canvas.drawLine(MARGIN + 18f, y, PAGE_WIDTH - MARGIN - 18f, y, Paint().apply {
            color = ORANGE
            alpha = 70
            strokeWidth = 1.4f
        })
        y += 30f
        canvas.drawText("Total", MARGIN + 18f, y, totalLabelPaint)
        canvas.drawText("Bs %.2f".format(order.total), PAGE_WIDTH - MARGIN - 118f, y, totalValuePaint)

        canvas.drawText(
            "Este recibo é apenas um comprovante do pedido e não uma nota fiscal.",
            MARGIN,
            PAGE_HEIGHT - MARGIN,
            smallPaint
        )

        document.finishPage(page)

        val folder = File(context.getExternalFilesDir(null), "recibos").apply { mkdirs() }
        val file = File(folder, "recibo_${order.orderNumber}.pdf")
        FileOutputStream(file).use { out -> document.writeTo(out) }
        document.close()

        return file
    }

    private fun paint(color: Int, size: Float, bold: Boolean = false): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            textSize = size
            isFakeBoldText = bold
        }
    }

    private fun infoLine(
        canvas: Canvas,
        label: String,
        value: String,
        y: Float,
        labelPaint: Paint,
        valuePaint: Paint
    ): Float {
        canvas.drawText("$label:", MARGIN + 18f, y, labelPaint)
        canvas.drawText(value.take(68), MARGIN + 104f, y, valuePaint)
        return y + 22f
    }

    private fun valueLine(
        canvas: Canvas,
        label: String,
        value: Double,
        y: Float,
        labelPaint: Paint,
        valuePaint: Paint
    ): Float {
        canvas.drawText(label, MARGIN + 18f, y, labelPaint)
        canvas.drawText("Bs %.2f".format(value), PAGE_WIDTH - MARGIN - 92f, y, valuePaint)
        return y + 22f
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
