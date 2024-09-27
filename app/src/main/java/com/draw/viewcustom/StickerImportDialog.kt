package com.draw.viewcustom

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draw.R
import com.draw.adapter.CategoryMemeAdapter
import com.draw.adapter.MemeAdapter
import com.draw.database.DataMemeIcon
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StickerImportDialog(
    private var stickerMemeView: StickerMemeView
) : BottomSheetDialogFragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dialogView = inflater.inflate(R.layout.bottom_sheet_sticker_import, container, false)

        // Sử dụng danh sách danh mục và meme từ DataSource
        val categoryList = DataMemeIcon.categories
        var currentMemeList = DataMemeIcon.memesByCategory[0] ?: emptyList()

        // Cài đặt RecyclerView cho danh mục
        val categoryAdapter = CategoryMemeAdapter(categoryList) { categoryPosition ->
            currentMemeList = DataMemeIcon.memesByCategory[categoryPosition] ?: emptyList()
            setupMemeRecyclerView(dialogView, currentMemeList)
        }
        dialogView.findViewById<RecyclerView>(R.id.categoryRecyclerView).apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        // Cài đặt RecyclerView cho meme
        setupMemeRecyclerView(dialogView, currentMemeList)

        // Xử lý nút xác nhận
        val ivCheck = dialogView.findViewById<ImageView>(R.id.ivCheck)
        ivCheck.setOnClickListener {

            stickerMemeView.visibility = View.VISIBLE // Hiển thị StickerMemeView
            dismiss() // Đóng dialog sau khi thêm nhãn dán
        }

        return dialogView
    }

    private fun setupMemeRecyclerView(view: View, memes: List<Int>) {
        val memeAdapter = MemeAdapter(memes)
        view.findViewById<RecyclerView>(R.id.memeRecyclerView).apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = memeAdapter
        }
    }
}
