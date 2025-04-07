package com.example.android_course_t_bank

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import domain.*

class DetailInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail_info)

        // Обработка кнопки "Назад"
        val buttonBack: Button = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish() // Закрыть текущую Activity
        }

        // Получение объекта из Intent
        val libraryObject: LibraryObj? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(LIB_OBJ, LibraryObj::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(LIB_OBJ) as? LibraryObj
        }
        // Вывод информации об объекте
        val titleTextView: TextView = findViewById(R.id.textViewDetails)
        titleTextView.text = libraryObject?.getLongInfo() ?: "Объект не найден"
    }

    companion object {
        const val LIB_OBJ = "libraryObj"
        fun createIntent(context: Context): Intent {
            return Intent(context, DetailInfoActivity::class.java)
        }
    }
}