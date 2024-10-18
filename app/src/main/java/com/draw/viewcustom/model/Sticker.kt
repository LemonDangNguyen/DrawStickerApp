package com.draw.viewcustom.model

import android.view.View


open class Sticker(
    open val view: View,    // View của sticker
    open val x: Float,      // Tọa độ X
    open val y: Float,      // Tọa độ Y
    open val rotation: Float // Góc xoay của sticker

)
