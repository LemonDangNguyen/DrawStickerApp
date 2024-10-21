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
                    val scaledWidth = 480
                    val scaledHeight = 480 * bitmap.height / bitmap.width

                    // Tính toán để ảnh xuất hiện ở giữa container
                    val parentWidth = (container?.width ?: 0) / 2f
                    val parentHeight = (container?.height ?: 0) / 2f

                    // Căn chỉnh tọa độ ảnh để nó ở giữa màn hình
                    val centerX = parentWidth - (scaledWidth / 2f)
                    val centerY = parentHeight - (scaledHeight / 2f)

                    // Gọi hàm để thêm Sticker và căn giữa ảnh
                    presenter.addStickerPhoto(
                        Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true),
                        centerX,
                        centerY
                    )
                }
                dismiss() // Đóng BottomSheetDialog sau khi cập nhật
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
