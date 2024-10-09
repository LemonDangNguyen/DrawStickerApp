package com.draw.viewcustom

import android.view.MotionEvent

interface StickerIconEvent {
    fun onActionDown(stickerView: MotionEvent, event: MotionEvent?)
    fun onActionMove(stickerView: StickerBaseView?, event: MotionEvent?)
    fun onActionUp(stickerView: StickerBaseView?, event: MotionEvent?)
}
