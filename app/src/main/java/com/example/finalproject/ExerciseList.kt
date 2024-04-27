import com.example.finalproject.Exercise

class ExerciseList {
    private var allExercises: ArrayList<Exercise>

    init {
        allExercises = ArrayList<Exercise>()
    }

    fun getAllExercises(): ArrayList<Exercise> {
        return allExercises
    }

    fun setAllExercises(allExercises: ArrayList<Exercise>) {
        this.allExercises = allExercises
    }
}