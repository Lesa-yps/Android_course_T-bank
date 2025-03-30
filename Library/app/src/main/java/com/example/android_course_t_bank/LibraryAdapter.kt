package com.example.android_course_t_bank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import domain.Book
import domain.Disk
import domain.LibraryObj
import domain.Newspaper


class LibraryAdapter(private val items: MutableList<LibraryObj>): RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>() {

    // Объект, который хранит ссылки на элементы карточки
    class LibraryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTitle: TextView = view.findViewById(R.id.textTitle)
        val imageIcon: ImageView = view.findViewById(R.id.imageIcon)
    }

    // Создание новой карточки
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item, parent, false)
        return LibraryViewHolder(view)
    }

    // Заполнение карточки данными
    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        // Получение элемента из списка
        val item = items[position]
        // Формирование текста (заголовка)
        holder.textTitle.text = holder.itemView.context.getString(R.string.item_text, item.myGetId(), item.myGetName())

        // Установка картинки в зависимости от типа объекта
        val imageRes = when (item) {
            is Book -> R.drawable.ic_book
            is Newspaper -> R.drawable.ic_newspaper
            is Disk -> R.drawable.ic_disk
            else -> R.drawable.ic_default
        }
        holder.imageIcon.setImageResource(imageRes)

        // Установка прозрачности текста и иконки в зависимости от доступности элемента
        val isAvailable = item.myGetIsAvailable()
        val alphaValue = if (isAvailable) 1.0f else 0.3f
        holder.textTitle.alpha = alphaValue
        holder.imageIcon.alpha = alphaValue

        // Установка подъема карточки в зависимости от доступности элемента
        holder.itemView.elevation = if (isAvailable) 10f else 1f

        // Реакция на клик - изменение доступности элемента и обновление UI
        holder.itemView.setOnClickListener {
            item.changeAvailable()
            // Сообщение RecyclerView, что данные изменились, и он перезапускает onBindViewHolder только для этого элемента
            notifyItemChanged(holder.bindingAdapterPosition)
            // Показ тоста
            val context = holder.itemView.context
            val message = context.getString(R.string.toast_item_changed, item.myGetId())
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Общее количество элементов
    override fun getItemCount() = items.size

    // Удаление карточки
    fun removeItem(position: Int) {
        if (position >= 0 && position < items.size) {
            items.removeAt(position)
            // Сообщение адаптеру, что элемент на position был удалён, RecyclerView анимированно уберёт этот элемент из списка
            notifyItemRemoved(position)
            // Сообщение адаптеру, что все элементы, начиная с position, изменились, RecyclerView перерисует эти элементы (без анимации удаления)
            notifyItemRangeChanged(position, items.size)
        }
    }
}