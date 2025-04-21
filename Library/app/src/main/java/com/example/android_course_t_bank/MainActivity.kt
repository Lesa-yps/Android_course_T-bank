package com.example.android_course_t_bank

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import domain.Library
import domain.LibraryObj


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var errorTextView: TextView
    private val library = Library()
    private val viewModel: LibraryViewModel by viewModels()
    private lateinit var adapter: LibraryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        shimmerLayout = findViewById(R.id.shimmerLayout)
        errorTextView = findViewById(R.id.errorTextView)

        adapter = LibraryAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.state.observe(this) { state ->
            when (state) {
                is State.Loading -> {
                    //println("LOADING")
                    shimmerLayout.visibility = View.VISIBLE
                    shimmerLayout.startShimmer()
                    errorTextView.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                }

                is State.Error -> {
                    //println("ERROR")
                    shimmerLayout.stopShimmer()
                    shimmerLayout.visibility = View.GONE
                    errorTextView.visibility = View.VISIBLE
                    errorTextView.text = state.message
                    recyclerView.visibility = View.GONE
                }

                is State.Data<*> -> {
                    //println("DATA")
                    shimmerLayout.stopShimmer()
                    shimmerLayout.visibility = View.GONE
                    errorTextView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.setItems(state.data as List<LibraryObj>)
                }
            }
        }

        viewModel.loadItems(library)

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                viewModel.removeItem(
                    position = position,
                    onError = {
                        // возврат элемента в адаптер
                        adapter.notifyItemChanged(position)
                    }
                )
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        findViewById<Button>(R.id.buttonAdd).setOnClickListener {
            val intent = DetailActivity.createIntent(this, null, isReadOnly = false)
            addItemLauncher.launch(intent)
        }
    }

    private val addItemLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val newObj: LibraryObj? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getSerializableExtra(DetailActivity.LIB_OBJ, LibraryObj::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getSerializableExtra(DetailActivity.LIB_OBJ) as? LibraryObj
            }
            newObj?.let { obj -> viewModel.addItem(obj) }
        }
    }
}
