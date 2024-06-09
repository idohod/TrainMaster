package fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.google.android.gms.auth.api.identity.SignInPassword
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import models.SharedViewModel
import models.StartPage
import models.TimerActivity
import utilities.Exercise
import utilities.ExerciseAdapter

class HomeFragment : Fragment() {
    private lateinit var addExerciseButton: ExtendedFloatingActionButton
    private lateinit var changeUserButton: ExtendedFloatingActionButton
    private lateinit var title: MaterialTextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var allExercises: ArrayList<Exercise>
    private var isUpdate = false
    private lateinit var sharedViewModel: SharedViewModel

    companion object {
        private const val ARG_USER_NAME = "name"
        private const val ARG_USER_EMAIL = "email"
        private const val ARG_USER_PASSWORD = "password"

        fun newInstance(userName: String, userEmail: String,userPassword: String): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(ARG_USER_NAME, userName)
            args.putString(ARG_USER_EMAIL, userEmail)
            args.putString(ARG_USER_PASSWORD, userPassword)

            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initViews(view)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        val userName = arguments?.getString(ARG_USER_NAME)
        val userEmail = arguments?.getString(ARG_USER_EMAIL)
        val userPassword = arguments?.getString(ARG_USER_PASSWORD)

        setValues(userEmail,userPassword,userName)

        return view
    }

    private fun setValues(userEmail: String?, userPassword: String?, userName: String?) {
        if (userEmail!=null && userPassword!=null){
            sharedViewModel.userEmail.value = userEmail
            sharedViewModel.userPassword.value = userPassword
        }

        if (userName != null ) {
            sharedViewModel.homeToInfoUserName.value = userName
            "$userName's plan".also { title.text = it }
            loadExercisesFromDb(userName)
        }

        if(userName == null) {
            sharedViewModel.infoToHomeUserName.observe(viewLifecycleOwner) { data ->
                "$data's plan".also { title.text = it }
                loadExercisesFromDb(data)
            }
        }
    }
    // val numOfQuiz = getNumOfQuiz()

   /*
    private fun getNumOfQuiz(): Int {
        val i = intent
        return i.getIntExtra("numOfQuiz", 0)
    }
    private fun changeUser(numOfQuiz: Int) {
        val intent = Intent(this, StartPage::class.java)
        intent.putExtra("numOfQuiz", numOfQuiz)
        startActivity(intent)
    }

    */


