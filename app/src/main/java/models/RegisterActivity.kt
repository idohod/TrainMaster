package models

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trainMaster.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    private lateinit var backButton: Button

    private  var isVisible: Boolean = true
    private  var numOfQuiz =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        findViews()
        getNumOfQuiz()

        backButton.setOnClickListener {backToStart()}
        registerButton.setOnClickListener {signUp()}

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRole = findViewById<RadioButton>(checkedId).text.toString()
            isVisible = checkRole(selectedRole)
        }
    }
    private fun backToStart(){
        startActivity(Intent(this,StartPage::class.java))
        finish()
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        startActivity(Intent(this,StartPage::class.java))
        finish()
    }
    private fun getNumOfQuiz() {
        val i = intent
        numOfQuiz = i.getIntExtra("numOfQuiz",0)
    }
    private fun findViews() {
        quizTitle = findViewById(R.id.quiz_title)
        backButton = findViewById(R.id.back_button)
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
        backButton = findViewById(R.id.back_button)
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
        setBMI(height,weight)

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { createUserTask ->
                if (createUserTask.isSuccessful) {
                    // new user
                    saveUserData(name, email, role,password)
                    moveActivity(name, role,email,password)
                } else
                    makeToast("this email in use")
            }
    }
    private fun setBMI(height: String, weight: String) {
        if (isVisible) {
            val myBMI = calculateBMI(height, weight)
            if (myBMI == 0.0)
                return
            else
                makeToast("your BMI: $myBMI")
        }
    }
    private fun checkRole(role: String): Boolean {
        if (role == "coach") {
            height.visibility = View.INVISIBLE
            heightField.visibility = View.INVISIBLE
            weight.visibility = View.INVISIBLE
            weightField.visibility = View.INVISIBLE
            return false

        } else {
            height.visibility = View.VISIBLE
            heightField.visibility = View.VISIBLE
            weight.visibility = View.VISIBLE
            weightField.visibility = View.VISIBLE
            return true
        }
    }
    private fun calculateBMI(heightInput: String, weightInput: String): Double {

        var height = heightInput.toDoubleOrNull() ?: return 0.0
        val weight = weightInput.toDoubleOrNull() ?: return 0.0

        if (height > 100)
            height /= 100
        return weight / (height * height)
    }
    private fun checkInput(
        name: String, email: String, password: String, confirmPassword: String,
        height: String, weight: String, role: String ): Boolean {

        if (!isVisible) {
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                makeToast("some data is missing")
                return false
            }
        }
        if (isVisible) {
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || height.isEmpty() || weight.isEmpty()) {
                makeToast("some data is missing")
                return false
            }
        }
        if (!email.endsWith("@gmail.com")) {
            makeToast("illegal gmail")
            return false
        }
        if (password.length < 6) {
            makeToast("password must be least 6 character")
            return false
        }
        if (password != confirmPassword) {
            makeToast("Passwords do not match")
            return false
        }
        if (role == "") {
            makeToast("no role selected")
            return false
        }
        if (isVisible && (height <= 0.toString() || weight <= 0.toString())) {
            makeToast("must be positive number")
            return false
        }
        return true
    }
    private fun makeToast(message :String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun moveActivity(name: String, role: String, email: String, password: String) {
        if(role == "coach") {
            val i = Intent(this, CoachActivity::class.java)
            startActivity(i)
        } else if (role == "trainee") {
            val i = Intent(this, HealthQuizActivity::class.java)
            i.putExtra("userName", name)
            i.putExtra("userEmail", email)
            i.putExtra("userPassword", password)
            i.putExtra("numOfQuiz",numOfQuiz)
            startActivity(i)
            finish()
        } else {
            makeToast("error")
        }
    }
    private fun saveUserData(name: String, email: String, role: String, password: String) {

        val sName = name.trim()
        val sEmail = email.trim()
        val sRole = role.trim()
        val sPassword = password.trim()

        val dateTime =getTime()
        val trainingHistory :String
        val loginTimes:ArrayList<String>
        val userMap :HashMap<String,*>

        if (role == "trainee") {
            trainingHistory = "1"
            loginTimes = arrayListOf(dateTime)

            userMap = hashMapOf(
                "name" to sName,
                "email" to sEmail,
                "password" to sPassword,
                "role" to sRole,
                "trainingHistory" to trainingHistory,
                "loginTimes" to loginTimes
            )
        }
        else{
            loginTimes = arrayListOf(dateTime)
            userMap = hashMapOf(
                "name" to sName,
                "email" to sEmail,
                "password" to sPassword,
                "loginTimes" to loginTimes,
                "role" to sRole
            )
        }
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val db = Firebase.firestore

        db.collection("user").document(userId).set(userMap).addOnSuccessListener {
            nameField.text?.clear()
            emailField.text?.clear()
            makeToast("Success")
        }
    }
    private fun getTime(): String {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd-mm-yyyy HH:mm:ss")
            return currentDateTime.format(formatter)
    }
}