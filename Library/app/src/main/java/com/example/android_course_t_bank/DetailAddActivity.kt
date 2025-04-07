package com.example.android_course_t_bank

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import domain.*


class DetailAddActivity : AppCompatActivity() {
    private lateinit var formContainer: LinearLayout
    private var selectedType: String = "Книга"

    companion object {
        const val LIB_OBJ = "libraryObj"
        fun createIntent(context: Context): Intent = Intent(context, DetailAddActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_add)
        // В зависимости от кнопок сверху рендерятся поля под разные объекты библиотеки
        formContainer = findViewById(R.id.formContainer)
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
        // Кнопка "Сохранить"
        findViewById<Button>(R.id.buttonSave).setOnClickListener {
            saveObject()
        }
        // По умолчанию рендерится книга
        renderFormForBook()
    }

    private fun renderFormForBook() {
        formContainer.removeAllViews()
        addEditText("ID", "editTextId")
        addEditText("Название", "editTextName")
        addEditText("Автор", "editTextAuthor")
        addEditText("Страницы", "editTextPages")
        addCheckBox("Доступна", "checkBoxAvailable")
    }

    private fun renderFormForNewspaper() {
        formContainer.removeAllViews()
        addEditText("ID", "editTextId")
        addEditText("Название", "editTextName")
        addEditText("Выпуск", "editTextIssue")
        addEditText("Месяц", "editTextMonth")
        addCheckBox("Доступна", "checkBoxAvailable")
    }

    private fun renderFormForDisk() {
        formContainer.removeAllViews()
        addEditText("ID", "editTextId")
        addEditText("Название", "editTextName")
        addEditText("Тип (CD или DVD)", "editTextTypeDisk")
        addCheckBox("Доступен", "checkBoxAvailable")
    }

    private fun addEditText(hint: String, tag: String) {
        val editText = EditText(this)
        editText.hint = hint
        editText.tag = tag
        editText.inputType = InputType.TYPE_CLASS_TEXT
        formContainer.addView(editText)
    }

    private fun addCheckBox(text: String, tag: String) {
        val checkBox = CheckBox(this)
        checkBox.text = text
        checkBox.tag = tag
        formContainer.addView(checkBox)
    }

    // Достаются значения из полей, формируется библиотечный объект и возвращается вызвавшему коду
    private fun saveObject() {
        val values = formContainer.children.toList()

        val id = (values.find { it.tag == "editTextId" } as? EditText)?.text.toString().toIntOrNull() ?: 0
        val name = (values.find { it.tag == "editTextName" } as? EditText)?.text.toString()
        val isAvailable = (values.find { it.tag == "checkBoxAvailable" } as? CheckBox)?.isChecked ?: false

        val obj: LibraryObj? = when (selectedType) {
            "Книга" -> {
                val author = (values.find { it.tag == "editTextAuthor" } as? EditText)?.text.toString()
                val pages = (values.find { it.tag == "editTextPages" } as? EditText)?.text.toString().toIntOrNull() ?: 0
                Book(id, isAvailable, name, pages, author)
            }
            "Газета" -> {
                val issue = (values.find { it.tag == "editTextIssue" } as? EditText)?.text.toString().toIntOrNull() ?: 0
                val month = (values.find { it.tag == "editTextMonth" } as? EditText)?.text.toString().toIntOrNull() ?: 1
                Newspaper(id, isAvailable, name, issue, month)
            }
            "Диск" -> {
                val typeStr = (values.find { it.tag == "editTextTypeDisk" } as? EditText)?.text.toString().uppercase()
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
            val resultIntent = Intent().putExtra(LIB_OBJ, it)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
        setResult(Activity.RESULT_CANCELED)
    }
}
