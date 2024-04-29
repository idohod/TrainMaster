package com.example.finalproject
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

class ExerciseAdapter(private val exerciseList: ArrayList<Exercise>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {
    private var exerciseCallback: ExerciseCallback? = null

    fun setExerciseCallback(exerciseCallback: ExerciseCallback?) {
        this.exerciseCallback = exerciseCallback
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

        val exerciseNameText: MaterialTextView = itemView.findViewById(R.id.exercise_name)
        val theName: MaterialTextView = itemView.findViewById(R.id.the_name)
        val setText: MaterialTextView = itemView.findViewById(R.id.set_text)
        val theSet: MaterialTextView = itemView.findViewById(R.id.set_number)
        val repetitionsText: MaterialTextView = itemView.findViewById(R.id.repetitions_text)
        val theRepetitions: MaterialTextView = itemView.findViewById(R.id.repetitions_text)
        val weightText: MaterialTextView = itemView.findViewById(R.id.repetitions_number)
        val theWeight: MaterialTextView = itemView.findViewById(R.id.weight_text)

        val x = itemView.setOnClickListener {
            exerciseCallback?.itemClick(getItem(adapterPosition), adapterPosition)
        }
    }
}

