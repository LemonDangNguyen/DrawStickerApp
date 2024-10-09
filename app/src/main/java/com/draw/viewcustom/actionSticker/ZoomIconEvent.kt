package com.draw.viewcustom.actionSticker

import android.view.MotionEvent
import com.draw.viewcustom.StickerBaseView
import com.draw.viewcustom.StickerIconEvent

class ZoomIconEvent : StickerIconEvent {
    override fun onActionDown(stickerView: MotionEvent, event: MotionEvent?) = Unit

    override fun onActionMove(stickerView: StickerBaseView?, event: MotionEvent?) {
        stickerView?.zoomCurrentSticker(event!!)
    }

    override fun onActionUp(stickerView: StickerBaseView?, event: MotionEvent?) = Unit
}
