package com.bennellin.app.visitormanagementapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.activity.ScanActivity
import com.bennellin.app.visitormanagementapp.models.GateModel

class GateAdapter(private val gates: List<GateModel>, private val mContext: Context) :
    RecyclerView.Adapter<GateAdapter.GateViewHolder>() {

    // ViewHolder to hold the item view
    class GateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGateName: TextView = itemView.findViewById(R.id.tvGateName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gate_item, parent, false)
        return GateViewHolder(view)
    }

    override fun onBindViewHolder(holder: GateViewHolder, position: Int) {
        val gate = gates[position]
        holder.tvGateName.text = "Gate Name: ${gate.GateName}"
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, ScanActivity::class.java)
            intent.putExtra("gate_id", gate.IdGate)
            intent.putExtra("gate_name", gate.GateName)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return gates.size
    }
}