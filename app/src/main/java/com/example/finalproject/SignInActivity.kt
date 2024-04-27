package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SignInActivity : AppCompatActivity() {

    private lateinit var emailField: TextInputEditText
    private lateinit var passwordField: TextInputEditText
    private lateinit var signInButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        emailField = findViewById(R.id.email_field)
        passwordField = findViewById(R.id.password_field)
        signInButton = findViewById(R.id.sign_in_button)

        signInButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            // Add logic to sign in with the provided email and password (e.g., validate credentials, handle successful sign in or failed sign in)
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()
            } else {
                // Simulate successful sign in for now
                Toast.makeText(this, "Signed in successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java) // Replace with your target activity after successful sign in
                startActivity(intent)
            }
        }
    }
}
