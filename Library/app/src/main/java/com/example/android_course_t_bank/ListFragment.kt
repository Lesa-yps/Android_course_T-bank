package com.example.android_course_t_bank

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import domain.Library
import domain.LibraryObj


class ListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonAdd: Button
    private val viewModel: LibraryViewModel by activityViewModels()
    private lateinit var adapter: LibraryAdapter
    private var listener: OnLibraryItemClickListener? = null

    interface OnLibraryItemClickListener {
        fun onAddItemRequested()
        fun onLibraryItemClick(item: LibraryObj)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLibraryItemClickListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerView)
        buttonAdd = view.findViewById(R.id.buttonAdd)

        val library = Library()

        val allItems = mutableListOf<LibraryObj>().apply {
            addAll(library.getBooks())
            addAll(library.getNewspapers())
            addAll(library.getDisks())
        }

        adapter = LibraryAdapter(allItems.toMutableList()) { item ->
            listener?.onLibraryItemClick(item)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeItem(viewHolder.bindingAdapterPosition)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)

        buttonAdd.setOnClickListener {
            listener?.onAddItemRequested()
        }

        viewModel.items.observe(viewLifecycleOwner) {
            adapter.setItems(it)
        }

        viewModel.loadItems(library)

        view.setBackgroundColor(0xFFFFC0CB.toInt())
    }

    // Метод для добавления элемента
    fun addItem(obj: LibraryObj) {
        val currentItems = viewModel.items.value?.toMutableList() ?: mutableListOf()
        currentItems.add(obj)
        viewModel.addItem(obj)
    }
}