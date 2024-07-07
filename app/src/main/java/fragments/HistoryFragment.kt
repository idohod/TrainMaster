package fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainMaster.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import utilities.SharedViewModel
import utilities.TrainingTimesAdapter

class HistoryFragment : Fragment() {
    private lateinit var historyNumber: TextView
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var traineeName: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingTimesAdapter: TrainingTimesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_training_history, container, false)
        findViews(view)
        getHistoryNumber()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.traineeName.observe(viewLifecycleOwner) { newValue ->
            traineeName = newValue
        }
    }

    private fun displayDates() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        getLoginTimes(userId) { loginTimes ->
            trainingTimesAdapter = TrainingTimesAdapter(loginTimes)
            recyclerView.adapter = trainingTimesAdapter
        }
    }

    private fun getHistoryNumber() {
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
        val userRole = it.data?.get("role")?.toString() ?: return

        if (userRole == "trainee") {
            val trainingHistory = it.data?.get("trainingHistory")?.toString() ?: return
            historyNumber.text = trainingHistory
            displayDates()
        } else {
            getTraineeDataByName(traineeName) { traineeData ->
                if (traineeData != null) {
                    historyNumber.text = traineeData["trainingHistory"].toString()
                    val loginTimes = traineeData["loginTimes"] as? List<*>
                    if (loginTimes != null) {
                        val formattedDates = loginTimes.filterIsInstance<String>()
                        trainingTimesAdapter = TrainingTimesAdapter(formattedDates)
                        recyclerView.adapter = trainingTimesAdapter
                    }
                }
            }
        }
    }

    private fun getTraineeDataByName(selectedTraineeName: String, onResult: (Map<String, Any>?) -> Unit) {
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
            .addOnFailureListener { onResult(null) }
    }

    private fun findViews(view: View) {
        historyNumber = view.findViewById(R.id.fragment_training_history_number)
        recyclerView = view.findViewById(R.id.recycler_view_training_times)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
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
        }.addOnFailureListener { callback(emptyList()) }
    }
}
