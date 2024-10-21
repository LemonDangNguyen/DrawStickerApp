package com.draw.viewcustom.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
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

        // Gọi updateBorderSize() để viền luôn vừa với kích thước của text view
        post {
            updateBorderSize()  // Cập nhật kích thước viền sau khi layout đã xong
        }
    }

    private fun setupTextView() {
        stickerTextView = TextView(context).apply {
            text = "Sticker Text"
            textSize = 24f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            setBackgroundColor(Color.TRANSPARENT)
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    addRule(CENTER_IN_PARENT, TRUE)
                }
        }
        addView(stickerTextView)
    }

    fun updateText(newText: String) {
        stickerTextView.text = newText
        updateBorderSize()  // Cập nhật viền sau khi thay đổi nội dung văn bản
    }

    fun setTextSize(newSize: Float) {
        stickerTextView.textSize = newSize
        updateBorderSize()  // Cập nhật viền sau khi thay đổi kích thước chữ
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

    // Cập nhật kích thước viền sao cho vừa với text view
    fun updateBorderSize() {
        // Lấy kích thước của TextView chứa văn bản
        val textViewWidth = stickerTextView.width
        val textViewHeight = stickerTextView.height

        // Đặt khoảng cách từ Sticker đến viền (padding)
        val padding = 35f

        // Cập nhật kích thước của borderView với khoảng cách padding
        if (textViewWidth > 0 && textViewHeight > 0) {
            borderView.layoutParams = LayoutParams(
                (textViewWidth + 2 * padding).toInt(),
                (textViewHeight + 2 * padding).toInt()
            ).apply {
                addRule(CENTER_IN_PARENT, TRUE)  // Đảm bảo viền nằm ở trung tâm Sticker
            }
            borderView.requestLayout()
            updateButtonPositions()  // Cập nhật các nút điều khiển
        }
    }
}
