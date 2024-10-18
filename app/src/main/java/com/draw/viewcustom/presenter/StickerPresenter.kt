package com.draw.viewcustom.presenter

import android.graphics.Bitmap
import com.draw.viewcustom.model.Sticker

interface StickerPresenter {
    fun addStickerText(text: String, textSize: Float, textColor: Int, textFont: String, x: Float, y: Float)
    fun addStickerPhoto(photo: Bitmap, x: Float, y: Float)
    fun addStickerMeme(resId: Int, x: Float, y: Float)
    fun removeSticker(sticker: Sticker)
    fun undo()
    fun redo()
    fun clearAllStickers()
}