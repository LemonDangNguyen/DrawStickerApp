package com.draw.viewcustom.model

import android.view.View

// StickerAction.kt
data class StickerAction(
    val sticker: View,             // View của sticker
    val position: Pair<Float, Float>, // Vị trí của sticker
    val actionType: ActionType       // Loại hành động: ADD hoặc REMOVE
)

enum class ActionType {
    ADD,
    REMOVE
}
