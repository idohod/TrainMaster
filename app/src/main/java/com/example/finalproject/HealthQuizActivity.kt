package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class HealthQuizActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var questionRadioGroup: RadioGroup
    private lateinit var submitButton: Button
    private var currentQuestionIndex = 0
    private lateinit var userName:String
    private lateinit var allExercises: ArrayList<Exercise>

    private var score = 0


    // Define your list of questions here
    private val questions = listOf(
        Question(
            "How many times a week do you work out?",
            listOf("0-1", "2-3", "4+")
        ),
        Question(
            "How many hours of sleep do you get per night?",
            listOf("Less than 6", "6-8", "More than 8")
        ),
        Question(
            "How often do you consume sugary drinks?",
            listOf("Daily", "Weekly", "Rarely")
        ),
        Question(
            "How often do you eat fast food?",
            listOf("Daily", "Weekly", "Rarely")
        )
        // Add more questions here
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_healthquiz)

        questionTextView = findViewById(R.id.question_text_view)
        questionRadioGroup = findViewById(R.id.question_radio_group)
        submitButton = findViewById(R.id.submit_button)
        getUserName()
        allExercises = arrayListOf()
        displayQuestion(currentQuestionIndex)


        submitButton.setOnClickListener { selectAnswer()}
    }

    private fun selectAnswer() {
        val selectedRadioButtonId = questionRadioGroup.checkedRadioButtonId

        if (selectedRadioButtonId != -1) {
            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            val answer = selectedRadioButton.text.toString()
            checkAnswer(answer)
        } else {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun displayQuestion(questionIndex: Int) {
        val question = questions[questionIndex]
        questionTextView.text = question.questionText

        questionRadioGroup.removeAllViews()
        for (option in question.options) {
            val radioButton = RadioButton(this)
            radioButton.text = option
            questionRadioGroup.addView(radioButton)
        }
    }

    private fun checkAnswer(userAnswer: String) {
        var id = questionRadioGroup.checkedRadioButtonId
        id -=  currentQuestionIndex * 3
        score += id
// score 4 - 12

        moveQuestion()

    }

    private fun moveQuestion() {
        // Move to the next question or finish the quiz
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            displayQuestion(currentQuestionIndex)
        } else {
            // Quiz completed, you can add your completion logic here
            Toast.makeText(this, "Quiz completed", Toast.LENGTH_SHORT).show()
            loadExercises(score)
            moveToMainActivity(userName)
        }
    }

    private fun loadExercises(score: Int) {
        val db = FirebaseFirestore.getInstance()
        val exercisesCollection = db.collection("exercises")

        exercisesCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val doc = document.id
                    val level = document.getLong("difficult_level")
                    val type = document.getLong("type")

                    val name = document.getString("exercise_name")
                    if (level != null && name != null && type != null)
                        addExercise(score, level, name, type)

                }
            }
            .addOnFailureListener { exception ->
                Log.w("debug", "Error getting documents.", exception)
            }

    }

    private fun addExercise(score: Int, level: Long, name: String,type :Long) {
        val db = FirebaseDatabase.getInstance()
        val exercisesRef = db.reference.child("users").child(userName).child("exercises")

        if (score in 4..6){ //easy
            if (level == 1L) {
                val ex = Exercise(name, "3", "10", "0");
                allExercises.add(ex)
                Log.d("EX", "$name l = $level t = $type")
            }
        }else if(score in 7..9){ //mid
            if(level == 2L){
                val ex = Exercise(name, "3", "10", "0");
                allExercises.add(ex)
                Log.d("EX", "$name l = $level t = $type")
            }

        }else if(score in 10..11){ //HARD
            if (level == 3L){
                val ex = Exercise(name, "3", "10", "0");
                allExercises.add(ex)
                Log.d("EX", "$name l = $level t = $type")
            }

        }else{ //expert
            if(level >= 4L){
                val ex = Exercise(name, "3", "10", "0");
                allExercises.add(ex)
                Log.d("EX", "$name l = $level t = $type")
            }
        }
         exercisesRef.setValue(allExercises)
    }

    private fun moveToMainActivity(userName: String) {
        val i = Intent(this,MainActivity::class.java)
        i.putExtra("userName",userName)
        startActivity(i)

    }

    private fun getUserName(){
        val i = intent
        userName= i.getStringExtra("userName").toString()
    }


}
