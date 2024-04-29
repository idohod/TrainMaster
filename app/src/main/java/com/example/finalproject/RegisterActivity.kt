
package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        findViews()
        registerButton.setOnClickListener { singUp() }
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
        registerButton = findViewById(R.id.register_button)
        height = findViewById(R.id.user_height)
        heightField = findViewById(R.id.height_field)
        weight = findViewById(R.id.user_weight)
        weightField = findViewById(R.id.weight_field)

    }

    private fun singUp() {
        val name = nameField.text.toString()
        val email = emailField.text.toString()
        val password = passwordField.text.toString()
        val confirmPassword = confirmPasswordField.text.toString()
        val weight = weightField.text.toString()
        val height = heightField.text.toString()

        if (!checkInput(name, email, password, confirmPassword, height, weight))
            return

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { createUserTask ->
                if (createUserTask.isSuccessful) {
                    // new user
                    saveUserData(name,email)
                    moveToQuizActivity(name)
                } else {
                    Toast.makeText(this, "you already registered", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkInput(
        name: String, email: String, password: String, confirmPassword: String,
        height: String, weight: String

    ): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
            || height.isEmpty() || weight.isEmpty()
        ) {
            Toast.makeText(this, "some data is missing", Toast.LENGTH_SHORT).show()
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
        return true
    }

    private fun moveToQuizActivity(name: String) {
        val i = Intent(this, HealthQuizActivity::class.java)
        i.putExtra("userName",name)
        startActivity(i)
    }

    private fun saveUserData(name:String, email:String){

        val sName = name.trim()
        val sEmail = email.trim()

        val userMap = hashMapOf(

            "name" to sName,
            "email" to sEmail

        )

        val userId =FirebaseAuth.getInstance().currentUser!!.uid
        val db = Firebase.firestore

        db.collection("user").document(userId).set(userMap).addOnSuccessListener {
            nameField.text?.clear()
            emailField.text?.clear()
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener{
                Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
            }
    }
}








