package com.bennellin.app.visitormanagementapp.tab.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.tab.network.models.Visitor

class VisitHistoryAdapter(private val visitList: List<Visitor>) :
    RecyclerView.Adapter<VisitHistoryAdapter.VisitViewHolder>() {

    class VisitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val entryTime: TextView = view.findViewById(R.id.tv_entry_time)
        val exitTime: TextView = view.findViewById(R.id.tv_exit_time)
        val idType: TextView = view.findViewById(R.id.tv_id_type)
        val idNumber: TextView = view.findViewById(R.id.tv_id_number)
        val visitPurpose: TextView = view.findViewById(R.id.tv_visit_purpose)
        val remark: TextView = view.findViewById(R.id.tv_remark)
        val contactNumber: TextView = view.findViewById(R.id.tv_contact_number)
        val company: TextView = view.findViewById(R.id.tv_company_name)
        val accessCardNumber: TextView = view.findViewById(R.id.tv_access_card_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_visit_history, parent, false)
        return VisitViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VisitViewHolder, position: Int) {
        val visit = visitList[position]
        holder.entryTime.text = visit.entryTime
        if (visit.exitTime == "0001-01-01T00:00:00") {
            holder.exitTime.text = "Still Inside"
        } else {
            holder.exitTime.text = visit.exitTime
        }

        holder.idType.text = "ID type"
        holder.idNumber.text = visit.idNumber
        holder.visitPurpose.text = visit.visitPurpose
        holder.remark.text = visit.remark
        holder.contactNumber.text = visit.contactNumber
        holder.company.text = visit.companyName
        holder.accessCardNumber.text = visit.accessCardNumber
    }

    override fun getItemCount(): Int = visitList.size
}