package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class QuizActivity : AppCompatActivity() {

    private lateinit var quizTitle : MaterialTextView
    private lateinit var theName : MaterialTextView
    private lateinit var fieldName : TextInputEditText
    private lateinit var saveButton : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        findViews()
        saveButton.setOnClickListener{test()}
    }

    private fun test() {
        Toast.makeText(this, "Regular Button Clicked", Toast.LENGTH_SHORT).show()
    }


    private fun findViews() {
        quizTitle = findViewById(R.id.quiz_title)
        theName = findViewById(R.id.user_name)
        fieldName = findViewById(R.id.name_field)
        saveButton = findViewById(R.id.save_button)
    }
}