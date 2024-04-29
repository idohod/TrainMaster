package com.example.finalproject

import android.os.Bundle
import android.util.Log
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
        Log.d("debug","findViews")
        getUserName()
        Log.d("debug","getUserName")
      //  allExercises = ExerciseList()
        loadExercisesFromDb()
        Log.d("debug","loadExercisesFromDb")

    }

    private fun findViews() {
        // backgroundImage = findViewById(R.id.background)
        title = findViewById(R.id.title)
        changeUserButton = findViewById(R.id.change_user_button)
        addExerciseButton = findViewById(R.id.add_exercise_button)

        recyclerView = findViewById(R.id.exercises_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        allExercises = arrayListOf<Exercise>()

    }
private fun getUserName(){
    val i =intent

    userName = i.getStringExtra("userName").toString()
}


    private fun loadExercisesFromDb() {
        val db = FirebaseDatabase.getInstance()
        val exercisesRef = db.reference.child("users").child(userName).child("exercises")

        Log.d("debug",exercisesRef.toString())
        exercisesRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    for (exerciseSnapshot in snapshot.children) {
                        val exercise = exerciseSnapshot.getValue(Exercise::class.java)
                        allExercises.add(exercise!!)
                    }
                    recyclerView.adapter = ExerciseAdapter(allExercises)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ALL",error.message)
            }
        })
    }

}