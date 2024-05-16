package models

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.R
import com.google.firebase.firestore.FirebaseFirestore

class CoachActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var userNameButton: Button
    private lateinit var title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coach)
        findViews()
        getUserNamesFromDB()

        userNameButton.setOnClickListener { selectUser() }
    }

    private fun selectUser() {
        val checkedRadioButtonId = radioGroup.checkedRadioButtonId
        if (checkedRadioButtonId != -1) {
            val radioButton = radioGroup.findViewById<RadioButton>(checkedRadioButtonId)
            val selectedUserName = radioButton.text.toString()
            moveToMainActivity(selectedUserName)

        } else {
            Toast.makeText(this, "no user selected", Toast.LENGTH_SHORT).show()
            return

        }
    }

    private fun moveToMainActivity(selectedUserName: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userName", selectedUserName)
        startActivity(intent)
    }

    private fun getUserNamesFromDB() {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("user")

        usersCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val role = document.getString("role")
                    val name = document.getString("name")
                    if (role != null && role != "coach" && name != null) {
                        addRadioButtonToRadioGroup(name)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
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
}
