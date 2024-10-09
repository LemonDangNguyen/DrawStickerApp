package com.draw.viewcustom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.draw.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener

class StickerTextDialog(
    private val stickerTextView: StickerTextView,
    // Màu mặc định cho văn bản của sticker
    private var mDefaultColor: Int = 0
) : BottomSheetDialogFragment() {

    private lateinit var mColorPreview: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout tùy chỉnh
        val dialogView = inflater.inflate(R.layout.bottom_sheet_dialog_textsticker, container, false)

        // Tìm các view trong layout
        val etInput = dialogView.findViewById<EditText>(R.id.etInput)
        mColorPreview = dialogView.findViewById(R.id.preview_selected_color)

        // Nút chọn màu
        val pickColorButton = dialogView.findViewById<TextView>(R.id.pick_color_button)
        val setColorButton = dialogView.findViewById<TextView>(R.id.set_color_button)

        // Thiết lập sự kiện chọn màu
        pickColorButton.setOnClickListener {
            showColorPicker()
        }

        // Nút thiết lập màu
        setColorButton.setOnClickListener {
            stickerTextView.setTextColor(mDefaultColor)
        }

        // Nút xác nhận chỉnh sửa văn bản
        val ivCheck = dialogView.findViewById<ImageView>(R.id.ivCheck)
        ivCheck.setOnClickListener {
            val newText = etInput.text.toString().trim()
            stickerTextView.updateText(newText) // Cập nhật văn bản cho StickerTextView
            stickerTextView.visibility = View.VISIBLE // Hiển thị StickerTextView
            dismiss() // Đóng hộp thoại sau khi cập nhật
        }

        return dialogView
    }

    // Hiển thị hộp thoại chọn màu
    private fun showColorPicker() {
        val colorPickerDialog = AmbilWarnaDialog(this.activity, mDefaultColor,
            object : OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                    // Xử lý khi hủy chọn màu (nếu cần)
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    // Cập nhật màu khi người dùng chọn OK
                    mDefaultColor = color
                    mColorPreview.setBackgroundColor(mDefaultColor) // Cập nhật preview màu
                }
            })
        colorPickerDialog.show()
    }
}
