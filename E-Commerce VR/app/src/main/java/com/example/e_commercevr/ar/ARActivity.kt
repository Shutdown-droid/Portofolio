package com.example.e_commercevr.ar

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.e_commercevr.ar.ui.ArOverlayComposeView
import com.example.e_commercevr.ui.theme.AuraColors
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.collision.Box as CollisionBox
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ARActivity : AppCompatActivity() {

    private var previewView: PreviewView? = null
    private var fragmentContainer: FrameLayout? = null
    private var arFragment: ArFragment? = null
    private var anchorNode: AnchorNode? = null
    private var transformableNode: TransformableNode? = null
    private var currentRenderable: Renderable? = null
    private var isModelReady = false

    private val modelLoader = FilamentModelLoader()
    private val screenshotManager by lazy { ArScreenshotManager(this) }

    private var isArMode = false
    private var modelPlaced by mutableStateOf(false)
    private var baseScale = 1.0f
    private var objectScale by mutableFloatStateOf(1.0f)
    private var isFlashOn by mutableStateOf(false)
    private var trackingStatusText by mutableStateOf("Menyiapkan AR...")
    private var capturedBitmapState by mutableStateOf<Bitmap?>(null)

    private var modelName = "sofa.glb"
    private var productName = "Produk AR"
    private var productPrice by mutableLongStateOf(0L)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            checkAndStartAR()
        } else {
            trackingStatusText = "Izin kamera ditolak"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modelName = intent.getStringExtra("modelName") ?: "sofa.glb"
        productName = intent.getStringExtra("productName") ?: "Produk AR"
        productPrice = intent.getLongExtra("productPrice", 0L)

        val rootLayout = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val container = FrameLayout(this).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        fragmentContainer = container
        rootLayout.addView(container)

        val pv = PreviewView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
            visibility = View.GONE
        }
        previewView = pv
        rootLayout.addView(pv)

        val composeOverlay = ComposeView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setContent {
                val isError = trackingStatusText.contains("ditolak") ||
                        trackingStatusText.contains("gagal") ||
                        trackingStatusText.contains("tidak tersedia")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .background(if (isError) AuraColors.Primary else Color.Transparent)
                ) {
                    ArOverlayComposeView(
                        trackingMode = ARTrackingMode.PLANE_DETECTION,
                        objectScale = objectScale,
                        isFlashOn = isFlashOn,
                        fps = 60f,
                        trackingStatusText = trackingStatusText,
                        capturedBitmap = capturedBitmapState,
                        productName = productName,
                        productPrice = productPrice,
                        onScaleChange = { newScale ->
                            objectScale = newScale
                            transformableNode?.let { node ->
                                try {
                                    node.scaleController.maxScale = 3.0f * baseScale
                                    node.scaleController.minScale = 0.25f * baseScale
                                    val finalS = baseScale * newScale.coerceIn(0.25f, 3f)
                                    node.worldScale = Vector3(finalS, finalS, finalS)
                                } catch (e: Exception) {
                                    println("AR Debug: Scale error: ${e.message}")
                                }
                            }
                        },
                        onToggleFlash = {
                            isFlashOn = !isFlashOn
                            if (isArMode) {
                                println("AR Debug: Flash tidak tersedia di mode ARCore")
                            }
                        },
                        onSwitchCamera = {
                            println("AR Debug: Switch camera tidak tersedia di mode ARCore")
                        },
                        onCaptureClick = {
                            try {
                                if (isArMode && arFragment?.arSceneView != null) {
                                    screenshotManager.captureSurfaceView(arFragment!!.arSceneView) { bitmap ->
                                        capturedBitmapState = bitmap
                                    }
                                } else if (previewView != null) {
                                    val bmp = previewView!!.bitmap
                                    if (bmp != null) {
                                        capturedBitmapState = bmp
                                    }
                                }
                            } catch (e: Exception) {
                                println("AR Debug: Screenshot error: ${e.message}")
                            }
                        },
                        onShareCapturedBitmap = { bitmap -> screenshotManager.shareBitmap(bitmap) },
                        onSaveCapturedBitmap = { bitmap -> screenshotManager.saveBitmapToGallery(bitmap) },
                        onDismissCapturedDialog = {
                            capturedBitmapState = null
                            trackingStatusText = if (modelPlaced) "Produk diletakkan - geser/pinch untuk mengatur" else "Ketuk permukaan untuk menaruh produk"
                        },
                        onCloseClick = { finish() }
                    )
                }
            }
        }
        rootLayout.addView(composeOverlay)
        setContentView(rootLayout)

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            checkAndStartAR()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun checkAndStartAR() {
        try {
            val availability = ArCoreApk.getInstance().checkAvailability(this)
            if (availability.isSupported) {
                initArCore()
            } else {
                println("AR Debug: ARCore tidak didukung (${availability.name}) -> fallback CameraX")
                initCameraX()
            }
        } catch (e: Exception) {
            println("AR Debug: ARCore check error (${e.message}) -> fallback CameraX")
            initCameraX()
        }
    }

    private fun initArCore() {
        try {
            isArMode = true
            val container = fragmentContainer ?: return
            val fragment = ArFragment()
            arFragment = fragment

            supportFragmentManager.beginTransaction()
                .replace(container.id, fragment)
                .commitAllowingStateLoss()

            fragment.setOnTapArPlaneListener { hitResult, plane, _ ->
                // AR Tap Listener Fix: Strictly validate Plane HitResult and check isModelReady flag before creating AnchorNode
                if (plane.trackingState != TrackingState.TRACKING || !plane.isPoseInPolygon(hitResult.hitPose)) {
                    println("AR Debug: INTERNAL: No point hit - Tap diabaikan (bukan permukaan Plane valid).")
                    return@setOnTapArPlaneListener
                }

                if (!isModelReady || currentRenderable == null) {
                    println("AR Debug: renderable siap=false (isModelReady = $isModelReady). Tap diabaikan.")
                    return@setOnTapArPlaneListener
                }

                placeOrMoveModel(hitResult.createAnchor())
            }

            var lightEstimateDisabled = false

            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    fragment.arSceneView?.post {
                        try {
                            fragment.arSceneView?.session?.let { session ->
                                val cfg = session.config
                                cfg.lightEstimationMode = Config.LightEstimationMode.DISABLED
                                session.configure(cfg)
                            }
                            lightEstimateDisabled = true
                        } catch (t: Throwable) {
                            println("AR Debug: gagal set light estimation: " + t.message)
                        }
                    }

                    fragment.arSceneView?.scene?.addOnUpdateListener { _ ->
                        try {
                            if (!lightEstimateDisabled) {
                                fragment.arSceneView?.session?.let { session ->
                                    val cfg = session.config
                                    cfg.lightEstimationMode = Config.LightEstimationMode.DISABLED
                                    session.configure(cfg)
                                    lightEstimateDisabled = true
                                }
                            }
                            val frame = fragment.arSceneView.arFrame
                            trackingStatusText = when {
                                modelPlaced -> "Produk diletakkan - geser/pinch untuk mengatur"
                                frame != null && frame.getUpdatedTrackables(Plane::class.java).any { it.trackingState == TrackingState.TRACKING } -> "Ketuk permukaan untuk menaruh produk"
                                else -> "Mencari permukaan... gerakkan kamera perlahan"
                            }
                        } catch (e: Exception) {
                            println("AR Debug: Scene update error: ${e.message}")
                        }
                    }
                } catch (e: Exception) {
                    println("AR Debug: addOnUpdateListener fail: ${e.message}")
                }
            }, 500)

            // Note: ModelRenderable.builder().build() must be called on Main Thread (AndroidPreconditions.checkUiThread)
            lifecycleScope.launch(Dispatchers.Main) {
                modelLoader.loadModel(
                    this@ARActivity,
                    modelName,
                    onReady = { renderable ->
                        currentRenderable = renderable
                        isModelReady = true
                        println("AR Debug: Model glb siap (isModelReady=true)")
                        transformableNode?.renderable = renderable
                        applyAutoScale()
                    },
                    onFailed = { t ->
                        println("AR Debug: Gagal glb: $t | cause=${t.cause?.message ?: "-"}")
                        modelLoader.makeFallbackCube(this@ARActivity) { cube ->
                            currentRenderable = cube
                            isModelReady = true
                            transformableNode?.renderable = cube
                            applyAutoScale()
                        }
                    }
                )
            }

            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    if (isArMode && arFragment?.arSceneView?.session == null) {
                        println("AR Debug: session null setelah 6 detik -> fallback CameraX")
                        fallbackToCameraX()
                    }
                } catch (e: Exception) {
                    println("AR Debug: Watchdog error: ${e.message}")
                }
            }, 6000)

        } catch (e: Exception) {
            println("AR Debug: initArCore error (${e.message}) -> fallback CameraX")
            fallbackToCameraX()
        }
    }

    private fun applyAutoScale() {
        val node = transformableNode ?: return
        val r = node.renderable ?: return
        val shape = r.collisionShape
        if (shape is CollisionBox) {
            val s = shape.size
            val maxDim = maxOf(s.x, s.y, s.z)
            if (maxDim > 0f && (maxDim < 0.05f || maxDim > 10f)) {
                baseScale = (1.5f / maxDim).coerceIn(0.01f, 100f)
                val finalS = baseScale * objectScale
                node.worldScale = Vector3(finalS, finalS, finalS)
                println("AR Debug: autoScale maxDim=$maxDim baseScale=$baseScale finalScale=$finalS")
            }
        }
    }

    private fun placeOrMoveModel(anchor: Anchor) {
        try {
            val fragment = arFragment ?: return
            anchorNode?.anchor?.detach()
            anchorNode?.setParent(null)

            val aNode = AnchorNode(anchor).apply {
                setParent(fragment.arSceneView.scene)
            }
            anchorNode = aNode

            val tNode = TransformableNode(fragment.transformationSystem).apply {
                setParent(aNode)
                if (currentRenderable != null) {
                    renderable = currentRenderable
                }
                select()
            }
            transformableNode = tNode
            modelPlaced = true
            println("AR Debug: Node ditempatkan, renderable siap=" + (currentRenderable != null))
            applyAutoScale()
            trackingStatusText = "Produk diletakkan - geser/pinch untuk mengatur"
        } catch (e: Exception) {
            println("AR Debug: placeOrMoveModel error: ${e.message}")
        }
    }

    private fun fallbackToCameraX() {
        try {
            isArMode = false
            arFragment?.let {
                supportFragmentManager.beginTransaction().remove(it).commitAllowingStateLoss()
            }
            arFragment = null
        } catch (e: Exception) {
            println("AR Debug: remove fragment error: ${e.message}")
        }
        initCameraX()
    }

    private fun initCameraX() {
        try {
            isArMode = false
            previewView?.visibility = View.VISIBLE
            trackingStatusText = "Kamera Aktif - $productName"
            val future = ProcessCameraProvider.getInstance(this)
            future.addListener({
                try {
                    val provider = future.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView?.surfaceProvider)
                    }
                    provider.unbindAll()
                    provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview)
                } catch (e: Exception) {
                    println("AR Debug: CameraX bind error: ${e.message}")
                    trackingStatusText = "Kamera gagal dimulai"
                }
            }, ContextCompat.getMainExecutor(this))
        } catch (e: Exception) {
            println("AR Debug: initCameraX error: ${e.message}")
            trackingStatusText = "Kamera tidak tersedia"
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            if (isArMode) {
                arFragment?.arSceneView?.resume()
            }
        } catch (e: Exception) {
            println("AR Debug: onResume fail -> fallback CameraX: ${e.message}")
            fallbackToCameraX()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            if (isArMode) {
                arFragment?.arSceneView?.pause()
            }
        } catch (e: Exception) {
            println("AR Debug: onPause fail: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            modelLoader.destroy()
        } catch (e: Exception) {
            println("AR Debug: onDestroy fail: ${e.message}")
        }
    }
}
