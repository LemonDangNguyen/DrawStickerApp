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
                addRule(CENTER_IN_PARENT, TRUE)
            }
        }
        addView(imageView)

        // Gọi updateBorderSize() để viền luôn vừa với kích thước ảnh
        post {
            updateBorderSize()  // Cập nhật kích thước viền sau khi layout đã xong
        }
    }

    // Cập nhật ảnh từ Bitmap
    fun setImageBitmap(bitmap: Bitmap) {
        imageView.setImageBitmap(bitmap)
        updateBorderSize() // Cập nhật viền sau khi ảnh thay đổi
    }

    // Cập nhật kích thước viền sao cho vừa với ảnh
    fun updateBorderSize() {
        // Lấy kích thước của ImageView chứa ảnh
        val imageViewWidth = imageView.width
        val imageViewHeight = imageView.height

        // Đặt khoảng cách từ Sticker đến viền (padding)
        val padding = 35f

        // Cập nhật kích thước của borderView với khoảng cách padding
        if (imageViewWidth > 0 && imageViewHeight > 0) {
            borderView.layoutParams = LayoutParams(
                (imageViewWidth + 2 * padding).toInt(),
                (imageViewHeight + 2 * padding).toInt()
            ).apply {
                addRule(CENTER_IN_PARENT, TRUE)  // Đảm bảo viền nằm ở trung tâm Sticker
            }
            borderView.requestLayout()
            updateButtonPositions()  // Cập nhật các nút điều khiển
        }
    }

}
