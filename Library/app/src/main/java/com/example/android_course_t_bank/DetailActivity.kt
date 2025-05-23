package com.example.android_course_t_bank

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import domain.*
import kotlin.collections.find


class DetailActivity : AppCompatActivity() {
    private lateinit var formContainer: LinearLayout
    private var selectedType: String = "Книга"
    private var isReadOnly: Boolean = false
    private var objSave: LibraryObj? = null

    companion object {
        const val LIB_OBJ = "libraryObj"
        const val READ_ONLY = "readOnly"

        fun createIntent(context: Context, obj: LibraryObj?, isReadOnly: Boolean = false): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(LIB_OBJ, obj)
                putExtra(READ_ONLY, isReadOnly)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail)
        formContainer = findViewById(R.id.formContainer)

        isReadOnly = intent.getBooleanExtra(READ_ONLY, false)
        @Suppress("DEPRECATION")
        objSave = intent.getSerializableExtra(LIB_OBJ) as? LibraryObj

        // установка типа в зависимости от переданного объекта
        selectedType = when (objSave) {
            is Newspaper -> "Газета"
            is Disk -> "Диск"
            else -> "Книга"
        }

        // рендер формы
        when (selectedType) {
            "Книга" -> renderFormForBook(objSave as? Book)
            "Газета" -> renderFormForNewspaper(objSave as? Newspaper)
            "Диск" -> renderFormForDisk(objSave as? Disk)
        }

        // в зависимости от кнопок сверху рендерятся поля под разные объекты библиотеки
        val buttonBook = findViewById<Button>(R.id.buttonBook)
        buttonBook.isEnabled = !isReadOnly
        val buttonNewspaper = findViewById<Button>(R.id.buttonNewspaper)
        buttonNewspaper.isEnabled = !isReadOnly
        val buttonDisk = findViewById<Button>(R.id.buttonDisk)
        buttonDisk.isEnabled = !isReadOnly

        // при просмотре информации показывается только кнопка нужного типа
        if (isReadOnly) {
            buttonBook.visibility = if (selectedType == "Книга") Button.VISIBLE else Button.GONE
            buttonNewspaper.visibility = if (selectedType == "Газета") Button.VISIBLE else Button.GONE
            buttonDisk.visibility = if (selectedType == "Диск") Button.VISIBLE else Button.GONE
        }

        findViewById<Button>(R.id.buttonBook).setOnClickListener {
            selectedType = "Книга"
            renderFormForBook()
        }
        findViewById<Button>(R.id.buttonNewspaper).setOnClickListener {
            selectedType = "Газета"
            renderFormForNewspaper()
        }
        findViewById<Button>(R.id.buttonDisk).setOnClickListener {
            selectedType = "Диск"
            renderFormForDisk()
        }

        // кнопочка "Сохранить"
        findViewById<Button>(R.id.buttonSave).apply {
            setOnClickListener {
                saveObject()
            }
        }
    }

    private fun renderFormForBook(book: Book? = null) {
        formContainer.removeAllViews()
        addEditText("ID", "editTextId", book?.id?.toString() ?: "")
        addEditText("Название", "editTextName", book?.name ?: "")
        addEditText("Автор", "editTextAuthor", book?.author ?: "")
        addEditText("Страницы", "editTextPages", book?.pages?.toString() ?: "")
        addCheckBox("Доступна", "checkBoxAvailable", book?.isAvailable == true)
    }

    private fun renderFormForNewspaper(news: Newspaper? = null) {
        formContainer.removeAllViews()
        addEditText("ID", "editTextId", news?.id?.toString() ?: "")
        addEditText("Название", "editTextName", news?.name ?: "")
        addEditText("Выпуск", "editTextIssue", news?.issueNumber?.toString() ?: "")
        addEditText("Месяц", "editTextMonth", news?.month?.toString() ?: "")
        addCheckBox("Доступна", "checkBoxAvailable", news?.isAvailable == true)
    }

    private fun renderFormForDisk(disk: Disk? = null) {
        formContainer.removeAllViews()
        addEditText("ID", "editTextId", disk?.id?.toString() ?: "")
        addEditText("Название", "editTextName", disk?.name ?: "")
        addEditText("Тип (CD или DVD)", "editTextTypeDisk", disk?.type?.name ?: "")
        addCheckBox("Доступен", "checkBoxAvailable", disk?.isAvailable == true)
    }

    private fun addEditText(hint: String, tag: String, value: String = "") {
        if (isReadOnly) {
            val textView = TextView(this).apply {
                this.tag = tag
                text = getString(R.string.readonly_field, hint, value)
                textSize = 16f
                setPadding(16, 16, 16, 16)
            }
            formContainer.addView(textView)
        } else {
            val editText = EditText(this).apply {
                this.hint = hint
                this.tag = tag
                this.setText(value)
                inputType = InputType.TYPE_CLASS_TEXT
            }
            formContainer.addView(editText)
        }
    }

    private fun addCheckBox(text: String, tag: String, value: Boolean = false) {
        val checkBox = CheckBox(this).apply {
            this.text = text
            this.tag = tag
            this.isChecked = value
            isEnabled = true
        }
        formContainer.addView(checkBox)
    }


    // достаются значения из полей, формируется библиотечный объект и возвращается вызвавшему коду
    private fun saveObject() {
        if (isReadOnly) {
            val isAvailableNew = (formContainer.children.find { it.tag == "checkBoxAvailable" } as? CheckBox)?.isChecked == true
            objSave?.isAvailable = isAvailableNew
        }
        else {
            objSave = formingObject()
            if (objSave == null) return
        }

        objSave?.let {
            val resultIntent = Intent().putExtra(LIB_OBJ, it)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        setResult(RESULT_CANCELED)
    }

    private fun formingObject() : LibraryObj? {
        val values = formContainer.children.toList()

        val id = (values.find { it.tag == "editTextId" } as? EditText)?.text.toString().toIntOrNull()
        if (id == null || id < 0) {
            Toast.makeText(this, "Некорректный ID. Введите неотрицательное целое число.", Toast.LENGTH_SHORT).show()
            return null
        }
        val name = (values.find { it.tag == "editTextName" } as? EditText)?.text.toString()
        val isAvailable = (values.find { it.tag == "checkBoxAvailable" } as? CheckBox)?.isChecked == true
        val addedDate = System.currentTimeMillis()

        val obj: LibraryObj? = when (selectedType) {
            "Книга" -> {
                val author = (values.find { it.tag == "editTextAuthor" } as? EditText)?.text.toString()
                val pages = (values.find { it.tag == "editTextPages" } as? EditText)?.text.toString().toIntOrNull() ?: 0
                Book(id, isAvailable, name, pages, author, addedDate)
            }
            "Газета" -> {
                val issue = (values.find { it.tag == "editTextIssue" } as? EditText)?.text.toString().toIntOrNull() ?: 0
                val rawMonth = (values.find { it.tag == "editTextMonth" } as? EditText)?.text.toString().toIntOrNull()
                val month = rawMonth?.takeIf { it in 1..12 } ?: 12
                Newspaper(id, isAvailable, name, issue, month, addedDate)
            }
            "Диск" -> {
                val typeStr = (values.find { it.tag == "editTextTypeDisk" } as? EditText)?.text.toString().uppercase()
                val type = try {
                    DiskType.valueOf(typeStr)
                } catch (_: Exception) {
                    DiskType.CD
                }
                Disk(id, isAvailable, name, type, addedDate)
            }
            else -> null
        }
        return obj
    }
}
