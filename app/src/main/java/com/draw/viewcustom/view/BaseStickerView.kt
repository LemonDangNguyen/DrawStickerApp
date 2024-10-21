package com.draw.viewcustom.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import com.draw.R
import kotlin.math.atan2

abstract class BaseStickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {

    protected lateinit var borderView: RelativeLayout
    protected lateinit var deleteButton: AppCompatImageView
    protected lateinit var flipButton: AppCompatImageView
    protected lateinit var transformButton: AppCompatImageView
    protected lateinit var rotateButton: AppCompatImageView
    private var isResizing = false
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var initialRotation: Float = 0f
    private var midPoint = FloatArray(2)
    private var hideBorderHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private val hideBorderRunnable = Runnable { borderView.isVisible = false }

    init {
        setupView()
        alignStickerCenter()
    }

    private fun alignStickerCenter() {
        post {
            val parentWidth = (parent as View).width
            val parentHeight = (parent as View).height

            // Tính toán vị trí trung tâm cho Sticker
            val centerX = (parentWidth - this.width * this.scaleX) / 2
            val centerY = (parentHeight - this.height * this.scaleY) / 2

            // Đặt vị trí cho Sticker
            this.x = centerX
            this.y = centerY
        }
    }

    private fun setupView() {

        borderView = RelativeLayout(context).apply {
            background = createBorderDrawable()
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                addRule(CENTER_IN_PARENT, TRUE)
            }
            isVisible = false
        }
        addView(borderView)

        // Tạo các nút điều khiển (Xóa, lật, xoay, resize)
        deleteButton = AppCompatImageView(context).apply {
            setImageResource(R.drawable.ic_sticker_delete)
            layoutParams = LayoutParams(30, 30).apply {
                addRule(ALIGN_PARENT_TOP, TRUE)
                addRule(ALIGN_PARENT_END, TRUE)
            }
            setOnClickListener { removeSticker() }
        }
        borderView.addView(deleteButton)

        flipButton = AppCompatImageView(context).apply {
            setImageResource(R.drawable.ic_sticker_flip)
            layoutParams = LayoutParams(30, 30).apply {
                addRule(ALIGN_PARENT_TOP, TRUE)
                addRule(CENTER_HORIZONTAL, TRUE)
            }
            setOnClickListener { flipSticker() }
        }
        borderView.addView(flipButton)

        transformButton = AppCompatImageView(context).apply {
            setImageResource(R.drawable.ic_sticker_resize)
            layoutParams = LayoutParams(100, 100).apply {
                addRule(ALIGN_PARENT_BOTTOM, TRUE)
                addRule(ALIGN_PARENT_END, TRUE)
            }
            setOnTouchListener { _, event -> handleTransform(event) }
        }
        borderView.addView(transformButton)

        rotateButton = AppCompatImageView(context).apply {
            setImageResource(R.drawable.ic_sticker_rotate)
            layoutParams = LayoutParams(30, 30).apply {
                addRule(ALIGN_PARENT_BOTTOM, TRUE)
                addRule(ALIGN_PARENT_START, TRUE)
            }
            setOnTouchListener { _, event -> handleRotate(event) }
        }
        borderView.addView(rotateButton)

