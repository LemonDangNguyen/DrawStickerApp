package com.draw.viewcustom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.draw.R
import com.draw.viewcustom.presenter.StickerPresenter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener

class StickerTextDialog(
    private val presenter: StickerPresenter, // Truyền Presenter vào
    private var mDefaultColor: Int = 0
) : BottomSheetDialogFragment() {

    private lateinit var mColorPreview: View
    private lateinit var ivCheck: ImageView // Nút xác nhận

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dialogView = inflater.inflate(R.layout.bottom_sheet_dialog_textsticker, container, false)

        val etInput = dialogView.findViewById<EditText>(R.id.etInput)
        mColorPreview = dialogView.findViewById(R.id.preview_selected_color)

        val pickColorButton = dialogView.findViewById<TextView>(R.id.pick_color_button)
        ivCheck = dialogView.findViewById(R.id.ivCheck)

        // Ẩn nút ivCheck ban đầu
        ivCheck.visibility = View.GONE

        // Hiển thị nút khi đã chọn màu
        pickColorButton.setOnClickListener {
            showColorPicker()
        }

        ivCheck.setOnClickListener {
            val newText = etInput.text.toString().trim()

            // Đảm bảo tính toán vị trí sau khi layout của parentView đã hoàn tất
            val parentView = activity?.window?.decorView?.findViewById<View>(android.R.id.content)
            parentView?.post {
                val parentWidth = parentView.width
                val parentHeight = parentView.height

                // Tính toán tọa độ giữa
                val centerX = parentWidth / 2f
                val centerY = parentHeight / 2f

                // Gọi Presenter để thêm sticker văn bản
                presenter.addStickerText(newText, 24f, mDefaultColor, "sans-serif", centerX, centerY)

                dismiss() // Đóng hộp thoại sau khi cập nhật
            }
        }

        isCancelable = false
        return dialogView

    }

    private fun showColorPicker() {
        val colorPickerDialog = AmbilWarnaDialog(this.activity, mDefaultColor,
            object : OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    mDefaultColor = color
                    mColorPreview.setBackgroundColor(mDefaultColor)

                    // Hiển thị nút ivCheck sau khi màu đã được chọn
                    ivCheck.visibility = View.VISIBLE
                }
            })
        colorPickerDialog.show()
    }
}
