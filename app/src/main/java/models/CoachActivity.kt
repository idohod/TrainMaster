package models

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.trainMaster.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
class CoachActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var userNameButton: Button
    private lateinit var title: TextView
    private var numOfQuiz: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coach)
        findViews()
        getNumOfQuiz()
        getUserNamesFromDB()

        userNameButton.setOnClickListener { selectUser() }
    }
    private fun getNumOfQuiz() {
        numOfQuiz = intent.getIntExtra("numOfQuiz", 0)
    }
    private fun selectUser() {
        val checkedRadioButtonId = radioGroup.checkedRadioButtonId
        if (checkedRadioButtonId != -1) {
            val radioButton = radioGroup.findViewById<RadioButton>(checkedRadioButtonId)
            val selectedUserName = radioButton.text.toString()
            moveToMenuActivity(selectedUserName)
        } else {
            Toast.makeText(this, "no user selected", Toast.LENGTH_SHORT).show()
            return
        }
    }
    private fun moveToMenuActivity(selectedUserName: String) {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra("userName", selectedUserName)
        intent.putExtra("isCoach", true)
        intent.putExtra("numOfQuiz",numOfQuiz)

        startActivity(intent)
        finish()
    }
    private fun getUserNamesFromDB() {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("user")
        usersCollection.get()
            .addOnSuccessListener { result ->
                for (document in result)
                    getUsers(document)
            }
            .addOnFailureListener { exception ->Log.w("TAG", "Error getting documents.", exception)}
    }
    private fun getUsers(document: QueryDocumentSnapshot) {
        val role = document.getString("role")?:return
        val name = document.getString("name")?:return
        if (role != "coach")
            addRadioButtonToRadioGroup(name)
    }
    private fun findViews() {
        radioGroup = findViewById(R.id.radioGroup)
        userNameButton = findViewById(R.id.button)
        title = findViewById(R.id.titleTextView)
    }
    private fun addRadioButtonToRadioGroup(name: String) {
        val radioButton = RadioButton(this)
        radioButton.text = name
        radioButton.id = View.generateViewId()
        radioGroup.addView(radioButton)
    }

    override fun onBackPressed() {
        startActivity(Intent(this,StartPage::class.java))
        finish()
    }
}