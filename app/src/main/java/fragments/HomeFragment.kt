package fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainMaster.R
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import utilities.SharedViewModel
import models.TimerActivity
import utilities.Exercise
import utilities.ExerciseAdapter

class HomeFragment : Fragment() {

    private lateinit var title: MaterialTextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var allExercises: ArrayList<Exercise>
    private var isUpdate = false
    private lateinit var userName:String
    private lateinit var userRole:String
    private lateinit var traineeName:String
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initViews(view)
        initValues()

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.traineeName.observe(viewLifecycleOwner, Observer { newValue ->
            traineeName = newValue
        })
    }
    private fun initValues() {
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
        userRole= it.data?.get("role")?.toString() ?: return

        if (userRole == "trainee") {
            userName = it.data?.get("name")?.toString() ?: return
            "$userName's plan".also { title.text = it }
            loadExercisesFromDb(userName)
        }else{
            "$traineeName's plan".also { title.text = it }
            loadExercisesFromDb(traineeName)

        }
    }

    private fun initViews(view :View) {
        title = view.findViewById(R.id.title)
        recyclerView = view.findViewById(R.id.exercises_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        allExercises = arrayListOf()
    }


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
                if (userRole == "trainee")
                    moveToTimerActivity(exercise)
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
               Toast.makeText(context, "max level!", Toast.LENGTH_SHORT).show()
                return 0
            }
        } else {
            if (oldLevel > 1L)
                return oldLevel.minus(1)
            else {
                Toast.makeText(context, "min level!", Toast.LENGTH_SHORT).show()
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
    private fun moveToTimerActivity(
        exercise: Exercise

    ) {
        val intent = Intent(context, TimerActivity::class.java)

        intent.putExtra("exName", exercise.name)
        intent.putExtra("exSet", exercise.numOfSets)
        intent.putExtra("exRep", exercise.numOfReps)
        intent.putExtra("exWeight", exercise.weight)
        startActivity(intent)
    }

}