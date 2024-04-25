
package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth


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
    }

    private fun singUp() {
        val name  = nameField.text.toString()
        val email = emailField.text.toString()
        val password = passwordField.text.toString()
        val confirmPassword = confirmPasswordField.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()|| confirmPassword.isEmpty()) {
            Toast.makeText(this, "some data is missing", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { createUserTask ->
                if (createUserTask.isSuccessful) {
                    moveToQuizActivity()
                }
                else{
                    singIn(email,password)
                }
            }
    }
    private fun singIn(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { signInTask ->
                if (signInTask.isSuccessful) {
                    // Sign-in successful, retrieve user data
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        moveToMainActivity()
                    }

                } else {
                    // Sign-in failed, handle error
                    val exception = signInTask.exception
                    // Handle the exception, e.g., show an error message
                    Log.e("SignInError", "Sign-in failed: ${exception?.message}")
                }
            }
    }



    private fun moveToQuizActivity() {
        val i = Intent(this,QuizActivity::class.java)
        startActivity(i)


    }

    private fun moveToMainActivity() {
        startActivity(Intent(this,MainActivity::class.java))
    }

}








