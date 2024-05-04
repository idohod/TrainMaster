package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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
    private lateinit var imageLink:String
    private lateinit var userName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        initializeViews()
        getExerciseData()
        setImage()
        if (imageLink != "")
            Glide
                .with(this)
                .load(imageLink)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageView)

    }

    private fun setImage() {
        imageLink = when (theNameTextView.text) {
            "leg press" -> "https://fitnessprogramer.com/wp-content/uploads/2015/11/Leg-Press.gif"
            "chess press" -> "https://i.pinimg.com/originals/b6/28/0f/b6280fbba2b4e7155a4a1901dfeebc9d.gif"
            else -> ""
        }
    }

    private fun getExerciseData() {
        val intent = intent

        theNameTextView.text = intent.getStringExtra("exName").toString()

        setNumberTextView.text = intent.getStringExtra("exSet").toString()
        repetitionsNumberTextView.text = intent.getStringExtra("exRep").toString()
        weightNumberTextView.text = intent.getStringExtra("exWeight").toString()

        userName = intent.getStringExtra("userName").toString()

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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userName",userName)
        startActivity(intent)
    }
}
