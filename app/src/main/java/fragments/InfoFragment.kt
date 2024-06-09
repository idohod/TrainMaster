package fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.finalproject.R
import com.google.android.gms.auth.api.identity.SignInPassword
import models.SharedViewModel

class InfoFragment : Fragment() {

    private lateinit var yourName:TextView
    private lateinit var yourEmail:TextView
    private lateinit var yourPassword:TextView

    private lateinit var userName:TextView
    private lateinit var userEmail:TextView
    private lateinit var userPassword:TextView

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_info, container, false)

        findViews(view)
        initData()

        return view
    }


    override fun onPause() {
        super.onPause()
        val name = userName.text.toString()
        Log.d("userName","onPause $name")
        sharedViewModel.infoToHomeUserName.value = name
    }
    private fun initData() {
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        sharedViewModel.homeToInfoUserName.observe(viewLifecycleOwner) { data ->
            userName.text = data
        }
        sharedViewModel.userEmail.observe(viewLifecycleOwner) { data ->
            userEmail.text = data
        }
        sharedViewModel.userPassword.observe(viewLifecycleOwner) { data ->
            userPassword.text = data
        }
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