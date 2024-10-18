package com.draw.viewcustom.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
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

            // Tính toán vị trí giữa
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
    private fun createBorderDrawable(): ShapeDrawable {
        // Tạo một ShapeDrawable với RectShape để vẽ viền
        return object : ShapeDrawable(RectShape()) {
            override fun draw(canvas: Canvas) {
                // Lấy kích thước hiện tại của sticker
                val stickerWidth = this@BaseStickerView.width * this@BaseStickerView.scaleX
                val stickerHeight = this@BaseStickerView.height * this@BaseStickerView.scaleY

                // Thêm padding để tạo khoảng cách giữa sticker và viền
                val padding = 5f // Giảm padding để viền gần với sticker hơn

                // Tính toán kích thước của viền dựa trên sticker và padding
                val left = padding
                val top = padding
                val right = stickerWidth + padding
                val bottom = stickerHeight + padding

                // Cập nhật paint để vẽ viền
                paint.color = Color.DKGRAY
                paint.strokeWidth = 3f  // Giảm độ dày của viền để mỏng hơn
                paint.style = Paint.Style.STROKE

                // Vẽ viền bao quanh sticker với khoảng cách padding nhỏ
                canvas.drawRect(left, top, right, bottom, paint)
            }
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
                updateBorderSize()
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
                        updateBorderSize()
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
        // Lấy kích thước thực tế của sticker, bao gồm cả scale
        val stickerWidth = (this.width * this.scaleX).toInt()
        val stickerHeight = (this.height * this.scaleY).toInt()

        // Khoảng cách giữa viền và sticker
        val padding = 20

        // Cập nhật kích thước của borderView để nó vừa khít sticker
        borderView.layoutParams = LayoutParams(
            stickerWidth + 2 * padding, // Kích thước viền cộng với padding hai bên
            stickerHeight + 2 * padding // Kích thước viền cộng với padding trên dưới
        ).apply {
            addRule(CENTER_IN_PARENT, TRUE) // Giữ viền ở giữa sticker
        }

        borderView.requestLayout()

        // Cập nhật vị trí của các nút điều khiển (nút xóa, xoay, resize)
        updateButtonPositions()
    }
    private fun calculateMidPoint() {
        midPoint[0] = this.x + (this.width * this.scaleX) / 2
        midPoint[1] = this.y + (this.height * this.scaleY) / 2
    }
    // Cập nhật vị trí của các nút điều khiển
    private fun updateButtonPositions() {
        val buttonSize = 50
        val borderPadding = -3

        // Đặt vị trí của nút xóa (delete) ở góc trên cùng bên phải
        deleteButton.layoutParams = LayoutParams(buttonSize, buttonSize).apply {
            addRule(ALIGN_PARENT_TOP, TRUE)
            addRule(ALIGN_PARENT_END, TRUE)
            setMargins(borderPadding, borderPadding, borderPadding, borderPadding)
        }

        // Đặt vị trí của nút lật (flip) ở phía trên, giữa
        flipButton.layoutParams = LayoutParams(buttonSize, buttonSize).apply {
            addRule(ALIGN_PARENT_TOP, TRUE)
            addRule(CENTER_HORIZONTAL, TRUE)
            setMargins(0, borderPadding, 0, borderPadding)
        }

        // Đặt vị trí của nút thay đổi kích thước (transform) ở góc dưới cùng bên phải
        transformButton.layoutParams = LayoutParams(buttonSize, buttonSize).apply {
            addRule(ALIGN_PARENT_BOTTOM, TRUE)
            addRule(ALIGN_PARENT_END, TRUE)
            setMargins(borderPadding, 0, borderPadding, borderPadding)


        }

        // Đặt vị trí của nút xoay (rotate) ở góc dưới cùng bên trái
        rotateButton.layoutParams = LayoutParams(buttonSize, buttonSize).apply {
            addRule(ALIGN_PARENT_BOTTOM, TRUE)
            addRule(ALIGN_PARENT_START, TRUE)
            setMargins(borderPadding, 0, borderPadding, borderPadding)
        }
    }

}
