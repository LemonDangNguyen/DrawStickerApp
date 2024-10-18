package com.draw.viewcustom.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView

class StickerTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : BaseStickerView(context, attrs) {

    private lateinit var stickerTextView: TextView
    private val textView: TextView = TextView(context)
    init {
        setupTextView()
        addView(textView)
    }

    private fun setupTextView() {
        stickerTextView = TextView(context).apply {
            text = "Sticker Text"
            textSize = 24f
            setTextColor(Color.BLACK)
            setBackgroundColor(Color.TRANSPARENT)
        }
        addView(stickerTextView)
    }

    fun updateText(newText: String) {
        stickerTextView.text = newText
    }

    fun setTextSize(newSize: Float) {
        stickerTextView.textSize = newSize
    }

    fun setTextColor(color: Int) {
        stickerTextView.setTextColor(color)
    }

    fun setFont(font: String) {
        // Cài đặt font cho text nếu cần
    }

    fun getTextView(): TextView {
        return textView
    }
}
