package com.example.e_commercevr.ar

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.PixelCopy
import android.view.SurfaceView
import androidx.core.content.FileProvider
import java.io.File

class ArScreenshotManager(private val context: Context) {

    // 1. Mengambil screenshot dari SurfaceView (AR)
    fun captureSurfaceView(surfaceView: SurfaceView, onCaptured: (Bitmap) -> Unit) {
        val bitmap = Bitmap.createBitmap(surfaceView.width, surfaceView.height, Bitmap.Config.ARGB_8888)
        PixelCopy.request(surfaceView, bitmap, { result ->
            if (result == PixelCopy.SUCCESS) {
                onCaptured(bitmap)
            }
        }, android.os.Handler(android.os.Looper.getMainLooper()))
    }

    // 2. Menyimpan gambar ke Galeri HP
    fun saveBitmapToGallery(bitmap: Bitmap): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "AURA_AR_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AURA_AR")
            }
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }
        }
        return uri
    }

    // 3. Membagikan gambar ke aplikasi lain (WhatsApp, Instagram, dll)
    fun shareBitmap(bitmap: Bitmap) {
        val file = File(context.cacheDir, "shared_ar.jpg").apply {
            outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
        }

        // PENTING: Memerlukan konfigurasi FileProvider di AndroidManifest.xml
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan Hasil AR"))
    }
}