package fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainMaster.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import utilities.AchievementsAdapter
import utilities.SharedViewModel

class AchievementsFragment : Fragment() {

    private lateinit var achievementsRecyclerView: RecyclerView
    private lateinit var scoresList: TextView
    private lateinit var traineeName: String
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_achivments, container, false)
        achievementsRecyclerView = view.findViewById(R.id.achievementsRecyclerView)
        achievementsRecyclerView.layoutManager = LinearLayoutManager(context)
        scoresList = view.findViewById(R.id.achievements_text_view)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.traineeName.observe(viewLifecycleOwner) { newValue ->
            traineeName = newValue
        }


        loadUserAchievements()
        return view
    }

    private fun loadUserAchievements() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        db.collection("user").document(userId).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                getUserData(documentSnapshot)
            }
        }.addOnFailureListener {
            Log.e("Firestore", "Error loading user achievements", it)
        }
    }

    private fun getScores(userId: String, callback: (List<String>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("user").document(userId)

        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val scoreList = document.get("scoreList") as? List<*>
                val scores = scoreList?.filterIsInstance<String>().orEmpty()
                callback(scores)
            } else {
                callback(emptyList())
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting scores", exception)
            callback(emptyList())
        }
    }

    private fun getTraineeDataByName(
        selectedTraineeName: String,
        onResult: (Map<String, Any>?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        db.collection("user")
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                var traineeData: Map<String, Any>? = null

                for (document in querySnapshot.documents) {
                    val userName = document.getString("name")
                    if (userName == selectedTraineeName) {
                        traineeData = document.data
                        break
                    }
                }
                onResult(traineeData)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting trainee data", exception)
                onResult(null)
            }
    }

    private fun getUserData(document: DocumentSnapshot) {

        val userRole = document.data?.get("role")?.toString() ?: return

        if (userRole == "trainee") {
            val trainingHistoryStr = document.data?.get("trainingHistory")?.toString() ?: return
            val trainingHistory = trainingHistoryStr.toIntOrNull() ?: 0
            displayScores()
            updateAchievementsDisplay(trainingHistory, userRole)
        } else {
            getTraineeDataByName(traineeName) { traineeData ->
                if (traineeData != null) {
                    val scores = traineeData["scoreList"] as? List<*>
                    val trainingHistoryObj = traineeData["trainingHistory"]

                    trainingHistoryObj?.toString()?.toIntOrNull()?.let {
                        updateAchievementsDisplay(it, userRole)
                    }

                    if (scores != null) {
                        val scoreText = scores.mapIndexed { index, score ->
                            "Score number ${index + 1}: $score"
                        }.joinToString(separator = "\n")
                        Log.d("Scores", "Formatted Scores:\n$scoreText")
                        scoresList.text = scoreText
                    }
                }
            }
        }
    }

    private fun displayScores() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        getScores(userId) { scoreList ->
            val scoreText = scoreList.mapIndexed { index, score ->
                "Score number ${index + 1}: $score"
            }.joinToString(separator = "\n")
            scoresList.text = scoreText
        }
    }

    private fun updateAchievementsDisplay(trainingHistory: Int, userRole: String) {
        val milestones = listOf(
            1 to "1 training session",
            5 to "5 training sessions",
            10 to "10 training sessions",
            25 to "25 training sessions",
            100 to "100 training sessions",
            500 to "500 training sessions"
        )
        val achievements: List<Pair<String, Boolean>>

        if (userRole == "trainee") {
            achievements = milestones.map { (count, description) ->
                if (trainingHistory >= count)
                    "Congratulations! You've completed $description!" to true
                else
                    "Complete $description to unlock this achievement." to false
            }
        } else {
            achievements = milestones.map { (count, description) ->
                if (trainingHistory >= count)
                    "$traineeName completed $description!" to true
                else
                    "$traineeName needs to complete $description to unlock this achievement." to false
            }
        }
        achievementsRecyclerView.adapter = AchievementsAdapter(achievements)
    }
}
