package fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HistoryFragment : Fragment() {
    private lateinit var historyNumber: TextView
    private lateinit var historyText: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_training_history, container, false)
        findViews(view)
        getHistoryNumber()

        return view
    }

    private fun getHistoryNumber() {
        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("user").document(userId)

        ref.get().addOnSuccessListener {
            if (it != null)
                getUserData(it)
        }
            .addOnFailureListener {exception -> Log.w("TAG", "Error getting documents.", exception)}
    }
    private fun getUserData(it: DocumentSnapshot) {
        val trainingHistory = it.data?.get("trainingHistory")?.toString() ?: return
        historyNumber.text = trainingHistory

    }
    private fun findViews(view: View) {
        historyText = view.findViewById(R.id.fragment_training_history)
        historyNumber = view.findViewById(R.id.fragment_training_history_number)
    }
}