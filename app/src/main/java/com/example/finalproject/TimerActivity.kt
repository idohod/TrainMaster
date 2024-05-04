package com.example.finalproject

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView

class TimerActivity : AppCompatActivity() {

    private lateinit var exerciseNameTextView: MaterialTextView
    private lateinit var theNameTextView: MaterialTextView
    private lateinit var setTextTextView: MaterialTextView
    private lateinit var setNumberTextView: MaterialTextView
    private lateinit var repetitionsTextTextView: MaterialTextView
    private lateinit var repetitionsNumberTextView: MaterialTextView
    private lateinit var weightTextTextView: MaterialTextView
    private lateinit var weightNumberTextView: MaterialTextView
    private lateinit var timerTextView: MaterialTextView

    private lateinit var imageView: ImageView

    private lateinit var startButton: ExtendedFloatingActionButton
    private lateinit var finishButton: ExtendedFloatingActionButton
    private lateinit var backButton: ExtendedFloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        initializeViews()

    }

    private fun initializeViews() {
        exerciseNameTextView = findViewById(R.id.exercise_name)
        theNameTextView = findViewById(R.id.the_name)
        setTextTextView = findViewById(R.id.set_text)
        setNumberTextView = findViewById(R.id.set_number)
        repetitionsTextTextView = findViewById(R.id.repetitions_text)
        repetitionsNumberTextView = findViewById(R.id.repetitions_number)
        weightTextTextView = findViewById(R.id.weight_text)
        weightNumberTextView = findViewById(R.id.weight_number)
        timerTextView = findViewById(R.id.timer)
        imageView = findViewById(R.id.imageView)
        startButton = findViewById(R.id.start_button)
        finishButton = findViewById(R.id.finish_button)
        backButton = findViewById(R.id.back_button)
    }
}
