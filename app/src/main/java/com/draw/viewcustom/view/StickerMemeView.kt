package com.draw.viewcustom.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView

class StickerMemeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : BaseStickerView(context, attrs) {

    private lateinit var imageView: ImageView

    init {
        setupView()
    }

    private fun setupView() {
        gravity = Gravity.CENTER
        imageView = AppCompatImageView(context).apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    addRule(CENTER_IN_PARENT, TRUE)
                }
        }
        addView(imageView)
    }

    // Đặt hình ảnh cho Meme
    fun setImageResource(resId: Int) {
        imageView.setImageResource(resId)
    }

}
