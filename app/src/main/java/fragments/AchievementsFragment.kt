package fragments

import AchievementsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainMaster.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AchievementsFragment : Fragment() {

    private lateinit var achievementsRecyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_share, container, false)
        achievementsRecyclerView = view.findViewById(R.id.achievementsRecyclerView)
        achievementsRecyclerView.layoutManager = LinearLayoutManager(context)
        loadUserAchievements()
        return view
    }

    private fun loadUserAchievements() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        db.collection("user").document(userId).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val trainingHistory = documentSnapshot.getString("trainingHistory")?.toInt() ?: 0
                updateAchievementsDisplay(trainingHistory)
            }
        }.addOnFailureListener {
            // Handle the failure e.g., by displaying a Toast or updating a TextView
        }
    }

    private fun updateAchievementsDisplay(trainingHistory: Int) {
        val milestones = listOf(
            1 to "1 training session",
            5 to "5 training sessions",
            10 to "10 training sessions",
            25 to "25 training sessions",
            100 to "100 training sessions",
            500 to "500 training sessions"
        )

        val achievements = milestones.map { (count, description) ->
            if (trainingHistory >= count) {
                "Congratulations! You've completed $description!"
            } else {
                "Complete $description to unlock this achievement."
            }
        }

        achievementsRecyclerView.adapter = AchievementsAdapter(achievements)
    }
}
