package com.example.finalproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var addExerciseButton: ExtendedFloatingActionButton
    private lateinit var changeUserButton: ExtendedFloatingActionButton
    private lateinit var title: MaterialTextView
    private lateinit var userName: String

    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var allExercises: ArrayList<Exercise>
    //  private var backgroundImage: AppCompatImageView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViews()
        getUserName()
        initViews()
        loadExercisesFromDb()
    }

    private fun findViews() {
        // backgroundImage = findViewById(R.id.background)
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

    @SuppressLint("SetTextI18n")
    private fun getUserName() {

        val i = intent
        userName = i.getStringExtra("userName").toString()
        title.text = "$userName's plan"
    }

    private fun loadExercisesFromDb() {
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
                            moveToTimerActivity(exercise)
                        }
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun  moveToTimerActivity(exercise: Exercise){
        val intent = Intent(applicationContext, TimerActivity::class.java)

        intent.putExtra("exName",exercise.name)
        intent.putExtra("exSet",exercise.numOfSets)
        intent.putExtra("exRep",exercise.numOfReps)
        intent.putExtra("exWeight",exercise.weight)

        intent.putExtra("userName",userName)
        startActivity(intent)
        finish()
    }
}