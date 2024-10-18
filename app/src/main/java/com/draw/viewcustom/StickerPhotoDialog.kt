package com.draw.viewcustom

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draw.R
import com.draw.adapter.ImageAdapter
import com.draw.extensions.checkPer
import com.draw.viewcustom.presenter.StickerPresenter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StickerPhotoDialog(
    private val presenter: StickerPresenter, // Truyền Presenter vào
    private val context: Context
) : BottomSheetDialogFragment() {

    private lateinit var rvImages: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private var imagePaths: MutableList<String> = mutableListOf()
    private var selectedImagePath: String? = null

    // Kiểm tra quyền truy cập lưu trữ
    val storagePer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    else arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    companion object {
        private const val REQUEST_PERMISSION = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dialogView = inflater.inflate(R.layout.bottom_sheet_insert_photo, container, false)

        rvImages = dialogView.findViewById(R.id.rvImages)
        rvImages.layoutManager = GridLayoutManager(context, 3)

        checkPermission.launch(storagePer)

        val ivCheck = dialogView.findViewById<ImageView>(R.id.ivCheck)
        ivCheck.setOnClickListener {
            if (selectedImagePath != null) {
                getBitmapFromPath(selectedImagePath!!)?.let { bitmap ->
                    // Giữ nguyên kích thước 480 và tỷ lệ ảnh
                    val scaledWidth = 480
                    val scaledHeight = (scaledWidth * bitmap.height / bitmap.width) // Giữ tỷ lệ ảnh

                    // Lấy View cha để tính vị trí giữa
                    val parentView = dialogView.parent as? ViewGroup
                    val parentWidth = parentView?.width ?: 0
                    val parentHeight = parentView?.height ?: 0

                    // Tính toán vị trí trung tâm của sticker
                    val xPos = (parentWidth - scaledWidth) / 2f
                    val yPos = (parentHeight - scaledHeight) / 2f

                    // Thêm sticker với vị trí và kích thước đã tính toán
                    presenter.addStickerPhoto(
                        Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true),
                        xPos,
                        yPos
                    )
                }
                dismiss() // Đóng BottomSheetDialog sau khi thêm sticker
            } else {
                Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }


        return dialogView
    }

    private fun getBitmapFromPath(path: String): Bitmap? {
        return BitmapFactory.decodeFile(path)
    }

    private var checkPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (context.checkPer(storagePer)) loadImages()
        }

    private fun loadImages() {
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media.DATA)

        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (it.moveToNext()) {
                val imagePath = it.getString(columnIndex)
                imagePaths.add(imagePath)
            }
            imagePaths.reverse()

            imageAdapter = ImageAdapter(requireContext(), imagePaths) { imagePath ->
                selectedImagePath = imagePath // Cập nhật đường dẫn ảnh được chọn
            }
            rvImages.adapter = imageAdapter
        } ?: run {
            Toast.makeText(context, "No images found", Toast.LENGTH_SHORT).show()
        }
    }
}