    private fun initViews(view :View) {
        title = view.findViewById(R.id.title)
        recyclerView = view.findViewById(R.id.exercises_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        allExercises = arrayListOf()
    }
   /* private fun getUserName(): String {
        val i = intent
        val userName = i.getStringExtra("userName").toString()
        "$userName's plan".also { title.text = it }
        return userName
    }*/
    /*private fun removeExercisesFromDb(userName: String) {
        val db = FirebaseDatabase.getInstance()
        val exercisesRef = db.reference.child("users").child(userName).child("exercises")

        exercisesRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    for (exerciseSnapshot in snapshot.children) {
                        val exercise = exerciseSnapshot.getValue(Exercise::class.java)
                        allExercises.remove(exercise!!)
                    }
                    exercisesRef.setValue(allExercises)

                    exerciseAdapter = ExerciseAdapter(allExercises)
                    recyclerView.adapter = exerciseAdapter
                    exerciseAdapter.setOnItemClickListener(object :
                        ExerciseAdapter.OnItemClickListener {
                        override fun itemClick(exercise: Exercise) {
                            //  moveToTimerActivity(exercise,userName)
                        }

                        override fun update(exercise: Exercise, increase: Boolean) {
                            //  updateExerciseLevel(exercise, increase,userName)
                        }
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }*/
    private fun loadExercisesFromDb(userName: String) {
        val db = FirebaseDatabase.getInstance()
        val exercisesRef = db.reference.child("users").child(userName).child("exercises")
        exercisesRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (!isUpdate)
                        for (exerciseSnapshot in snapshot.children) {
                            val exercise = exerciseSnapshot.getValue(Exercise::class.java)
                            allExercises.add(exercise!!)
                        }
                    setAdapter(userName)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun setAdapter(userName: String) {
        exerciseAdapter = ExerciseAdapter(allExercises)
        recyclerView.adapter = exerciseAdapter
        exerciseAdapter.setOnItemClickListener(object :
            ExerciseAdapter.OnItemClickListener {
            override fun itemClick(exercise: Exercise) {
            //    moveToTimerActivity(exercise, userName)
            }
            override fun update(exercise: Exercise, position: Int, increase: Boolean) {
                updateExerciseLevel(exercise,position, increase, userName)
            }
        })
    }
    private fun updateExerciseLevel(exercise: Exercise,position: Int,increase: Boolean,userName: String) {

        val firestore = FirebaseFirestore.getInstance()
        val exercisesCollection = firestore.collection("exercises")
        val database = FirebaseDatabase.getInstance()
        val exercisesRef = database.reference.child("users").child(userName).child("exercises")

        val oldLevel = exercise.level ?:return
        val newLevel = setNewLevel(oldLevel, increase)

        if (newLevel == 0L)
            return
        val type = exercise.type ?: return
        val exToUpdate = ArrayList<Exercise>()

        exercisesCollection.get()
            .addOnSuccessListener { result ->
                for (document in result)
                    getExercises(document,newLevel,type,exercise,increase,exToUpdate)
                updateList(exToUpdate, exercise,position)
                exercisesRef.setValue(allExercises)
            }
            .addOnFailureListener {}
    }
    private fun getExercises(
        document: QueryDocumentSnapshot, newLevel: Long, type: Long,
        exercise: Exercise, increase: Boolean, exToUpdate: ArrayList<Exercise>) {

        val curLevel = document.getLong("difficult_level") ?: return
        val curType = document.getLong("type") ?: return
        val name = document.getString("exercise_name") ?: return

        if (curLevel == newLevel && curType == type) {
            val newExercise = setNewExercise(exercise, name, curType, curLevel, increase)
            exToUpdate.add(newExercise)
        }
    }
    private fun setNewExercise(oldExercise: Exercise,name: String,type: Long,newLevel: Long,increase: Boolean): Exercise {
        val newReps: Int; val newSets: Int; val newWeight: Int

        if (increase) {

            if (newLevel < 4L) {
                newReps = oldExercise.numOfReps?.toInt()?.plus(2)!!
                newSets = oldExercise.numOfSets?.toInt()!!
                newWeight = oldExercise.weight?.toInt()!!
                return Exercise(name,newSets.toString(),newReps.toString(),newWeight.toString(),type,newLevel)
            }
            else if (newLevel == 4L) {
                newReps = oldExercise.numOfReps?.toInt()!!
                newSets = oldExercise.numOfSets?.toInt()?.plus(1)!!
                newWeight = oldExercise.weight?.toInt()!!
                return Exercise(name,newSets.toString(),newReps.toString(),newWeight.toString(),type,newLevel)
            }
            return Exercise(name,oldExercise.numOfSets,oldExercise.numOfReps,oldExercise.weight,type,oldExercise.level?.plus(1))

        } else {
            if (newLevel < 3L) {
                newReps = oldExercise.numOfReps?.toInt()?.minus(2)!!
                newSets = oldExercise.numOfSets?.toInt()!!
                newWeight = oldExercise.weight?.toInt()!!
                return Exercise(name,newSets.toString(),newReps.toString(),newWeight.toString(),type,newLevel)
            }
            else if (newLevel == 3L) {
                newReps = oldExercise.numOfReps?.toInt()!!
                newSets = oldExercise.numOfSets?.toInt()?.minus(1)!!
                newWeight = oldExercise.weight?.toInt()!!
                return Exercise(name,newSets.toString(),newReps.toString(),newWeight.toString(),type,newLevel)
            }
            return Exercise(name,oldExercise.numOfSets,oldExercise.numOfReps,oldExercise.weight,type,oldExercise.level?.minus(1))
        }
    }
    private fun setNewLevel(oldLevel: Long, increase: Boolean): Long {
        if (increase) {
            if (oldLevel <= 4L)
                return oldLevel.plus(1)
            else {
             //   Toast.makeText(this, "max level!", Toast.LENGTH_SHORT).show()
                return 0
            }
        } else {
            if (oldLevel > 1L)
                return oldLevel.minus(1)
            else {
              //  Toast.makeText(this, "min level!", Toast.LENGTH_SHORT).show()
                return 0
            }
        }
    }
    private fun updateList(exToUpdate: ArrayList<Exercise>, exercise: Exercise, position: Int) {

        if (exToUpdate.isNotEmpty()) {
            val ranIndex = mutableListOf<Int>()
            for (e in exToUpdate)
                ranIndex.add(exToUpdate.indexOf(e))

            val randomIndex = ranIndex.random()
            val newExercise = exToUpdate[randomIndex]

            allExercises.remove(exercise)
            allExercises.add(position, newExercise)
            isUpdate = true
        }
    }
  /*  private fun moveToTimerActivity(exercise: Exercise, userName: String) {
        val intent = Intent(this, TimerActivity::class.java)

        intent.putExtra("exName", exercise.name)
        intent.putExtra("exSet", exercise.numOfSets)
        intent.putExtra("exRep", exercise.numOfReps)
        intent.putExtra("exWeight", exercise.weight)

        intent.putExtra("userName", userName)
        startActivity(intent)
        finish()
    }*/

}