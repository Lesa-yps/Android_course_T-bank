package com.example.android_course_t_bank

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import domain.*


class DetailFragment : Fragment() {

    private lateinit var formContainer: LinearLayout
    private var selectedType: String = "Книга"
    private var isReadOnly: Boolean = false
    private var libraryObj: LibraryObj? = null
    private var listener: OnSaveListener? = null

    interface OnSaveListener {
        fun onObjectSaved(obj: LibraryObj)
    }

    companion object {
        private const val ARG_OBJ = "libraryObj"
        private const val ARG_READ_ONLY = "readOnly"

        fun newInstance(obj: LibraryObj?, isReadOnly: Boolean): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_OBJ, obj)
                    putBoolean(ARG_READ_ONLY, isReadOnly)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSaveListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION")
            libraryObj = it.getSerializable(ARG_OBJ) as? LibraryObj
            isReadOnly = it.getBoolean(ARG_READ_ONLY)
        }

        selectedType = when (libraryObj) {
            is Newspaper -> "Газета"
            is Disk -> "Диск"
            else -> "Книга"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        formContainer = view.findViewById(R.id.formContainer)

        val buttonBook = view.findViewById<Button>(R.id.buttonBook)
        val buttonNewspaper = view.findViewById<Button>(R.id.buttonNewspaper)
        val buttonDisk = view.findViewById<Button>(R.id.buttonDisk)
        val buttonSave = view.findViewById<Button>(R.id.buttonSave)

        buttonBook.isEnabled = !isReadOnly
        buttonNewspaper.isEnabled = !isReadOnly
        buttonDisk.isEnabled = !isReadOnly

        if (isReadOnly) {
            listOf(buttonBook, buttonNewspaper, buttonDisk).forEach {
                it.visibility = if (it.text == selectedType) Button.VISIBLE else Button.GONE
            }
        }

        buttonBook.setOnClickListener {
            selectedType = "Книга"
            renderFormForBook()
        }

        buttonNewspaper.setOnClickListener {
            selectedType = "Газета"
            renderFormForNewspaper()
        }

        buttonDisk.setOnClickListener {
            selectedType = "Диск"
            renderFormForDisk()
        }

        buttonSave.text = if (isReadOnly) "Назад" else "Сохранить"
        buttonSave.setOnClickListener {
            if (isReadOnly) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } else {
                saveObject()
            }
        }

        when (selectedType) {
            "Книга" -> renderFormForBook(libraryObj as? Book)
            "Газета" -> renderFormForNewspaper(libraryObj as? Newspaper)
            "Диск" -> renderFormForDisk(libraryObj as? Disk)
        }

        view.setBackgroundColor(0xFFAAC0CB.toInt())
    }

    private fun renderFormForBook(book: Book? = null) {
        formContainer.removeAllViews()
        addEditText("ID", "editTextId", book?.id?.toString() ?: "")
        addEditText("Название", "editTextName", book?.name ?: "")
        addEditText("Автор", "editTextAuthor", book?.author ?: "")
        addEditText("Страницы", "editTextPages", book?.pages?.toString() ?: "")
        addCheckBox("Доступна", "checkBoxAvailable", book?.isAvailable ?: false)
    }

    private fun renderFormForNewspaper(news: Newspaper? = null) {
        formContainer.removeAllViews()
        addEditText("ID", "editTextId", news?.id?.toString() ?: "")
        addEditText("Название", "editTextName", news?.name ?: "")
        addEditText("Выпуск", "editTextIssue", news?.issueNumber?.toString() ?: "")
        addEditText("Месяц", "editTextMonth", news?.month?.toString() ?: "")
        addCheckBox("Доступна", "checkBoxAvailable", news?.isAvailable ?: false)
    }

    private fun renderFormForDisk(disk: Disk? = null) {
        formContainer.removeAllViews()
        addEditText("ID", "editTextId", disk?.id?.toString() ?: "")
        addEditText("Название", "editTextName", disk?.name ?: "")
        addEditText("Тип (CD или DVD)", "editTextTypeDisk", disk?.type?.name ?: "")
        addCheckBox("Доступен", "checkBoxAvailable", disk?.isAvailable ?: false)
    }

    private fun addEditText(hint: String, tag: String, value: String = "") {
        if (isReadOnly) {
            val textView = TextView(requireContext()).apply {
                this.tag = tag
                text = getString(R.string.readonly_field, hint, value)
                textSize = 16f
                setPadding(16, 16, 16, 16)
            }
            formContainer.addView(textView)
        } else {
            val editText = EditText(requireContext()).apply {
                this.hint = hint
                this.tag = tag
                this.setText(value)
                inputType = InputType.TYPE_CLASS_TEXT
            }
            formContainer.addView(editText)
        }
    }

    private fun addCheckBox(text: String, tag: String, value: Boolean = false) {
        val checkBox = CheckBox(requireContext()).apply {
            this.text = text
            this.tag = tag
            this.isChecked = value
            isEnabled = !isReadOnly
        }
        formContainer.addView(checkBox)
    }

    private fun saveObject() {
        val id = (formContainer.findViewWithTag<EditText>("editTextId")?.text.toString().toIntOrNull()) ?: 0
        val name = formContainer.findViewWithTag<EditText>("editTextName")?.text.toString()
        val isAvailable = formContainer.findViewWithTag<CheckBox>("checkBoxAvailable")?.isChecked ?: false

        val obj: LibraryObj? = when (selectedType) {
            "Книга" -> {
                val author = formContainer.findViewWithTag<EditText>("editTextAuthor")?.text.toString()
                val pages = formContainer.findViewWithTag<EditText>("editTextPages")?.text.toString().toIntOrNull() ?: 0
                Book(id, isAvailable, name, pages, author)
            }
            "Газета" -> {
                val issueNumber = formContainer.findViewWithTag<EditText>("editTextIssue")?.text.toString().toIntOrNull() ?: 0
                val rawMonth = formContainer.findViewWithTag<EditText>("editTextMonth")?.text.toString().toIntOrNull()
                val month = rawMonth?.takeIf { it in 1..12 } ?: 12
                Newspaper(id, isAvailable, name, issueNumber, month)
            }
            "Диск" -> {
                val typeStr = formContainer.findViewWithTag<EditText>("editTextTypeDisk")?.text.toString().uppercase()
                val type = try {
                    DiskType.valueOf(typeStr)
                } catch (e: Exception) {
                    DiskType.CD
                }
                Disk(id, isAvailable, name, type)
            }
            else -> null
        }

        obj?.let {
            listener?.onObjectSaved(it)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}