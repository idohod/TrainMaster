import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainMaster.R

class AchievementsAdapter(private val achievements: List<String>) :
    RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder>() {

    class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textViewAchievement)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.achievement_item, parent, false)
        return AchievementViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.textView.text = achievements[position]
    }

    override fun getItemCount() = achievements.size
}
