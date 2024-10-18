package com.draw.viewcustom.model

import android.graphics.Bitmap
import android.view.View


data class StickerPhoto(
    override val view: View,
    override val x: Float,
    override val y: Float,
    override val rotation: Float,
    val bitmap: Bitmap,      // Ảnh dưới dạng Bitmap
    val scaleX: Float,       // Độ thu phóng ngang
    val scaleY: Float        // Độ thu phóng dọc
) : Sticker(view, x, y, rotation)
