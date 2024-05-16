package models

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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

        signInButton.setOnClickListener { start() }

    }

    private fun start() {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        if (checkInput(email, password)) {
            singIn(email, password)

        } else {
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

    private fun singIn(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { signInTask ->
                if (signInTask.isSuccessful) {
                    loadUserData()
                } else {
                    Toast.makeText(this, " email or password incorrect", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun moveActivity(name: String, role: String) {
        if (role == "trainee") {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("userName", name)
            startActivity(intent)
        } else {
            val intent = Intent(this, CoachActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserData() {
        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("user").document(userId)

        ref.get().addOnSuccessListener {
            if (it != null) {
                val name = it.data?.get("name")?.toString()
                val role = it.data?.get("role")?.toString()

                if (name != null && role != null)
                    moveActivity(name, role)
            }
        }
            .addOnFailureListener {
                Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()

            }
    }
}
