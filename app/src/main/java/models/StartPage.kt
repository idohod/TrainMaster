package models

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.trainMaster.R
import com.google.android.material.button.MaterialButton
class StartPage : AppCompatActivity() {

    private lateinit var registerButton: MaterialButton
    private lateinit var signInButton: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_page)

        val numOfQuiz = getNumOfQuiz()
        findViews()

        registerButton.setOnClickListener { moveToRegisterActivity(numOfQuiz) }
        signInButton.setOnClickListener { moveToSignInActivity() }
    }
    private fun moveToSignInActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun moveToRegisterActivity(numOfQuiz: Int) {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.putExtra("numOfQuiz",numOfQuiz)

        startActivity(intent)
        finish()
    }
    private fun findViews() {
        registerButton = findViewById(R.id.register_button)
        signInButton = findViewById(R.id.sign_in_button)
    }
    private fun getNumOfQuiz(): Int {
        return intent.getIntExtra("numOfQuiz", 0)
    }
}