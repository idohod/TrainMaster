package models

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import utilities.Exercise
import utilities.Question

class HealthQuizActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var questionRadioGroup: RadioGroup
    private lateinit var submitButton: Button
    private var currentQuestionIndex = 0
    private lateinit var allExercises: ArrayList<Exercise>

    private var score = 0
    private var temp = 0

    // Define your list of questions here
    private val questions = listOf(
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
        ),
        Question(
            "How many servings of fruits and vegetables do you eat per day?",
            listOf("0-1", "2-3", "4+"),
        ),
        Question(
            "How often do you engage in mindfulness or relaxation activities (e.g., meditation, yoga)?",
            listOf("Rarely", "Weekly", "Daily")
        ),
        Question(
            "How many liters of water do you drink per day?",
            listOf("Less than 1", "1-2", "More than 2")
        ),
        Question(
            "How often do you eat breakfast?",
            listOf("Rarely", "Sometimes", "Daily")
        ),
        Question(
            "How often do you consume alcohol?",
            listOf("Never", "Occasionally", "Regularly")
        ),
        Question(
            "How often do you experience symptoms like headaches, fatigue, or digestive issues?",
            listOf("Rarely", "Sometimes", "Often")
        ),

        Question(
            "How many times a week do you engage in strength training exercises?",
            listOf("0", "1-2", "3+")
        ),
        Question(
            "How would you rate your current level of physical fitness?",
            listOf("Poor", "Average", "Excellent")
        ),
        Question(
            "How often do you stretch or perform flexibility exercises?",
            listOf("Rarely", "Weekly", "Daily")
        ),
        Question(
            "How motivated are you to maintain a regular fitness routine?",
            listOf("Not motivated", "Somewhat motivated", "Very motivated")
        )
        //13 question
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_healthquiz)

        initViews()
        val userName = getUserName()
        val numOfQuiz = getNumOfQuiz()
        displayQuestion(currentQuestionIndex)

        submitButton.setOnClickListener { selectAnswer(numOfQuiz, userName) }
    }

    private fun getNumOfQuiz(): Int {
        val i = intent
        return i.getIntExtra("numOfQuiz", 0)
    }

    private fun initViews() {
        questionTextView = findViewById(R.id.question_text_view)
        questionRadioGroup = findViewById(R.id.question_radio_group)
        submitButton = findViewById(R.id.submit_button)
        allExercises = arrayListOf()
    }
    private fun getUserName(): String {
        val i = intent
        return i.getStringExtra("userName").toString()
    }

    private fun selectAnswer(numOfQuiz: Int, userName: String) {

        val selectedRadioButtonId = questionRadioGroup.checkedRadioButtonId


        if (selectedRadioButtonId - temp > 0) {
            temp = selectedRadioButtonId
            findViewById<RadioButton>(selectedRadioButtonId)
            //  val answer = selectedRadioButton.text.toString()
            checkAnswer(numOfQuiz, userName)
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

    private fun checkAnswer(numOfQuiz: Int, userName: String) {
        var id = questionRadioGroup.checkedRadioButtonId
        id -= numOfQuiz * 39
        id -= currentQuestionIndex * 3

        score += id

// score 13 - 39
        moveQuestion(numOfQuiz, userName)

    }

    private fun moveQuestion(numOfQuiz: Int, userName: String) {
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            displayQuestion(currentQuestionIndex)
        } else {
            // Quiz completed
            numOfQuiz + 1
            loadExercises(userName)
            moveToMainActivity(userName, numOfQuiz)
        }
    }

    private fun loadExercises(userName: String) {
        val firestore = FirebaseFirestore.getInstance()
        val exercisesCollection = firestore.collection("exercises")

        val database = FirebaseDatabase.getInstance()
        val exercisesRef = database.reference.child("users").child(userName).child("exercises")

        exercisesCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //  val doc = document.id
                    val level = document.getLong("difficult_level")
                    val type = document.getLong("type")

                    val name = document.getString("exercise_name")
                    if (level != null && name != null && type != null)
                        addExercise(level, name, type)

                }
                setExList()
                exercisesRef.setValue(allExercises)
            }
            .addOnFailureListener { exception ->
                Log.w("debug", "Error getting documents.", exception)
            }

    }

    private fun setExList() {

        val max1 = 1; val max2 = 2
        var counter = 0; var index: Int
        var curType: Long = 1; var preType: Long
        var isMax2 = true

        val max1List = arrayOf(2L, 3L, 6L, 7L)
        val max2List = arrayOf(1L, 4L, 5L)

        val ranIndex = mutableListOf<Int>()
        val exToRemove = ArrayList<Exercise>()

        for (ex in allExercises) {
            preType = curType
            curType = ex.type!!
            index = allExercises.indexOf(ex)

            if (curType != preType) {
                if (isMax2 && counter >= 2)
                    exToRemove.add(allExercises[ranIndex.random()])
                else if (!isMax2 && counter >= 1)
                    exToRemove.add(allExercises[ranIndex.random()])

                ranIndex.clear()
                counter = 0
            }

            if (ex.type in max2List) {
                isMax2 = true
                if (counter < max2) {
                    ranIndex.add(index)
                    counter++
                } else
                    ranIndex.add(index)

            } else if (ex.type in max1List) {
                isMax2 = false
                if (counter < max1) {
                    ranIndex.add(index)
                    counter++
                } else
                    ranIndex.add(index)
            }
        }
        removeExercises(exToRemove)
    }
    private fun removeExercises(exToRemove: ArrayList<Exercise>) {
        for (ex in exToRemove) {
            allExercises.remove(ex)
        }
    }
    private fun addExercise(level: Long, name: String, type: Long) {

        if (score in 13..19) { //easy
            if (level == 1L) {
                val ex = Exercise(name, "3", "10", "0", type, level)
                allExercises.add(ex)
            }
        } else if (score in 20..26) { //mid

            if (level == 2L) {
                val ex = Exercise(name, "3", "10", "0", type, level)
                allExercises.add(ex)
            }

        } else if (score in 27..32) { //HARD

            if (level == 3L) {
                val ex = Exercise(name, "3", "10", "0", type, level)
                allExercises.add(ex)
            }

        } else { //expert
            if (level >= 4L) {
                val ex = Exercise(name, "3", "10", "0", type, level)
                allExercises.add(ex)
            }
        }

    }

    private fun moveToMainActivity(userName: String, numOfQuiz: Int) {
        val i = Intent(this, MainActivity::class.java)
        i.putExtra("userName", userName)
        i.putExtra("numOfQuiz", numOfQuiz)
        startActivity(i)
        finish()

    }


}
