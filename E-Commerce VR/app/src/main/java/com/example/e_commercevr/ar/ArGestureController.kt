package com.example.e_commercevr.ar

import android.view.MotionEvent
import kotlin.math.atan2

/**
 * Controller untuk menangani gesture sentuhan pada layar AR.
 * - 1 Jari: Menggeser (Translate) objek
 * - 2 Jari: Memperbesar/memperkecil (Scale) dan Memutar (Rotate) objek
 */
class ArGestureController(
    private val onTranslate: (deltaX: Float, deltaY: Float) -> Unit,
    private val onScale: (scaleFactor: Float) -> Unit,
    private val onRotate: (deltaAngleRadians: Float) -> Unit
) {
    private var lastX = 0f
    private var lastY = 0f
    private var lastSpan = 0f
    private var lastAngle = 0f

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 1) {
                    // 1 Jari: Geser objek
                    onTranslate(event.x - lastX, event.y - lastY)
                    lastX = event.x
                    lastY = event.y
                } else if (event.pointerCount >= 2) {
                    // 2 Jari: Zoom dan Putar
                    val currentSpan = calculateSpan(event)
                    if (lastSpan > 0f) {
                        onScale(currentSpan / lastSpan)
                    }
                    lastSpan = currentSpan

                    val currentAngle = calculateAngle(event)
                    onRotate(currentAngle - lastAngle)
                    lastAngle = currentAngle
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                // Reset saat jari diangkat agar perhitungan berikutnya akurat
                lastSpan = 0f
                lastAngle = 0f
            }
        }
        return true
    }

    // Menghitung jarak antara dua jari (untuk Zoom/Scale)
    private fun calculateSpan(e: MotionEvent): Float {
        val dx = e.getX(0) - e.getX(1)
        val dy = e.getY(0) - e.getY(1)
        return kotlin.math.sqrt((dx * dx) + (dy * dy))
    }

    // Menghitung sudut antara dua jari (untuk Rotasi)
    private fun calculateAngle(e: MotionEvent): Float {
        return atan2((e.getY(1) - e.getY(0)).toDouble(), (e.getX(1) - e.getX(0)).toDouble()).toFloat()
    }
}