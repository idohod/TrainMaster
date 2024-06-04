package models

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import utilities.Exercise
import utilities.ExerciseAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var addExerciseButton: ExtendedFloatingActionButton
    private lateinit var changeUserButton: ExtendedFloatingActionButton
    private lateinit var title: MaterialTextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var allExercises: ArrayList<Exercise>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViews()
        val userName =getUserName()
        val numOfQuiz = getNumOfQuiz()
        initViews()
        loadExercisesFromDb(userName)

        changeUserButton.setOnClickListener { changeUser(numOfQuiz) }
    }

    private fun getNumOfQuiz(): Int {
        val i = intent
        return i.getIntExtra("numOfQuiz", 0)
    }

    private fun changeUser(numOfQuiz: Int) {
        val intent = Intent(this, StartPage::class.java)
        intent.putExtra("numOfQuiz", numOfQuiz)
        startActivity(intent)
    }


    private fun findViews() {
        title = findViewById(R.id.title)
        changeUserButton = findViewById(R.id.change_user_button)
        addExerciseButton = findViewById(R.id.add_exercise_button)
        recyclerView = findViewById(R.id.exercises_list)

    }

    private fun initViews() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        allExercises = arrayListOf()

    }

    private fun getUserName(): String {

        val i = intent
        val userName = i.getStringExtra("userName").toString()
        "$userName's plan".also { title.text = it }
        return userName
    }

    private fun loadExercisesFromDb(userName:String) {
        val db = FirebaseDatabase.getInstance()
        val exercisesRef = db.reference.child("users").child(userName).child("exercises")

        exercisesRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    for (exerciseSnapshot in snapshot.children) {
                        val exercise = exerciseSnapshot.getValue(Exercise::class.java)
                        allExercises.add(exercise!!)
                    }
                    exerciseAdapter = ExerciseAdapter(allExercises)
                    recyclerView.adapter = exerciseAdapter
                    exerciseAdapter.setOnItemClickListener(object :
                        ExerciseAdapter.OnItemClickListener {
                        override fun itemClick(exercise: Exercise) {
                            moveToTimerActivity(exercise,userName)
                        }

                        override fun update(exercise: Exercise, increase: Boolean) {
                            updateExerciseLevel(exercise, increase,userName)
                        }

                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateExerciseLevel(exercise: Exercise, increase: Boolean,userName:String) {

        val firestore = FirebaseFirestore.getInstance()
        val exercisesCollection = firestore.collection("exercises")

        val database = FirebaseDatabase.getInstance()
        val exercisesRef = database.reference.child("users").child(userName).child("exercises")

        val oldLevel = exercise.level
        val newLevel = setNewLevel(oldLevel, increase)
        if (newLevel == 0L)
            return

        val type = exercise.type

        val exToUpdate = ArrayList<Exercise>()

        exercisesCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    val curLevel = document.getLong("difficult_level")
                    val curType = document.getLong("type")
                    val name = document.getString("exercise_name")

                    if (curLevel != null && name != null && curType != null)
                        if (curLevel == newLevel && curType == type) {
                            val newExercise = Exercise(name, "3", "10", "0", type, newLevel)
                            exToUpdate.add(newExercise)
                        }
                }
                updateList(exToUpdate, exercise)
                exercisesRef.setValue(allExercises)
            }
            .addOnFailureListener { exception ->
                Log.w("debug", "Error getting documents.", exception)
            }
    }

    private fun setNewLevel(oldLevel: Long?, increase: Boolean): Long {
        if (oldLevel != null) {

            if (increase && oldLevel < 5L)
                return oldLevel.plus(1)
            else if (increase && oldLevel == 5L) {
                Toast.makeText(this, "max level!", Toast.LENGTH_SHORT).show()

                return 0
            } else if (!increase && oldLevel > 1L)
                return oldLevel.minus(1)
            else if (!increase && oldLevel == 1L) {
                Toast.makeText(this, "min level!", Toast.LENGTH_SHORT).show()
                return 0
            }
        }
        return 0
    }


    private fun updateList(exToUpdate: ArrayList<Exercise>, exercise: Exercise) {
        if(exToUpdate.isNotEmpty()) {
            val ranIndex = mutableListOf<Int>()
            for (e in exToUpdate)
                ranIndex.add(exToUpdate.indexOf(e))

            val randomIndex = ranIndex.random()
            val newExercise = exToUpdate[randomIndex]
            val i = allExercises.indexOf(exercise)
            allExercises.remove(exercise)
            allExercises.add(i, newExercise)

        }
    }
    private fun  moveToTimerActivity(exercise: Exercise,userName:String){
        val intent = Intent(this, TimerActivity::class.java)

        intent.putExtra("exName",exercise.name)
        intent.putExtra("exSet",exercise.numOfSets)
        intent.putExtra("exRep",exercise.numOfReps)
        intent.putExtra("exWeight",exercise.weight)

        intent.putExtra("userName",userName)
        startActivity(intent)
        finish()
    }
}


