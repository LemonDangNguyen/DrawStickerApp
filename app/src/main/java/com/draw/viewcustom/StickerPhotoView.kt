package com.draw.viewcustom

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import com.draw.R
import kotlin.math.atan2
import kotlin.math.hypot

class StickerPhotoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {

    private lateinit var stickerImageView: AppCompatImageView
    private lateinit var deleteButton: AppCompatImageView
    private lateinit var flipButton: AppCompatImageView
    private lateinit var transformButton: AppCompatImageView

    private var isDragging = false
    private var lastTouchX = 0f
    private var lastTouchY = 0f

    private var isTransforming = false
    private var initialDistance = 0f
    private var initialRotation = 0f


    init {
        initStickerView()
    }

    private fun initStickerView() {
        // Initialize ImageView
        stickerImageView = AppCompatImageView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            setOnTouchListener { _, event -> handleTouch(event) }
        }
        addView(stickerImageView)

        // Initialize buttons
        deleteButton = AppCompatImageView(context).apply {
            setImageResource(R.drawable.ic_sticker_delete)
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            setOnClickListener { removeSticker() }
        }
        addView(deleteButton)

        flipButton = AppCompatImageView(context).apply {
            setImageResource(R.drawable.ic_sticker_flip)
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            setOnClickListener { flipSticker() }
        }
        addView(flipButton)

        transformButton = AppCompatImageView(context).apply {
            setImageResource(R.drawable.ic_sticker_resize)
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            setOnTouchListener { _, event -> handleTransform(event) }
        }
        addView(transformButton)
    }

    fun setImage(bitmap: Bitmap) {
        stickerImageView.setImageBitmap(bitmap)
        updateControlButtonPositions()
    }

    private fun updateControlButtonPositions() {
        val imageWidth = stickerImageView.width
        val imageHeight = stickerImageView.height

        // Đặt vị trí cho các nút điều khiển
        deleteButton.x = (imageWidth - deleteButton.width).toFloat()
        deleteButton.y = 0f

        flipButton.x = (imageWidth / 2 - flipButton.width / 2).toFloat()
        flipButton.y = 0f

        transformButton.x = (imageWidth - transformButton.width).toFloat()
        transformButton.y = (imageHeight - transformButton.height).toFloat()
    }


    private fun flipSticker() {
        stickerImageView.scaleX *= -1

    }

    private fun removeSticker() {
        this.visibility = View.GONE
    }

    private fun handleTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.rawX
                lastTouchY = event.rawY
                isDragging = true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    val deltaX = event.rawX - lastTouchX
                    val deltaY = event.rawY - lastTouchY

                    // Move the sticker image
                    stickerImageView.translationX += deltaX
                    stickerImageView.translationY += deltaY

                    // Move the buttons with the sticker image
                    deleteButton.translationX += deltaX
                    deleteButton.translationY += deltaY

                    flipButton.translationX += deltaX
                    flipButton.translationY += deltaY

                    transformButton.translationX += deltaX
                    transformButton.translationY += deltaY

                    // Update last touch position
                    lastTouchX = event.rawX
                    lastTouchY = event.rawY
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
            }
        }
        return true
    }
    private fun handleTransform(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.rawX
                lastTouchY = event.rawY
                initialDistance = getDistance(event)
                initialRotation = getAngle(event.rawX - stickerImageView.x, event.rawY - stickerImageView.y)
                isTransforming = true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isTransforming) {
                    val newDistance = getDistance(event)

                    // Kiểm tra khoảng cách ban đầu
                    if (initialDistance > 0 && newDistance > 0) {
                        val scaleFactor = newDistance / initialDistance

                        // Giới hạn tỷ lệ phóng to
                        if (scaleFactor > 0.5f && scaleFactor < 2f) {
                            stickerImageView.scaleX = scaleFactor
                            stickerImageView.scaleY = scaleFactor
                            updateControlButtonPositions()
                        }
                    }

                    // Tính góc mới để xoay
                    val newRotation = getAngle(event.rawX - stickerImageView.x, event.rawY - stickerImageView.y)
                    val rotationDelta = newRotation - initialRotation
                    stickerImageView.rotation += rotationDelta
                    initialRotation = newRotation
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isTransforming = false
            }
        }
        return true
    }


    // Hàm tính khoảng cách giữa hai ngón tay
    private fun getDistance(event: MotionEvent): Float {
        return if (event.pointerCount == 2) {
            val dx = event.getX(0) - event.getX(1)
            val dy = event.getY(0) - event.getY(1)
            hypot(dx, dy)
        } else {
            0f
        }
    }

    // Hàm tính góc giữa hai điểm
    private fun getAngle(x: Float, y: Float): Float {
        return Math.toDegrees(atan2(y, x).toDouble()).toFloat()
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        updateControlButtonPositions()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateControlButtonPositions()
    }
}
