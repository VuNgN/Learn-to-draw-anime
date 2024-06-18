package com.example.ardrawsketch.ui.custom.dialog

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import com.vungn.application.databinding.DialogWelcomeBinding
import eightbitlab.com.blurview.BlurAlgorithm
import eightbitlab.com.blurview.BlurController
import eightbitlab.com.blurview.BlurViewFacade
import eightbitlab.com.blurview.NoOpController
import eightbitlab.com.blurview.PreDrawBlurController
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur

class WelcomeDialog : FrameLayout {
    private lateinit var binding: DialogWelcomeBinding
    private var blurController: BlurController = NoOpController()

    @ColorInt
    private var overlayColor: Int = Color.parseColor("#78000000")

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        binding = DialogWelcomeBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onDraw(canvas: Canvas) {
        if (blurController.draw(canvas)) {
            super.onDraw(canvas)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        blurController.updateBlurViewSize()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        blurController.setBlurAutoUpdate(false)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isHardwareAccelerated) {
            Log.e(TAG, "BlurView can't be used in not hardware-accelerated window!");
        } else {
            blurController.setBlurAutoUpdate(true)
        }
    }

    fun setupWith(rootView: ViewGroup, algorithmLog: BlurAlgorithm): BlurViewFacade {
        blurController.destroy()
        blurController = PreDrawBlurController(this, rootView, overlayColor, algorithmLog)
        return blurController
    }

    fun setupWith(rootView: ViewGroup): BlurViewFacade {
        return setupWith(rootView, getBlurAlgorithm())
    }

    fun setBlurRadius(radius: Float): BlurViewFacade = blurController.setBlurRadius(radius)

    fun setOverlayColor(@ColorInt color: Int): BlurViewFacade {
        overlayColor = color
        return blurController.setOverlayColor(color)
    }

    fun setBlurAutoUpdate(enabled: Boolean): BlurViewFacade =
        blurController.setBlurAutoUpdate(enabled)

    fun setBlurEnabled(enabled: Boolean): BlurViewFacade = blurController.setBlurEnabled(enabled)

    private fun getBlurAlgorithm(): BlurAlgorithm {
        val algorithm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RenderEffectBlur()
        } else {
            RenderScriptBlur(context)
        }
        return algorithm
    }


    companion object {
        private val TAG = WelcomeDialog::class.simpleName
    }
}