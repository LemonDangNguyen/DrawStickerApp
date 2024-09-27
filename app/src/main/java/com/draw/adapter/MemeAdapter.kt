package com.draw.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.draw.R

class MemeAdapter(
    private val memes: List<Int>
) : RecyclerView.Adapter<MemeAdapter.MemeViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION // Vị trí được chọn

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meme, parent, false)
        return MemeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        holder.bind(memes[position], position)
    }

    override fun getItemCount(): Int = memes.size

    inner class MemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageViewMeme)

        fun bind(meme: Int, position: Int) {
            imageView.setImageResource(meme)

            // Đặt màu nền dựa trên vị trí được chọn
            if (position == selectedPosition) {
                itemView.setBackgroundColor(Color.LTGRAY) // Màu khi được chọn
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT) // Màu mặc định
            }

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition

                // Cập nhật màu nền của meme cũ và meme mới
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }
}
