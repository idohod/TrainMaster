package models

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log

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
    private lateinit var userName: String

    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var allExercises: ArrayList<Exercise>

    private var numOfQuiz=0
    //  private var backgroundImage: AppCompatImageView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViews()
        getUserName()
        getNumOfQuiz()
        initViews()
        loadExercisesFromDb()

        changeUserButton.setOnClickListener{changeUser()}
    }

    private fun getNumOfQuiz() {
        val i = intent
        numOfQuiz = i.getIntExtra("numOfQuiz",0)
        Log.d("numOfQuiz","main numOfQuiz=$numOfQuiz")

    }

    private fun changeUser() {
        val intent = Intent(this, StartPage::class.java)
        intent.putExtra("numOfQuiz",numOfQuiz)
        startActivity(intent)
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

                        override fun increase(exercise: Exercise) {
                            increaseExerciseLevel(exercise)
                        }

                        override fun decrease(exercise: Exercise) {
                            decreaseExerciseLevel(exercise)
                        }
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun increaseExerciseLevel(exercise: Exercise) {

        val firestore = FirebaseFirestore.getInstance()
        val exercisesCollection = firestore.collection("exercises")

        val database = FirebaseDatabase.getInstance()
        val exercisesRef = database.reference.child("users").child(userName).child("exercises")

        val oldLevel = exercise.level
        val newLevel = oldLevel?.plus(1)
        val type = exercise.type

        val exToUpdate = ArrayList<Exercise>()

        exercisesCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    val curLevel = document.getLong("difficult_level")
                    val curType = document.getLong("type")
                    val name = document.getString("exercise_name")

                    if (curLevel != null && name != null && curType != null)
                        if(curLevel == newLevel && curType == type){
                            val newExercise = Exercise(name, "3", "10", "0", type, newLevel)
                            val i = allExercises.indexOf(exercise)
                            exToUpdate.add(newExercise)
                        }


                }
                updateList(exToUpdate,exercise)
                exercisesRef.setValue(allExercises)


            }
            .addOnFailureListener { exception ->
                Log.w("debug", "Error getting documents.", exception)
            }

    }

    private fun updateList(exToUpdate: ArrayList<Exercise>, exercise: Exercise) {
        val ranIndex = mutableListOf<Int>()
        for(e in exToUpdate)
            ranIndex.add(exToUpdate.indexOf(e))

        val randomIndex = ranIndex.random()
        val newExercise = exToUpdate[randomIndex]
        val i = allExercises.indexOf(exercise)
        allExercises.remove(exercise)
        allExercises.add(i,newExercise)


    }

    private fun decreaseExerciseLevel(exercise: Exercise) {

    }

    private fun  moveToTimerActivity(exercise: Exercise){
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