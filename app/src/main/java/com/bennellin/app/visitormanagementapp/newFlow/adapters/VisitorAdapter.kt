import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.newFlow.dataClass.Visitor

class VisitorAdapter(private val visitorList: List<Visitor>) : RecyclerView.Adapter<VisitorAdapter.VisitorViewHolder>() {

    class VisitorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val idNumberTextView: TextView = itemView.findViewById(R.id.idNumberTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val entranceTextView: TextView = itemView.findViewById(R.id.entranceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_for_scan, parent, false)
        return VisitorViewHolder(view)
    }

    override fun onBindViewHolder(holder: VisitorViewHolder, position: Int) {
        val visitor = visitorList[position]
        holder.nameTextView.text = visitor.name
        holder.idNumberTextView.text = visitor.idNumber
        holder.dateTextView.text = visitor.date
        holder.entranceTextView.text = visitor.entrance
    }

    override fun getItemCount(): Int = visitorList.size
}
