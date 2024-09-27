package com.draw.viewcustom

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.draw.R
import kotlin.math.atan2
import kotlin.math.hypot
import android.os.Handler
import android.os.Looper

class StickerMemeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {

    private lateinit var memeImageView: AppCompatImageView
    private lateinit var memeTextView: AppCompatTextView
    private lateinit var deleteButton: AppCompatImageView
    private lateinit var flipButton: AppCompatImageView
    private lateinit var transformButton: AppCompatImageView

    private var isDragging = false
    private var lastTouchX = 0f
    private var lastTouchY = 0f

    private var isTransforming = false
    private var initialDistance = 0f
    private var initialRotation = 0f

    private val handler = Handler(Looper.getMainLooper())
    private var hideButtonsRunnable: Runnable? = null

    init {
        initMemeView()
    }

    private fun initMemeView() {
        // Initialize ImageView for meme
        memeImageView = AppCompatImageView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            setOnTouchListener { _, event -> handleTouch(event) }
        }
        addView(memeImageView)

        // Initialize TextView for meme text
        memeTextView = AppCompatTextView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            setText("Your Meme Text")
            setTextSize(16f) // Adjust as needed
            setOnTouchListener { _, event -> handleTouch(event) }
        }
        addView(memeTextView)

        // Initialize buttons
        deleteButton = AppCompatImageView(context).apply {
            setImageResource(R.drawable.ic_sticker_delete)
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            setOnClickListener { removeMeme() }
        }
        addView(deleteButton)

        flipButton = AppCompatImageView(context).apply {
            setImageResource(R.drawable.ic_sticker_flip)
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            setOnClickListener { flipMeme() }
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
        memeImageView.setImageBitmap(bitmap)
        updateControlButtonPositions()
    }

    fun setText(text: String) {
        memeTextView.text = text
        updateControlButtonPositions()
    }

    private fun updateControlButtonPositions() {
        val imageWidth = memeImageView.width
        val imageHeight = memeImageView.height

        // Set positions for control buttons relative to the meme image
        deleteButton.x = memeImageView.x + imageWidth - deleteButton.width
        deleteButton.y = memeImageView.y

        flipButton.x = memeImageView.x + (imageWidth / 2) - (flipButton.width / 2)
        flipButton.y = memeImageView.y

        transformButton.x = memeImageView.x + imageWidth - transformButton.width
        transformButton.y = memeImageView.y + imageHeight - transformButton.height

        // Adjust text view position if necessary
        memeTextView.x = memeImageView.x
        memeTextView.y = memeImageView.y + imageHeight + 10 // Adjust for spacing
    }

    private fun flipMeme() {
        memeImageView.scaleX *= -1
        memeTextView.scaleX *= -1
    }

    private fun removeMeme() {
        this.visibility = View.GONE
    }

    private fun handleTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.rawX
                lastTouchY = event.rawY
                isDragging = true
                // Show control buttons at the current position of the meme
                showControlButtons()
                // Schedule hiding buttons after 2 seconds
                scheduleHideControlButtons()
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    val deltaX = event.rawX - lastTouchX
                    val deltaY = event.rawY - lastTouchY

                    // Move meme
                    memeImageView.translationX += deltaX
                    memeImageView.translationY += deltaY
                    memeTextView.translationX += deltaX
                    memeTextView.translationY += deltaY

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
                initialRotation = getAngle(event.rawX - memeImageView.x, event.rawY - memeImageView.y)
                isTransforming = true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isTransforming) {
                    val newDistance = getDistance(event)

                    // Check initial distance
                    if (initialDistance > 0 && newDistance > 0) {
                        val scaleFactor = newDistance / initialDistance

                        // Limit zoom
                        if (scaleFactor > 0.5f && scaleFactor < 2f) {
                            memeImageView.scaleX = scaleFactor
                            memeImageView.scaleY = scaleFactor
                            memeTextView.scaleX = scaleFactor
                            memeTextView.scaleY = scaleFactor
                            updateControlButtonPositions()
                        }
                    }

                    // Calculate new rotation
                    val newRotation = getAngle(event.rawX - memeImageView.x, event.rawY - memeImageView.y)
                    val rotationDelta = newRotation - initialRotation
                    memeImageView.rotation += rotationDelta
                    memeTextView.rotation += rotationDelta
                    initialRotation = newRotation
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isTransforming = false
            }
        }
        return true
    }

    private fun getDistance(event: MotionEvent): Float {
        return if (event.pointerCount == 2) {
            val dx = event.getX(0) - event.getX(1)
            val dy = event.getY(0) - event.getY(1)
            hypot(dx, dy)
        } else {
            0f
        }
    }

    private fun getAngle(x: Float, y: Float): Float {
        return Math.toDegrees(atan2(y, x).toDouble()).toFloat()
    }

    private fun showControlButtons() {
        deleteButton.visibility = View.VISIBLE
        flipButton.visibility = View.VISIBLE
        transformButton.visibility = View.VISIBLE
    }

    private fun hideControlButtons() {
        deleteButton.visibility = View.GONE
        flipButton.visibility = View.GONE
        transformButton.visibility = View.GONE
    }

    private fun scheduleHideControlButtons() {
        hideButtonsRunnable?.let { handler.removeCallbacks(it) }

        hideButtonsRunnable = Runnable {
            hideControlButtons()
        }

        handler.postDelayed(hideButtonsRunnable!!, 2000)
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
