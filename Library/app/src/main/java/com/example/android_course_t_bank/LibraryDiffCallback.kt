package com.example.android_course_t_bank

import androidx.recyclerview.widget.DiffUtil
import domain.LibraryObj

class LibraryDiffCallback(
    private val oldList: List<LibraryObj>,
    private val newList: List<LibraryObj>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    // сравнение по уникальному идентификатору
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    // сравнение по содержимому
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
