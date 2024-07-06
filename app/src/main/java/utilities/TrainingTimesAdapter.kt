package fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainMaster.R

class TrainingTimesAdapter(private val trainingTimes: List<String>) :
    RecyclerView.Adapter<TrainingTimesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trainingTimeTextView: TextView = view.findViewById(R.id.training_time_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_training_time, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.trainingTimeTextView.text = trainingTimes[position]
    }

    override fun getItemCount() = trainingTimes.size
}
