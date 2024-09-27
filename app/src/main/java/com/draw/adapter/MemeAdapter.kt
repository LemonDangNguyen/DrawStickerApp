package com.draw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.draw.R

class MemeAdapter(
    private val memes: List<Int>
) : RecyclerView.Adapter<MemeAdapter.MemeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meme, parent, false)
        return MemeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        holder.bind(memes[position])
    }

    override fun getItemCount(): Int = memes.size

    inner class MemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageViewMeme)

        fun bind(meme: Int) {
            imageView.setImageResource(meme)
        }
    }
}
