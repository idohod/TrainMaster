package fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.trainMaster.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import utilities.SharedViewModel

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
    private lateinit var traineeName:String
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_info, container, false)
        findViews(view)
        initValues()
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.traineeName.observe(viewLifecycleOwner) { newValue ->
            traineeName = newValue
        }
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

        loginTimes(it)

        when (val userRole = it.data?.get("role")?.toString()) {
            "trainee" -> {
                calculateHighestScore(it, userRole)
            }
            "coach" -> {
                getTraineeData(userRole)
            }
            else -> "No trainee assigned".also { userHighestScore.text = it }
        }
    }

    private fun getTraineeData(userRole: String) {
        getTraineeIdByName(traineeName) { traineeId ->
            if (traineeId.isNotEmpty()) {
                val db = FirebaseFirestore.getInstance()
                val userDocRef = db.collection("user").document(traineeId)
                userDocRef.get().addOnSuccessListener { traineeDoc ->
                    calculateHighestScore(traineeDoc,userRole)
                }.addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching trainee document: ", exception)
                    "Failed to fetch trainee data".also { userHighestScore.text = it }
                }
            } else
                "Trainee not found".also { userHighestScore.text = it }
        }
    }

    private fun loginTimes(it: DocumentSnapshot) {
        val loginTimes = it.data?.get("loginTimes") as? List<*>
        if (loginTimes != null && loginTimes.isNotEmpty())
            userFirstLogin.text = loginTimes[0]?.toString() ?: "No login time recorded"
         else
            "No login times available".also { userFirstLogin.text = it }
    }

    private fun calculateHighestScore(doc: DocumentSnapshot, userRole: String) {
        val scoreList = doc.data?.get("scoreList") as? List<*>

        if (scoreList != null && scoreList.isNotEmpty() ) {
            val scores = scoreList.mapNotNull { it.toString().toIntOrNull() }
            val maxScore = scores.maxOrNull()
            if (userRole == "coach")
                "$traineeName's Highest Score:".also { yourHighestScore.text = it }

            if (maxScore != null) {
                userHighestScore.text = maxScore.toString()
            } else {
                "Invalid or empty score list".also { userHighestScore.text = it }
            }
        } else {
            "No scores available".also { userHighestScore.text = it }
        }
    }
    private fun getTraineeIdByName(name: String, callback: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("user")
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val traineeId = document.id
                    callback(traineeId)
                    return@addOnSuccessListener
                }
                callback("")
            }
            .addOnFailureListener {callback("") }
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