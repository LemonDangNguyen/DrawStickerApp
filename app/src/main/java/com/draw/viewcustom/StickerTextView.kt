package com.draw.viewcustom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView

class StickerTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : StickerBaseView(context, attrs) {

    private var text: String = "Sticker"
    private lateinit var stickerTextView: TextView

    init {
        initStickerTextView()
        post { updateBorderSize() } // Cập nhật kích thước viền sau khi layout được vẽ
    }

    // Khởi tạo TextView và thêm nó vào StickerTextView
    private fun initStickerTextView() {
        stickerTextView = TextView(context).apply {
            text = this@StickerTextView.text
            textSize = 24f
            setTextColor(Color.BLACK)
            setBackgroundColor(Color.TRANSPARENT)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.CENTER
            }
        }
        addView(stickerTextView)
    }

    // Cập nhật kích thước của viền và nút điều khiển
    private fun updateBorderSize() {
        val textWidth = stickerTextView.paint.measureText(stickerTextView.text.toString())
        val textHeight = stickerTextView.paint.fontMetrics.run { bottom - top }
        val padding = 50f

        // Tính toán kích thước của viền bao quanh TextView
        val newWidth = (textWidth * stickerTextView.scaleX + padding).toInt()
        val newHeight = (textHeight * stickerTextView.scaleY + padding).toInt()

        // Đặt kích thước viền bao quanh TextView và viền sticker
        layoutParams = FrameLayout.LayoutParams(newWidth, newHeight).apply {
            gravity = android.view.Gravity.CENTER
        }

        // Gọi invalidate để vẽ lại viền và các icon điều khiển
        invalidate()
    }

    // Cập nhật văn bản của sticker
    fun updateText(newText: String) {
        text = newText
        stickerTextView.text = newText
        post { updateBorderSize() } // Cập nhật lại kích thước khi thay đổi văn bản
    }

    // Thay đổi màu văn bản
    fun setTextColor(color: Int) {
        stickerTextView.setTextColor(color)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            // Cập nhật kích thước viền và tính toán vị trí các nút điều khiển
            updateBorderSize()
        }
    }
}
