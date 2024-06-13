package models

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class TimerActivity : AppCompatActivity() {

    private lateinit var exerciseNameTextView: MaterialTextView
    private lateinit var theNameTextView: MaterialTextView
    private lateinit var setTextTextView: MaterialTextView
    private lateinit var setNumberTextView: MaterialTextView
    private lateinit var repetitionsTextTextView: MaterialTextView
    private lateinit var repetitionsNumberTextView: MaterialTextView
    private lateinit var weightTextTextView: MaterialTextView
    private lateinit var weightNumberTextView: MaterialTextView

    private lateinit var exerciseGif: ImageView

    private lateinit var startButton: ExtendedFloatingActionButton
    private lateinit var finishButton: ExtendedFloatingActionButton
    private lateinit var backButton: ExtendedFloatingActionButton

    private lateinit var timerTextView: MaterialTextView
    private var isTimerRunning = false
    private var secondsElapsed = 0
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var userName: String
    private lateinit var userEmail: String
    private lateinit var userPassword: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        initializeViews()
        getExerciseData()
        getLinkFromDB()
        getUserData()

        startButton.setOnClickListener { startTimer() }
        finishButton.setOnClickListener { stopTimer() }
        backButton.setOnClickListener { backToPlan() }

    }

    private fun backToPlan() {
        val intent = Intent(this, MenuActivity::class.java)

        startActivity(intent)
        finish()

    }

    private fun stopTimer() {
        isTimerRunning = false
        handler.removeCallbacksAndMessages(null)
    }

    private fun startTimer() {
        isTimerRunning = true
        secondsElapsed = 0
        handler.post(object : Runnable {
            override fun run() {
                val hours = secondsElapsed / 3600
                val minutes = (secondsElapsed % 3600) / 60
                val seconds = secondsElapsed % 60
                val time = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                timerTextView.text = time
                secondsElapsed++
                handler.postDelayed(this, 1000)

            }
        })
    }
    private fun getUserData()  {
        userName = intent.getStringExtra("userName").toString()
        userEmail = intent.getStringExtra("userEmail").toString()
        userPassword = intent.getStringExtra("userPassword").toString()
    }
    private fun getExerciseData() {
        val intent = intent

        theNameTextView.text = intent.getStringExtra("exName").toString()
        setNumberTextView.text = intent.getStringExtra("exSet").toString()
        repetitionsNumberTextView.text = intent.getStringExtra("exRep").toString()
        weightNumberTextView.text = intent.getStringExtra("exWeight").toString()
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
        exerciseGif = findViewById(R.id.imageView)
        startButton = findViewById(R.id.start_button)
        finishButton = findViewById(R.id.finish_button)
        backButton = findViewById(R.id.back_button)
    }
    private fun getLinkFromDB() {
        val db = FirebaseFirestore.getInstance()
        val exercisesCollection = db.collection("exercises")
        exercisesCollection.get()
            .addOnSuccessListener { result ->
                for (document in result)
                    getImage(document)
            }
            .addOnFailureListener {exception ->Log.w("TAG", "Error getting documents.", exception)}
    }
    private fun getImage(document: QueryDocumentSnapshot) {
        val name = document.getString("exercise_name") ?: return
        val link = document.getString("link") ?: return
        uploadImage(name, link)
    }
    private fun uploadImage(name: String, link: String) {
        if (name == theNameTextView.text)
            Glide
                .with(this)
                .load(link)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(exerciseGif)
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userName", userName)
        intent.putExtra("userEmail", userEmail)
        intent.putExtra("userPassword", userPassword)
        startActivity(intent)
        finish()
    }
}