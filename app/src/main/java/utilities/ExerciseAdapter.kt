package utilities
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trainMaster.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class ExerciseAdapter(private var exerciseList: ArrayList<Exercise>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun itemClick(exercise: Exercise)
        fun update(exercise: Exercise,position: Int, increase: Boolean)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.exercise_item, parent, false)
        return ExerciseViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise: Exercise = exerciseList[position]

        holder.theName.text = exercise.name
        "${exercise.numOfSets}   ".also { holder.theSet.text = it }
        holder.theRepetitions.text = exercise.numOfReps
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    fun getItem(position: Int): Exercise {
        return exerciseList[position]
    }

    inner class ExerciseViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private lateinit var increase: MaterialTextView
        private lateinit var decrease: MaterialTextView
        private lateinit var exerciseNameText: MaterialTextView
        lateinit var theName: MaterialTextView
        private lateinit var setText: MaterialTextView
        lateinit var theSet: MaterialTextView
        private lateinit var repetitionsText: MaterialTextView
        lateinit var theRepetitions: MaterialTextView
        private lateinit var increaseImage: ShapeableImageView
        private lateinit var decreaseImage:ShapeableImageView
        init {
            findViews()

            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION)
                    listener.itemClick(getItem(position))
            }
            increase.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION)
                    listener.update(getItem(position), position, true)
            }
            decrease.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION)
                    listener.update(getItem(position), position, false)
            }
        }
        private fun findViews() {
            increase = itemView.findViewById(R.id.increase)
            decrease = itemView.findViewById(R.id.decrease)
            exerciseNameText = itemView.findViewById(R.id.exercise_name)
            theName = itemView.findViewById(R.id.the_name)
            setText = itemView.findViewById(R.id.set_text)
            theSet = itemView.findViewById(R.id.set_number)
            repetitionsText = itemView.findViewById(R.id.repetitions_text)
            theRepetitions = itemView.findViewById(R.id.repetitions_number)
            increaseImage =itemView.findViewById(R.id.upgrade_image)
            decreaseImage =itemView.findViewById(R.id.downward_image)
        }
    }
}