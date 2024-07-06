package fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.trainMaster.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InfoFragment : Fragment() {

    private lateinit var yourName: TextView
    private lateinit var yourEmail: TextView
    private lateinit var yourPassword: TextView
    private lateinit var yourFirstLogin: TextView
    private lateinit var yourHighestScore: TextView

    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userPassword: TextView
    private lateinit var userFirstLogin: TextView
    private lateinit var userHighestScore: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_info, container, false)
        findViews(view)
        initValues()
        return view
    }

    private fun initValues() {
        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("user").document(userId)

        ref.get().addOnSuccessListener {
            if (it != null)
                getUserData(it)
        }.addOnFailureListener { exception ->
            Log.w("TAG", "Error getting documents.", exception)
        }
    }


    private fun getUserData(it: DocumentSnapshot) {
        userName.text = it.data?.get("name")?.toString() ?: return
        userEmail.text = it.data?.get("email")?.toString() ?: return
        userPassword.text = it.data?.get("password")?.toString() ?: return

        // Safely retrieve the first login time from the 'loginTimes' array
        val loginTimes = it.data?.get("loginTimes") as? List<*>
        if (loginTimes != null && loginTimes.isNotEmpty()) {
            userFirstLogin.text = loginTimes[0]?.toString() ?: "No login time recorded"
        } else {
            userFirstLogin.text = "No login times available"
        }

        // Handling the scoreList to find the maximum score
        val scoreList = it.data?.get("scoreList") as? List<*>
        if (scoreList != null && scoreList.isNotEmpty()) {
            val scores = scoreList.mapNotNull { it.toString().toIntOrNull() }
            val maxScore = scores.maxOrNull()
            if (maxScore != null) {
                userHighestScore.text = maxScore.toString()
            } else {
                userHighestScore.text = "Invalid or empty score list"
            }
        } else {
            userHighestScore.text = "No scores available"
        }
    }


    private fun findViews(view: View) {
        yourName = view.findViewById(R.id.fragment_user_name)
        yourEmail = view.findViewById(R.id.fragment_user_email)
        yourPassword = view.findViewById(R.id.fragment_user_password)
        yourFirstLogin = view.findViewById(R.id.fragment_user_first_login)
        yourHighestScore = view.findViewById(R.id.fragment_user_highest_score)

        userName = view.findViewById(R.id.fragment_the_user_name)
        userEmail = view.findViewById(R.id.fragment_the_user_email)
        userPassword = view.findViewById(R.id.fragment_the_user_password)
        userFirstLogin = view.findViewById(R.id.fragment_the_user_first_login)
        userHighestScore = view.findViewById(R.id.fragment_the_user_highest_score)
    }
}
