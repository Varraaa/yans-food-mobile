package com.example.yansfood.util

import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.text.NumberFormat
import java.util.Locale

/**
 * Efek sentuhan membal (Button Bounce Scale) sesuai spesifikasi Anti-AI Slop.
 * Skala mengecil ke 0.96 lalu membal ke 1.0 (durasi 120ms).
 */
fun View.applyButtonBounceEffect() {
    this.setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(100).start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start()
            }
        }
        false
    }
}

/**
 * Animasi angka bergulir (Number Count-Up) untuk Omset, Transaksi & Laba.
 */
fun TextView.animateCurrencyCountUp(targetAmount: Double, durationMs: Long = 600L) {
    val animator = ValueAnimator.ofFloat(0f, targetAmount.toFloat())
    animator.duration = durationMs
    animator.interpolator = FastOutSlowInInterpolator()
    animator.addUpdateListener { animation ->
        val animatedValue = (animation.animatedValue as Float).toDouble()
        this.text = animatedValue.toRupiahFormat()
    }
    animator.start()
}

/**
 * Konversi angka ke format Rupiah Indonesia (Rp 15.000).
 */
fun Double.toRupiahFormat(): String {
    val localeId = Locale("id", "ID")
    val formatter = NumberFormat.getCurrencyInstance(localeId)
    return formatter.format(this)
        .replace("Rp", "Rp ")
        .replace(",00", "")
}

fun Long.toRupiahFormat(): String {
    return this.toDouble().toRupiahFormat()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}
