package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var emailField: TextInputEditText
    private lateinit var passwordField: TextInputEditText
    private lateinit var signInButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_in)

        emailField = findViewById(R.id.email_field)
        passwordField = findViewById(R.id.password_field)
        signInButton = findViewById(R.id.sign_in_button)

        signInButton.setOnClickListener { singIn() }

    }

    private fun singIn() {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        if (checkInput(email, password)) {
            moveToMainActivity(email, password)

        }
        else {
            Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()
            return
        }

    }


    private fun checkInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            return false
        }
        return true
    }

    private fun moveToMainActivity(email: String, password: String) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { signInTask ->
                if (signInTask.isSuccessful) {
                    //exist user
                    startActivity(Intent(this,MainActivity::class.java))
                } else {
                    // cant singIn
                    Toast.makeText(this, "Password or email incorrect", Toast.LENGTH_LONG).show()

                }
            }
    }

}