package com.draw.viewcustom.presenter

import android.graphics.Bitmap
import com.draw.activity.DrawActivity
import com.draw.viewcustom.view.StickerMemeView
import com.draw.viewcustom.view.StickerPhotoView
import com.draw.viewcustom.view.StickerTextView
import com.draw.viewcustom.model.ActionType
import com.draw.viewcustom.model.Sticker
import com.draw.viewcustom.model.StickerAction
import com.draw.viewcustom.model.StickerHistoryModel
import com.draw.viewcustom.model.StickerMeme
import com.draw.viewcustom.model.StickerPhoto
import com.draw.viewcustom.model.StickerText

class StickerPresenterImpl(
    private val view: DrawActivity,
    private val model: StickerHistoryModel
) : StickerPresenter {

    // Thêm Sticker Text
    override fun addStickerText(text: String, textSize: Float, textColor: Int, textFont: String, x: Float, y: Float) {
        val stickerView = StickerTextView(view.getContext())
        stickerView.updateText(text)
        stickerView.setTextSize(textSize)
        stickerView.setTextColor(textColor)
        stickerView.setFont(textFont)

        val stickerText = StickerText(
            view = stickerView,
            x = x,
            y = y,
            rotation = 0f,
            text = text,
            textSize = textSize,
            textColor = textColor,
            textFont = textFont
        )

        model.addAction(StickerAction(stickerView, Pair(x, y), ActionType.ADD))
        view.showSticker(stickerText)
    }

    // Thêm Sticker Photo
    override fun addStickerPhoto(photo: Bitmap, x: Float, y: Float) {
        val stickerView = StickerPhotoView(view.getContext())
        stickerView.setImageBitmap(photo)

        val stickerPhoto = StickerPhoto(
            view = stickerView,
            x = x,
            y = y,
            rotation = 0f,
            bitmap = photo,
            scaleX = 1f,
            scaleY = 1f
        )

        model.addAction(StickerAction(stickerView, Pair(x, y), ActionType.ADD))
        view.showSticker(stickerPhoto)
    }

    // Thêm Sticker Meme
    override fun addStickerMeme(resId: Int, x: Float, y: Float) {
        val stickerView = StickerMemeView(view.getContext())
        stickerView.setImageResource(resId)

        val stickerMeme = StickerMeme(
            view = stickerView,
            x = x,
            y = y,
            rotation = 0f,
            resId = resId,
            flip = false
        )

        model.addAction(StickerAction(stickerView, Pair(x, y), ActionType.ADD))
        view.showSticker(stickerMeme)
    }

    // Xóa Sticker
    override fun removeSticker(sticker: Sticker) {
        // Xóa sticker khỏi View và thêm hành động vào lịch sử
        view.removeSticker(sticker)
        model.addAction(StickerAction(sticker.view, Pair(sticker.x, sticker.y), ActionType.REMOVE))
    }

    // Thực hiện hành động undo
    override fun undo() {
        val action = model.undo()
        action?.let {
            when (it.actionType) {
                ActionType.ADD -> view.removeSticker(Sticker(it.sticker, it.position.first, it.position.second, 0f)) // Xóa sticker
                ActionType.REMOVE -> view.showSticker(Sticker(it.sticker, it.position.first, it.position.second, 0f)) // Hiển thị lại sticker
            }
        }
    }

    // Thực hiện hành động redo
    override fun redo() {
        val action = model.redo()
        action?.let {
            when (it.actionType) {
                ActionType.ADD -> view.showSticker(Sticker(it.sticker, it.position.first, it.position.second, 0f)) // Hiển thị lại sticker
                ActionType.REMOVE -> view.removeSticker(Sticker(it.sticker, it.position.first, it.position.second, 0f)) // Xóa sticker
            }
        }
    }

    // Xóa toàn bộ sticker
    override fun clearAllStickers() {
        view.clearAllStickers()
        model.clearHistory()
    }
}