        updateButtonPositions()
    }

    private fun createBorderDrawable(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setStroke(5, Color.DKGRAY) // Độ dày và màu của viền
            setColor(Color.TRANSPARENT) // Màu nền là trong suốt
        }
    }

    // Hiển thị viền và các nút điều khiển
    private fun showBorder() {
        borderView.isVisible = true
        hideBorderHandler.removeCallbacks(hideBorderRunnable)
    }

    // Ẩn viền và các nút điều khiển sau khi ngừng tương tác
    private fun hideBorderAfterDelay() {
        hideBorderHandler.postDelayed(hideBorderRunnable, 2000)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                showBorder()
                lastX = event.rawX
                lastY = event.rawY
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.rawX - lastX
                val deltaY = event.rawY - lastY
                this.x += deltaX
                this.y += deltaY
                lastX = event.rawX
                lastY = event.rawY
            }

            MotionEvent.ACTION_UP -> {
                hideBorderAfterDelay()
            }
        }
        return true
    }

    // Xử lý việc xóa sticker
    protected open fun removeSticker() {
        this.visibility = GONE
    }

    // Xử lý việc lật sticker
    protected open fun flipSticker() {
        this.scaleX *= -1
        updateButtonPositions()
    }

    // Xử lý việc thay đổi kích thước sticker
    protected open fun handleTransform(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX
                lastY = event.rawY
                isResizing = true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isResizing) {
                    val deltaX = event.rawX - lastX
                    val deltaY = event.rawY - lastY

                    // Tính toán scale mới dựa trên di chuyển của người dùng
                    val newScaleX = this.scaleX + deltaX / 200
                    val newScaleY = this.scaleY + deltaY / 200

                    // Đảm bảo sticker không bị co quá nhỏ
                    if (newScaleX > 0.1f && newScaleY > 0.1f) {
                        this.scaleX = newScaleX
                        this.scaleY = newScaleY
                    }

                    lastX = event.rawX
                    lastY = event.rawY
                }
            }

            MotionEvent.ACTION_UP -> {
                isResizing = false
                hideBorderAfterDelay()
            }
        }
        return true
    }

    // Xử lý việc xoay sticker
    protected open fun handleRotate(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX
                lastY = event.rawY
                initialRotation = this.rotation
                calculateMidPoint() // Tính toán điểm giữa của sticker
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - midPoint[0]
                val dy = event.rawY - midPoint[1]

                // Tính toán góc xoay dựa trên tọa độ chạm
                val angle = atan2(dy.toDouble(), dx.toDouble()) * (180 / Math.PI)
                this.rotation = initialRotation + angle.toFloat()

                lastX = event.rawX
                lastY = event.rawY
            }

            MotionEvent.ACTION_UP -> {
                hideBorderAfterDelay()
            }
        }
        return true
    }
    private fun updateBorderSize() {
        // Lấy kích thước của Sticker (View con đầu tiên)
        val stickerWidth = this.childCount.takeIf { it > 0 }?.let { getChildAt(0).width } ?: 0
        val stickerHeight = this.childCount.takeIf { it > 0 }?.let { getChildAt(0).height } ?: 0

        Log.d("StickerSize", "Width: $stickerWidth, Height: $stickerHeight")

        // Đặt khoảng cách từ Sticker đến viền (padding)
        val padding = 10f

        // Cập nhật kích thước của borderView với khoảng cách padding
        borderView.layoutParams = LayoutParams(
            (stickerWidth + 2 * padding).toInt(),  // Cộng thêm padding vào cả 2 bên
            (stickerHeight + 2 * padding).toInt()  // Cộng thêm padding vào cả 2 bên
        ).apply {
            addRule(CENTER_IN_PARENT, TRUE)  // Đảm bảo viền nằm ở trung tâm Sticker
        }

        // Cập nhật lại view
        borderView.requestLayout()
        updateButtonPositions()
    }

    private fun calculateMidPoint() {
        midPoint[0] = this.x + (this.width * this.scaleX) / 2
        midPoint[1] = this.y + (this.height * this.scaleY) / 2
    }

    // Cập nhật vị trí của các nút điều khiển
     fun updateButtonPositions() {
        val buttonSize = 50
        val borderPadding = -3

        deleteButton.layoutParams = LayoutParams(buttonSize, buttonSize).apply {
            addRule(ALIGN_PARENT_TOP, TRUE)
            addRule(ALIGN_PARENT_END, TRUE)
            setMargins(borderPadding, borderPadding, borderPadding, borderPadding)
        }

        flipButton.layoutParams = LayoutParams(buttonSize, buttonSize).apply {
            addRule(ALIGN_PARENT_TOP, TRUE)
            addRule(CENTER_HORIZONTAL, TRUE)
            setMargins(0, borderPadding, 0, borderPadding)
        }

        transformButton.layoutParams = LayoutParams(buttonSize, buttonSize).apply {
            addRule(ALIGN_PARENT_BOTTOM, TRUE)
            addRule(ALIGN_PARENT_END, TRUE)
            setMargins(borderPadding, 0, borderPadding, borderPadding)
        }

        rotateButton.layoutParams = LayoutParams(buttonSize, buttonSize).apply {
            addRule(ALIGN_PARENT_BOTTOM, TRUE)
            addRule(ALIGN_PARENT_START, TRUE)
            setMargins(borderPadding, 0, borderPadding, borderPadding)
        }
    }
}
