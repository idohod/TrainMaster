package com.example.finalproject
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

class ExerciseAdapter(private val exerciseList: ArrayList<Exercise>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {
    private lateinit var  exerciseCallback: ExerciseCallback

    fun setExerciseCallback(exerciseCallback: ExerciseCallback?) {
        if (exerciseCallback != null)
            this.exerciseCallback = exerciseCallback

        else {
            Log.d("debug", "exerciseCallback is null")

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.exercise_item, parent, false)
        return ExerciseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise: Exercise = exerciseList[position]

        holder.theName.text = exercise.name
        holder.theSet.text = exercise.numOfSets
        holder.theRepetitions.text = exercise.numOfReps
        holder.theWeight.text = exercise.weight
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    fun getItem(position: Int): Exercise {
        return exerciseList[position]
    }

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            // Set a click listener on the itemView (the root view of the item layout)
            itemView.setOnClickListener {
                // Retrieve the adapter position of the clicked item using adapterPosition
                val position = adapterPosition
                // Ensure that a valid position is obtained
                if (position != RecyclerView.NO_POSITION) {
                 //   this.setExerciseCallback(exerciseCallback)
                    exerciseCallback.itemClick(getItem(position), position)
                }
            }
        }



        val exerciseNameText: MaterialTextView = itemView.findViewById(R.id.exercise_name)
        val theName: MaterialTextView = itemView.findViewById(R.id.the_name)

        val setText: MaterialTextView = itemView.findViewById(R.id.set_text)
        val theSet: MaterialTextView = itemView.findViewById(R.id.set_number)

        val repetitionsText: MaterialTextView = itemView.findViewById(R.id.repetitions_text)
        val theRepetitions: MaterialTextView = itemView.findViewById(R.id.repetitions_number)

        val weightText: MaterialTextView = itemView.findViewById(R.id.weight_text)
        val theWeight: MaterialTextView = itemView.findViewById(R.id.weight_number)

    }
}

