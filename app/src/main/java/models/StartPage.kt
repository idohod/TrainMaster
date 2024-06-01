package models

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.R
import com.google.android.material.button.MaterialButton

class StartPage : AppCompatActivity() {

    private lateinit var registerButton: MaterialButton
    private lateinit var signInButton: MaterialButton
    private var numOfQuiz =0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_page)
        getNumOfQuiz()
        registerButton = findViewById(R.id.register_button)
        signInButton = findViewById(R.id.sign_in_button)

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("numOfQuiz",numOfQuiz)
            startActivity(intent)
        }

        signInButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

    }
    private fun getNumOfQuiz(){
        val i = intent
        numOfQuiz =i.getIntExtra("numOfQuiz",0)
        Log.d("numOfQuiz","start numOfQuiz=$numOfQuiz")

    }
}
