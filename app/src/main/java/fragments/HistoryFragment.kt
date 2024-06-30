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
import com.google.firebase.firestore.FirebaseFirestore
class HistoryFragment : Fragment() {
    private lateinit var historyNumber: TextView
    private lateinit var historyText: TextView
    private lateinit var dates:TextView
    private lateinit var trainingList :TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_training_history, container, false)
        findViews(view)
        getHistoryNumber()
        displayDates()

        return view
    }
    private fun displayDates() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        getLoginTimes(userId) { loginTimes ->
            val allLoginTimes = loginTimes.joinToString(separator = "\n")
            Log.d("dateAndTime", allLoginTimes)
            dates.text = allLoginTimes
        }
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
        dates = view.findViewById(R.id.date)
        trainingList = view.findViewById(R.id.training_list)
    }
    private fun getLoginTimes(userId: String, callback: (List<String>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("user").document(userId)

        userDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val loginTimes = document.get("loginTimes") as? List<*> ?: emptyList<Any>()
                    callback(loginTimes.filterIsInstance<String>())
                } else
                    callback(emptyList())
            }
            .addOnFailureListener {callback(emptyList()) }
    }
}