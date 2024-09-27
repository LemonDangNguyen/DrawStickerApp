package com.draw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.draw.R

class CategoryMemeAdapter(
    private val categories: List<Int>,
    private val onCategoryClick: (Int) -> Unit
) : RecyclerView.Adapter<CategoryMemeAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.categoryImage)

        fun bind(category: Int) {
            imageView.setImageResource(category)
            itemView.setOnClickListener {
                onCategoryClick(adapterPosition)
            }
        }
    }
}

