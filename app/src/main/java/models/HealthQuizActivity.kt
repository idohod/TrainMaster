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
import com.example.trainMaster.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import utilities.Exercise
import utilities.Question
<<<<<<< HEAD
import kotlin.random.Random

=======
>>>>>>> 40332ada2b62502366380b0ac10d49a1e22ecce6
class HealthQuizActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var questionRadioGroup: RadioGroup
    private lateinit var submitButton: Button
    private var currentQuestionIndex = 0
    private lateinit var allExercises: ArrayList<Exercise>
    private lateinit var userName: String
    private lateinit var userEmail: String
    private lateinit var userPassword:String

    private  var numOfQuiz: Int = 0
    private var score = 0
    private var temp = 0
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
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_healthquiz)

        initViews()
        getUserData()
        numOfQuiz = getNumOfQuiz()
        displayQuestion(currentQuestionIndex)

        submitButton.setOnClickListener { selectAnswer() }
    }
    private fun getNumOfQuiz(): Int {
        return intent.getIntExtra("numOfQuiz", 0)
    }
    private fun initViews() {
        questionTextView = findViewById(R.id.question_text_view)
        questionRadioGroup = findViewById(R.id.question_radio_group)
        submitButton = findViewById(R.id.submit_button)
        allExercises = arrayListOf()
    }
    private fun getUserData() {
         userName = intent.getStringExtra("userName").toString()
         userEmail = intent.getStringExtra("userEmail").toString()
         userPassword = intent.getStringExtra("userPassword").toString()

    }
    private fun selectAnswer() {

        val selectedRadioButtonId = questionRadioGroup.checkedRadioButtonId
        if (selectedRadioButtonId - temp > 0) {
            temp = selectedRadioButtonId
            findViewById<RadioButton>(selectedRadioButtonId)
            checkAnswer()
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
    private fun checkAnswer() {
        var id = questionRadioGroup.checkedRadioButtonId
        id -= numOfQuiz * 39
        id -= currentQuestionIndex * 3
        score += id
        moveQuestion()
    }
    private fun moveQuestion() {
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            displayQuestion(currentQuestionIndex)
        } else {
            // Quiz completed
            numOfQuiz += 1
            loadExercises()
            moveToMenuActivity()
        }
    }
    private fun loadExercises() {
        val firestore = FirebaseFirestore.getInstance()
        val exercisesCollection = firestore.collection("exercises")

        val database = FirebaseDatabase.getInstance()
        val exercisesRef = database.reference.child("users").child(userName).child("exercises")

        exercisesCollection.get()
            .addOnSuccessListener { result ->
                for (document in result)
                    getExercisesData(document)
                setExList()
                exercisesRef.setValue(allExercises)
            }
            .addOnFailureListener { exception ->Log.w("debug", "Error getting documents.", exception)
            }
    }
    private fun getExercisesData(document: QueryDocumentSnapshot) {

        val level = document.getLong("difficult_level")?: return
        val type = document.getLong("type")?: return
        val name = document.getString("exercise_name")?: return

        addExercise(level, name, type)
    }
    private fun setExList() {

        val max1 = 1; val max2 = 2
        var counter = 0; var index: Int
        var curType: Long = 1; var preType: Long
        var isMax2 = true

        val max1List = arrayOf(2L, 3L, 6L, 7L)
        val max2List = arrayOf(1L, 4L, 5L)

        val ranIndex = mutableListOf<Int>()
<<<<<<< HEAD
        var exToRemove = ArrayList<Exercise>()
=======
        val exToRemove = ArrayList<Exercise>()
>>>>>>> 40332ada2b62502366380b0ac10d49a1e22ecce6

        for (ex in allExercises) {
            preType = curType
            curType = ex.type!!
            index = allExercises.indexOf(ex)

            if (curType != preType) {
<<<<<<< HEAD
                if (isMax2 && counter >= 2) {
                    val count = ranIndex.size - max2
                    val range = ranIndex[0]..ranIndex[ranIndex.size-1]
                    val uniqueNumbers = generateUniqueRandomNumbers(count, range)
                    exToRemove = saveExercisesToRemove(uniqueNumbers, exToRemove)
                }
                else if (!isMax2 && counter >= 1) {
                    val count = ranIndex.size - max1
                    val range = ranIndex[0]..ranIndex[ranIndex.size-1]
                    val uniqueNumbers = generateUniqueRandomNumbers(count, range)
                    exToRemove = saveExercisesToRemove(uniqueNumbers, exToRemove)
                }
=======
                if (isMax2 && counter >= 2)
                    exToRemove.add(allExercises[ranIndex.random()])
                else if (!isMax2 && counter >= 1)
                    exToRemove.add(allExercises[ranIndex.random()])

>>>>>>> 40332ada2b62502366380b0ac10d49a1e22ecce6
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
<<<<<<< HEAD

    private fun generateUniqueRandomNumbers(count: Int, range: IntRange): Set<Int> {
        val uniqueNumbers = mutableSetOf<Int>()
        while (uniqueNumbers.size < count) {
            val number = Random.nextInt(range.first, range.last + 1)
            uniqueNumbers.add(number)
        }
        return uniqueNumbers
    }
    private fun saveExercisesToRemove(uniqueNumbers: Set<Int>,exToRemove: ArrayList<Exercise>): ArrayList<Exercise> {
        for (i in uniqueNumbers)
            exToRemove.add(allExercises[i])

        return exToRemove
    }


=======
>>>>>>> 40332ada2b62502366380b0ac10d49a1e22ecce6
    private fun removeExercises(exToRemove: ArrayList<Exercise>) {
        for (ex in exToRemove)
            allExercises.remove(ex)
    }
    private fun addExercise(level: Long, name: String, type: Long) {

        if (score in 13..19) { //easy
            if (level == 1L) {
                val ex = Exercise(name, "3", "8", "0", type, level)
                allExercises.add(ex)
            }
        } else if (score in 20..26) { //mid

            if (level == 2L) {
                val ex = Exercise(name, "3", "10", "0", type, level)
                allExercises.add(ex)
            }

        } else if (score in 27..32) { //HARD

            if (level == 3L) {
                val ex = Exercise(name, "3", "12", "0", type, level)
                allExercises.add(ex)
            }

        } else { //expert
            if (level >= 4L) {
                val ex = Exercise(name, "4", "12", "0", type, level)
                allExercises.add(ex)
            }
        }
    }
    private fun moveToMenuActivity() {
        val i = Intent(this, MenuActivity::class.java)
        i.putExtra("userName", userName)
        i.putExtra("userEmail", userEmail)
        i.putExtra("userPassword", userPassword)
        i.putExtra("numOfQuiz", numOfQuiz)
        startActivity(i)
        finish()
    }
}