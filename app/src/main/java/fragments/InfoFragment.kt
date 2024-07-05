package fragments

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

    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userPassword: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

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
        }
            .addOnFailureListener { exception ->Log.w("TAG","Error getting documents.",exception)}
    }
    private fun getUserData(it: DocumentSnapshot) {
        userName.text = it.data?.get("name")?.toString() ?: return
        userEmail.text = it.data?.get("email")?.toString() ?: return
        userPassword.text = it.data?.get("password")?.toString() ?: return
    }
    private fun findViews(view: View) {
        yourName = view.findViewById(R.id.fragment_user_name)
        yourEmail = view.findViewById(R.id.fragment_user_email)
        yourPassword = view.findViewById(R.id.fragment_user_password)

        userName = view.findViewById(R.id.fragment_the_user_name)
        userEmail = view.findViewById(R.id.fragment_the_user_email)
        userPassword = view.findViewById(R.id.fragment_the_user_password)
    }
}