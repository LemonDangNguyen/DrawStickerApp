package com.draw.viewcustom.model

import android.view.View

data class StickerMeme(
    override val view: View,
    override val x: Float,
    override val y: Float,
    override val rotation: Float,
    val resId: Int,          // ID của hình ảnh meme
    val flip: Boolean        // Trạng thái lật
) : Sticker(view, x, y, rotation)
