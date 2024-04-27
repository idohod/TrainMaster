package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {
    private lateinit var testButton: ExtendedFloatingActionButton
    private lateinit var testText: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getUserName()


        testButton.setOnClickListener{moveActivity()}
    }
    private fun getUserName()
    {
        val i = intent
        val temp = i.getStringExtra("userName")
        val newTitle = "hello $temp!"
        testText.text = newTitle
    }
    private fun moveActivity() {
        val intent = Intent(this,RegisterActivity::class.java)
        startActivity(intent)

    }
}