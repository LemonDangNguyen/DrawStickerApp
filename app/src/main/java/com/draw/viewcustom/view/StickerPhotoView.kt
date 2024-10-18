package com.draw.viewcustom.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.RelativeLayout

import androidx.appcompat.widget.AppCompatImageView
import com.draw.R
import kotlin.math.atan2

class StickerPhotoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : BaseStickerView(context, attrs) {

    private lateinit var imageView: ImageView


    init {
        setupView()
    }

    private fun setupView() {
        imageView = ImageView(context).apply {
            setImageResource(R.drawable.anhtest)
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                addRule(CENTER_IN_PARENT, TRUE)}

        }
        addView(imageView)

        // Thiết lập các xử lý cho touch event (ví dụ: thay đổi kích thước, xoay)
    }

    // Cập nhật ảnh từ Bitmap
    fun setImageBitmap(bitmap: Bitmap) {
        imageView.setImageBitmap(bitmap)
    }


}
