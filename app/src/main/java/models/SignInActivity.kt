package models

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trainMaster.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SignInActivity : AppCompatActivity() {
    private lateinit var emailField: TextInputEditText
    private lateinit var passwordField: TextInputEditText
    private lateinit var signInButton: MaterialButton
    private lateinit var backButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        findViews()
        signInButton.setOnClickListener { start() }
        backButton.setOnClickListener { backToStart() }
    }
    private fun backToStart() {
        startActivity(Intent(this,StartPage::class.java))
        finish()
    }
    private fun findViews() {
        emailField = findViewById(R.id.email_field)
        passwordField = findViewById(R.id.password_field)
        signInButton = findViewById(R.id.sign_in_button)
        backButton = findViewById(R.id.back_button)
    }
    private fun start() {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        if (checkInput(email, password))
            singIn(email, password)
        else
            return
    }
    private fun checkInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!email.endsWith("@gmail.com")) {
            Toast.makeText(this, "illegal gmail", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun singIn(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { signInTask ->
                if (signInTask.isSuccessful) {
                    loadUserData(password)
                } else {
                    Toast.makeText(this, " email or password incorrect", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun moveActivity(name: String,role: String,email: String,password: String,trainingHistory: String) {

        if (role == "trainee") {
            updateTrainingHistory(trainingHistory)
            saveDateTime()

            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("userName", name)
            intent.putExtra("userEmail", email)
            intent.putExtra("userPassword", password)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, CoachActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveDateTime() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userDocRef = db.collection("user").document(userId)

        val formattedDateTime = getTime()

        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists())
                updateTimes(document, formattedDateTime, userDocRef)
            else
                initDocument(userDocRef, formattedDateTime)
        }
    }

    private fun getTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return currentDateTime.format(formatter)
    }

    private fun initDocument(userDocRef: DocumentReference, formattedDateTime: String) {
        val loginTimes = arrayListOf(formattedDateTime)
        userDocRef.set(mapOf("loginTimes" to loginTimes))
    }

    private fun updateTimes(document: DocumentSnapshot, formattedDateTime: String, userDocRef: DocumentReference) {

        val loginTimes = document.get("loginTimes") as? List<*> ?: emptyList<Any>()
        val loginTimesList = ArrayList(loginTimes.filterIsInstance<String>())

        loginTimesList.add(formattedDateTime)
        userDocRef.update("loginTimes", loginTimesList)
    }


    private fun updateTrainingHistory(trainingHistory: String) {
        var temp = trainingHistory.toInt()
        temp += 1
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val db = Firebase.firestore
            db.collection("user").document(userId).update("trainingHistory", temp.toString())
        }
    }

    private fun loadUserData(password: String) {
        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("user").document(userId)

        ref.get().addOnSuccessListener {
            if (it != null)
                getUserData(it,password)
        }
            .addOnFailureListener {exception -> Log.w("TAG", "Error getting documents.", exception)}
    }
    private fun getUserData(it: DocumentSnapshot, password: String) {
        val name = it.data?.get("name")?.toString() ?: return
        val role = it.data?.get("role")?.toString() ?: return
        val email = it.data?.get("email")?.toString() ?: return

        if(role == "trainee") {
            val trainingHistory = it.data?.get("trainingHistory")?.toString() ?: return
            moveActivity(name, role, email, password, trainingHistory)
        }
        else
            moveActivity(name, role, email, password, "0")
    }
    override fun onBackPressed() {
        startActivity(Intent(this,StartPage::class.java))
        finish()
    }
}