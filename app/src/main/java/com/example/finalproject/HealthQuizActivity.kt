package com.example.finalproject

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HealthQuizActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var questionRadioGroup: RadioGroup
    private lateinit var submitButton: Button
    private var currentQuestionIndex = 0

    // Define your list of questions here
    private val questions = listOf(
        Question(
            "How many times a week do you work out?",
            listOf("0-1", "2-3", "4+"),
            "0-1"
        ),
        Question(
            "How many hours of sleep do you get per night?",
            listOf("Less than 6", "6-8", "More than 8"),
            "6-8"
        ),
        Question(
            "How often do you consume sugary drinks?",
            listOf("Daily", "Weekly", "Rarely"),
            "Rarely"
        ),
        Question(
            "How often do you eat fast food?",
            listOf("Daily", "Weekly", "Rarely"),
            "Rarely"
        )
        // Add more questions here
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_healthquiz)

        questionTextView = findViewById(R.id.question_text_view)
        questionRadioGroup = findViewById(R.id.question_radio_group)
        submitButton = findViewById(R.id.submit_button)

        displayQuestion(currentQuestionIndex)


        submitButton.setOnClickListener {
            val selectedRadioButtonId = questionRadioGroup.checkedRadioButtonId

            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val answer = selectedRadioButton.text.toString()
                checkAnswer(answer)
            } else {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show()
            }
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
        val currentQuestion = questions[currentQuestionIndex]
        if (userAnswer == currentQuestion.correctAnswer) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Incorrect. The correct answer is ${currentQuestion.correctAnswer}", Toast.LENGTH_SHORT).show()
        }

        // Move to the next question or finish the quiz
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            displayQuestion(currentQuestionIndex)
        } else {
            // Quiz completed, you can add your completion logic here
            Toast.makeText(this, "Quiz completed", Toast.LENGTH_SHORT).show()
            // You might want to navigate to another activity or display quiz results here
        }
    }

    data class Question(val questionText: String, val options: List<String>, val correctAnswer: String)
}
