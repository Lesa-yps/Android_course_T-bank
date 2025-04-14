package com.example.android_course_t_bank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import domain.Book
import domain.Disk
import domain.LibraryObj
import domain.Newspaper

class LibraryAdapter(
    private val items: MutableList<LibraryObj>,
    private val onItemClick: (LibraryObj) -> Unit
) : RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>() {

    class LibraryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTitle: TextView = view.findViewById(R.id.textTitle)
        val imageIcon: ImageView = view.findViewById(R.id.imageIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item, parent, false)
        return LibraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val item = items[position]

        holder.textTitle.text = holder.itemView.context.getString(
            R.string.item_text, item.myGetId(), item.myGetName()
        )

        val imageRes = when (item) {
            is Book -> R.drawable.ic_book
            is Newspaper -> R.drawable.ic_newspaper
            is Disk -> R.drawable.ic_disk
            else -> R.drawable.ic_default
        }
        holder.imageIcon.setImageResource(imageRes)

        val isAvailable = item.myGetIsAvailable()
        val alphaValue = if (isAvailable) 1.0f else 0.3f
        holder.textTitle.alpha = alphaValue
        holder.imageIcon.alpha = alphaValue
        holder.itemView.elevation = if (isAvailable) 10f else 1f

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = items.size

    fun removeItem(position: Int) {
        if (position in items.indices) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }
    }

    fun setItems(newItems: List<LibraryObj>) {
        val diffCallback = LibraryDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }
}