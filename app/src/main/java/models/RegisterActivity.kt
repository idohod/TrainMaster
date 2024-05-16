
package models

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception


class RegisterActivity : AppCompatActivity() {

    private lateinit var quizTitle: MaterialTextView
    private lateinit var userName: MaterialTextView
    private lateinit var nameField: TextInputEditText
    private lateinit var userEmail: MaterialTextView
    private lateinit var emailField: TextInputEditText
    private lateinit var userPassword: MaterialTextView
    private lateinit var passwordField: TextInputEditText
    private lateinit var confirmPassword: MaterialTextView
    private lateinit var confirmPasswordField: TextInputEditText
    private lateinit var registerButton: MaterialButton

    private lateinit var height: MaterialTextView
    private lateinit var heightField: TextInputEditText
    private lateinit var weight: MaterialTextView
    private lateinit var weightField: TextInputEditText

    private lateinit var role: MaterialTextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var coachButton: RadioButton
    private lateinit var traineeButton: RadioButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        findViews()
        registerButton.setOnClickListener { signUp() }
    }

    private fun findViews() {
        quizTitle = findViewById(R.id.quiz_title)
        userName = findViewById(R.id.user_name)
        nameField = findViewById(R.id.name_field)

        userEmail = findViewById(R.id.user_email)
        emailField = findViewById(R.id.email_field)

        userPassword = findViewById(R.id.user_password)
        passwordField = findViewById(R.id.password_field)

        confirmPassword = findViewById(R.id.user_confirm_password)
        confirmPasswordField = findViewById(R.id.confirm_password_field)

        height = findViewById(R.id.user_height)
        heightField = findViewById(R.id.height_field)
        weight = findViewById(R.id.user_weight)
        weightField = findViewById(R.id.weight_field)

        role = findViewById(R.id.user_role)
        radioGroup = findViewById(R.id.radioGroup)
        coachButton = findViewById(R.id.trainee_button)
        traineeButton = findViewById(R.id.coach_button)

        registerButton = findViewById(R.id.register_button)

    }

    private fun roleSelected(): String {

        val checkedRadioButtonId = radioGroup.checkedRadioButtonId
        if (checkedRadioButtonId != -1)
            return findViewById<RadioButton>(checkedRadioButtonId).text.toString()

        return ""

    }

    private fun signUp() {
        val name = nameField.text.toString()
        val email = emailField.text.toString()
        val password = passwordField.text.toString()
        val confirmPassword = confirmPasswordField.text.toString()
        val weight = weightField.text.toString()
        val height = heightField.text.toString()

        val role = roleSelected()

        if (!checkInput(name, email, password, confirmPassword, height, weight, role))
            return

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { createUserTask ->
                if (createUserTask.isSuccessful) {
                    // new user
                    saveUserData(name, email, role)
                    moveActivity(name, role)
                } else {

                    val exception = createUserTask.exception
                    myError(exception)

                }
            }
    }

    private fun myError(exception: Exception?) {
        Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
        return
    }

    private fun checkInput(
        name: String, email: String, password: String, confirmPassword: String,
        height: String, weight: String, role: String

    ): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
            || height.isEmpty() || weight.isEmpty()
        ) {
            Toast.makeText(this, "some data is missing", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!email.endsWith("@gmail.com")) {
            Toast.makeText(this, "illegal gmail", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(this, "password must be least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        if (role == "")
            return false

        return true
    }

    private fun moveActivity(name: String, role: String) {
        if (role == "coach") {
            val i = Intent(this, CoachActivity::class.java)
            startActivity(i)
        } else if (role == "trainee") {
            val i = Intent(this, HealthQuizActivity::class.java)
            i.putExtra("userName", name)
            startActivity(i)
            finish()
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            return
        }

    }

    private fun saveUserData(name: String, email: String, role: String) {

        val sName = name.trim()
        val sEmail = email.trim()
        val sRole = role.trim()

        val userMap = hashMapOf(

            "name" to sName,
            "email" to sEmail,
            "role" to sRole

        )


        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val db = Firebase.firestore

        db.collection("user").document(userId).set(userMap).addOnSuccessListener {
            nameField.text?.clear()
            emailField.text?.clear()

            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {
                Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
            }
    }
}








