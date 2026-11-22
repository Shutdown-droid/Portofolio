package com.example.e_commercevr.ar

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ShapeFactory

class FilamentModelLoader {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var activeRenderable: Renderable? = null

    fun loadModel(
        context: Context,
        assetName: String,
        onReady: (ModelRenderable) -> Unit,
        onFailed: (Throwable) -> Unit
    ) {
        val modelUri = when {
            assetName.startsWith("file://") || assetName.startsWith("http://") || assetName.startsWith("https://") -> Uri.parse(assetName)
            else -> Uri.parse("file:///android_asset/$assetName")
        }
        println("AR Debug: Memuat model Sceneform dari assets URI: $modelUri")
        ModelRenderable.builder()
            .setSource(context, modelUri)
            .setIsFilamentGltf(true)
            .build()
            .thenAccept { r ->
                println("AR Debug: Model glb siap: $assetName")
                activeRenderable = r
                mainHandler.post { onReady(r) }
                }
            .exceptionally { t ->
                val errorMsg = t?.message ?: "Gagal memuat $assetName"
                println("AR Debug: Gagal memuat $assetName: $errorMsg")
                println("AR Debug: STACK LENGKAP:\n" + (t?.let { android.util.Log.getStackTraceString(it) } ?: "-"))
                t?.cause?.let { println("AR Debug: CAUSE: ${it.javaClass.name}: ${it.message}") }
                val ex = if (t != null) t else Exception(errorMsg)
                mainHandler.post { onFailed(ex) }
                null
            }
    }

    fun makeFallbackCube(context: Context, onReady: (Renderable) -> Unit) {
        mainHandler.post {
            try {
                MaterialFactory.makeOpaqueWithColor(context, Color(0.92f, 0.27f, 0.37f))
                    .thenAccept { material ->
                        val cube = ShapeFactory.makeCube(
                            Vector3(0.3f, 0.3f, 0.3f),
                            Vector3(0f, 0.15f, 0f),
                            material
                        )
                        activeRenderable = cube
                        mainHandler.post { onReady(cube) }
                    }
                    .exceptionally { t ->
                        println("AR Debug: Gagal membuat kubus fallback: ${t?.message}")
                        null
                    }
            } catch (e: Exception) {
                println("AR Debug: Gagal membuat kubus fallback Exception: ${e.message}")
            }
        }
    }

    fun destroy() {
        activeRenderable = null
        println("AR Debug: FilamentModelLoader dibersihkan")
    }
}
