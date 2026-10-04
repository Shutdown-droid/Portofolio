package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.example.model.OrderItem
import com.example.viewmodel.WarungPosViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReceiptManager {

    private const val TAG = "PdfReceiptManager"

    /**
     * Generates a clean, professionally formatted PDF receipt from cart data.
     */
    fun generateReceiptPdf(
        context: Context,
        storeName: String,
        items: List<OrderItem>,
        totalAmount: Int,
        transactionId: String? = null
    ): File? {
        val receiptsDir = File(context.cacheDir, "receipts").apply { mkdirs() }
        val fileName = "Struk_${System.currentTimeMillis()}.pdf"
        val targetFile = File(receiptsDir, fileName)

        // Try standard Android PdfDocument first
        try {
            val pdfDocument = PdfDocument()

            val pageWidth = 380
            val baseHeight = 360
            val itemHeight = 32
            val calculatedHeight = baseHeight + (items.size * itemHeight)
            val pageHeight = maxOf(480, calculatedHeight)

            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            // Paint configurations
            val titlePaint = Paint().apply {
                color = Color.parseColor("#0F172A")
                textSize = 17f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            val subtitlePaint = Paint().apply {
                color = Color.parseColor("#475569")
                textSize = 10f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            val boldTextPaint = Paint().apply {
                color = Color.parseColor("#0F172A")
                textSize = 11.5f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                isAntiAlias = true
            }

            val normalTextPaint = Paint().apply {
                color = Color.parseColor("#1E293B")
                textSize = 10.5f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
                isAntiAlias = true
            }

            val grayTextPaint = Paint().apply {
                color = Color.parseColor("#64748B")
                textSize = 9.5f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
                isAntiAlias = true
            }

            val totalTitlePaint = Paint().apply {
                color = Color.parseColor("#0F172A")
                textSize = 14f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                isAntiAlias = true
            }

            val totalAmountPaint = Paint().apply {
                color = Color.parseColor("#0F172A")
                textSize = 15f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                textAlign = Paint.Align.RIGHT
                isAntiAlias = true
            }

            val linePaint = Paint().apply {
                color = Color.parseColor("#CBD5E1")
                strokeWidth = 1.2f
                style = Paint.Style.STROKE
                pathEffect = DashPathEffect(floatArrayOf(6f, 4f), 0f)
                isAntiAlias = true
            }

            val solidLinePaint = Paint().apply {
                color = Color.parseColor("#94A3B8")
                strokeWidth = 1.5f
                style = Paint.Style.STROKE
                isAntiAlias = true
            }

            val centerX = pageWidth / 2f
            var currentY = 36f

            // Draw Paper Background
            canvas.drawColor(Color.WHITE)

            // 1. Store Header
            canvas.drawText(storeName.uppercase(Locale("id", "ID")), centerX, currentY, titlePaint)
            currentY += 16f
            canvas.drawText("Jl. Kuliner Kampus No. 12, Jakarta", centerX, currentY, subtitlePaint)
            currentY += 14f

            val timeString = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date()) + " WIB"
            canvas.drawText(timeString, centerX, currentY, subtitlePaint)
            currentY += 14f

            val trxId = transactionId ?: ("TRX-" + SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date()).takeLast(8))
            canvas.drawText("No: $trxId", centerX, currentY, grayTextPaint.apply { textAlign = Paint.Align.CENTER })
            grayTextPaint.textAlign = Paint.Align.LEFT
            currentY += 16f

            // 2. Dashed Divider
            canvas.drawLine(24f, currentY, pageWidth - 24f, currentY, linePaint)
            currentY += 18f

            // 3. Table Column Headers
            canvas.drawText("MENU / ITEM", 24f, currentY, boldTextPaint)
            val rightAlignHeaderPaint = Paint(boldTextPaint).apply { textAlign = Paint.Align.RIGHT }
            canvas.drawText("TOTAL", pageWidth - 24f, currentY, rightAlignHeaderPaint)
            currentY += 8f
            canvas.drawLine(24f, currentY, pageWidth - 24f, currentY, solidLinePaint)
            currentY += 18f

            // 4. Item List
            val rightAlignBoldPaint = Paint(boldTextPaint).apply { textAlign = Paint.Align.RIGHT }

            if (items.isEmpty()) {
                canvas.drawText("(Tidak ada pesanan)", centerX, currentY, grayTextPaint.apply { textAlign = Paint.Align.CENTER })
                grayTextPaint.textAlign = Paint.Align.LEFT
                currentY += 24f
            } else {
                for (item in items) {
                    val displayName = if (item.name.length > 24) item.name.take(22) + ".." else item.name
                    canvas.drawText(displayName, 24f, currentY, boldTextPaint)
                    canvas.drawText(WarungPosViewModel.formatRupiah(item.total), pageWidth - 24f, currentY, rightAlignBoldPaint)
                    currentY += 14f

                    val qtyDetail = "${item.qty} x ${WarungPosViewModel.formatRupiah(item.unitPrice)}"
                    canvas.drawText(qtyDetail, 32f, currentY, grayTextPaint)
                    currentY += 18f
                }
            }

            // 5. Divider
            canvas.drawLine(24f, currentY, pageWidth - 24f, currentY, linePaint)
            currentY += 20f

            // 6. Total Amount
            canvas.drawText("TOTAL BAYAR", 24f, currentY, totalTitlePaint)
            canvas.drawText(WarungPosViewModel.formatRupiah(totalAmount), pageWidth - 24f, currentY, totalAmountPaint)
            currentY += 24f

            // 7. Payment status seal
            val sealPaint = Paint().apply {
                color = Color.parseColor("#F1F5F9")
                style = Paint.Style.FILL
            }
            val sealBorder = Paint().apply {
                color = Color.parseColor("#059669")
                style = Paint.Style.STROKE
                strokeWidth = 1.2f
            }
            val sealText = Paint().apply {
                color = Color.parseColor("#059669")
                textSize = 10f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            canvas.drawRoundRect(24f, currentY, pageWidth - 24f, currentY + 28f, 8f, 8f, sealPaint)
            canvas.drawRoundRect(24f, currentY, pageWidth - 24f, currentY + 28f, 8f, 8f, sealBorder)
            canvas.drawText("✔ LUNAS VIA QRIS / KASIR POS", centerX, currentY + 18f, sealText)
            currentY += 44f

            // 8. Footer Message
            val footerPaint = Paint().apply {
                color = Color.parseColor("#94A3B8")
                textSize = 9.5f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.ITALIC)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("~ Terima Kasih Atas Kunjungan Anda ~", centerX, currentY, footerPaint)
            currentY += 14f
            canvas.drawText("Simpan struk ini sebagai bukti pembayaran sah", centerX, currentY, footerPaint.apply { textSize = 8f })

            pdfDocument.finishPage(page)

            FileOutputStream(targetFile).use { fos ->
                pdfDocument.writeTo(fos)
                fos.flush()
            }
            pdfDocument.close()
            return targetFile
        } catch (e: Throwable) {
            Log.w(TAG, "Standard PdfDocument threw exception, using portable raw PDF generator", e)
            return try {
                generateRawPdfReceipt(targetFile, storeName, items, totalAmount, transactionId)
                targetFile
            } catch (err: Exception) {
                Log.e(TAG, "Failed to write raw PDF receipt", err)
                null
            }
        }
    }

    /**
     * Fallback standard PDF 1.4 generator that runs in all environments (including headless unit tests).
     */
    private fun generateRawPdfReceipt(
        file: File,
        storeName: String,
        items: List<OrderItem>,
        totalAmount: Int,
        transactionId: String?
    ) {
        val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date()) + " WIB"
        val trxId = transactionId ?: ("TRX-" + SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date()).takeLast(8))

        val contentLines = mutableListOf<String>()
        contentLines.add("BT")
        contentLines.add("/F1 15 Tf")
        contentLines.add("40 540 Td (${escapePdfText(storeName.uppercase(Locale.ROOT))}) Tj")
        contentLines.add("/F1 10 Tf")
        contentLines.add("0 -18 Td (Jl. Kuliner Kampus No. 12, Jakarta) Tj")
        contentLines.add("0 -14 Td (Waktu: $dateStr) Tj")
        contentLines.add("0 -14 Td (No: $trxId) Tj")
        contentLines.add("0 -16 Td (------------------------------------------------) Tj")
        contentLines.add("0 -16 Td (MENU / ITEM                    QTY    TOTAL) Tj")
        contentLines.add("0 -14 Td (------------------------------------------------) Tj")
        for (item in items) {
            val name = item.name.take(22).padEnd(24)
            val qty = "${item.qty}x".padEnd(5)
            val total = WarungPosViewModel.formatRupiah(item.total)
            contentLines.add("0 -16 Td (${escapePdfText("$name $qty $total")}) Tj")
        }
        contentLines.add("0 -16 Td (------------------------------------------------) Tj")
        contentLines.add("/F1 13 Tf")
        contentLines.add("0 -18 Td (TOTAL BAYAR: ${escapePdfText(WarungPosViewModel.formatRupiah(totalAmount))}) Tj")
        contentLines.add("/F1 10 Tf")
        contentLines.add("0 -18 Td (STATUS: LUNAS VIA QRIS / KASIR) Tj")
        contentLines.add("0 -20 Td (~ Terima Kasih Atas Kunjungan Anda ~) Tj")
        contentLines.add("ET")

        val streamContent = contentLines.joinToString("\n")
        val streamBytes = streamContent.toByteArray(Charsets.ISO_8859_1)

        val obj1 = "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n"
        val obj2 = "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n"
        val obj3 = "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 380 600] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>\nendobj\n"
        val obj4Header = "4 0 obj\n<< /Length ${streamBytes.size} >>\nstream\n"
        val obj4Footer = "\nendstream\nendobj\n"
        val obj5 = "5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>\nendobj\n"

        FileOutputStream(file).use { fos ->
            val offsets = mutableListOf<Int>()
            var currentOffset = 0

            fun writeStr(str: String) {
                val bytes = str.toByteArray(Charsets.ISO_8859_1)
                fos.write(bytes)
                currentOffset += bytes.size
            }

            writeStr("%PDF-1.4\n")

            offsets.add(currentOffset)
            writeStr(obj1)

            offsets.add(currentOffset)
            writeStr(obj2)

            offsets.add(currentOffset)
            writeStr(obj3)

            offsets.add(currentOffset)
            writeStr(obj4Header)
            fos.write(streamBytes)
            currentOffset += streamBytes.size
            writeStr(obj4Footer)

            offsets.add(currentOffset)
            writeStr(obj5)

            val xrefStart = currentOffset
            writeStr("xref\n0 6\n0000000000 65535 f \n")
            for (off in offsets) {
                writeStr(String.format(Locale.US, "%010d 00000 n \n", off))
            }
            writeStr("trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n$xrefStart\n%%EOF\n")
            fos.flush()
        }
    }

    private fun escapePdfText(text: String): String {
        return text.replace("\\", "\\\\")
            .replace("(", "\\(")
            .replace(")", "\\)")
    }

    /**
     * Shares the generated receipt PDF using Android Sharesheet (FileProvider).
     */
    fun shareReceiptPdf(context: Context, pdfFile: File) {
        try {
            val authority = "${context.packageName}.fileprovider"
            val uri: Uri = FileProvider.getUriForFile(context, authority, pdfFile)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Struk Pembelian - ${pdfFile.name}")
                putExtra(Intent.EXTRA_TEXT, "Berikut adalah struk transaksi digital Anda.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Bagikan Struk PDF")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share receipt PDF", e)
        }
    }

    /**
     * Opens or downloads the receipt PDF for the user to view or save.
     */
    fun openOrDownloadReceiptPdf(context: Context, pdfFile: File): File? {
        try {
            val receiptsDir = File(context.filesDir, "receipts").apply { mkdirs() }
            val publicFile = File(receiptsDir, pdfFile.name)
            pdfFile.copyTo(publicFile, overwrite = true)

            try {
                val authority = "${context.packageName}.fileprovider"
                val uri: Uri = FileProvider.getUriForFile(context, authority, publicFile)

                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                val chooser = Intent.createChooser(viewIntent, "Buka atau Unduh Struk PDF")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            } catch (intentErr: Exception) {
                Log.w(TAG, "Chooser could not be started", intentErr)
            }

            return publicFile
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open or download receipt PDF", e)
            return null
        }
    }
}
