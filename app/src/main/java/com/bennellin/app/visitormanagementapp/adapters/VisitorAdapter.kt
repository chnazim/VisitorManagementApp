package com.bennellin.app.visitormanagementapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.models.VisitorData

class VisitorAdapter(private val visitorList: List<VisitorData>) :
    RecyclerView.Adapter<VisitorAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textName: TextView = itemView.findViewById(R.id.textName)
        val eidNumber: TextView = itemView.findViewById(R.id.eidNumber)
        val textDate: TextView = itemView.findViewById(R.id.textDate)
        val textGate: TextView = itemView.findViewById(R.id.textGate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.visitor_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = visitorList[position]
        holder.imageView.setImageResource(item.imageResId)
        holder.textName.text = item.name
        holder.eidNumber.text = item.eidNumber
        holder.textDate.text = item.date
        holder.textGate.text = item.gate
    }

    override fun getItemCount(): Int = visitorList.size
}