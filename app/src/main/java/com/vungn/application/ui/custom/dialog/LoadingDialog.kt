package com.example.ardrawsketch.ui.custom.dialog

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.vungn.application.databinding.DialogLoadingBinding

/**
 * Custom loading dialog.
 * @author Nguyễn Ngọc Vũ
 */
class LoadingDialog : FrameLayout {
    private lateinit var binding: DialogLoadingBinding

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        binding = DialogLoadingBinding.inflate(LayoutInflater.from(context), this, true)
    }
}