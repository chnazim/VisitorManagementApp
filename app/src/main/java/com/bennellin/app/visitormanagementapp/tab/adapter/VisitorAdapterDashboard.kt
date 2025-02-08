package com.bennellin.app.visitormanagementapp.tab.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.tab.network.models.Visitor

class VisitorAdapterDashboard(
    private val visitors: List<Visitor>,
    private val onPositiveClick: (Visitor) -> Unit,
    private val onCallDetailView: (Visitor) -> Unit
) :
    RecyclerView.Adapter<VisitorAdapterDashboard.VisitorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return VisitorViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VisitorViewHolder, position: Int) {
        val visitor = visitors[position]
        holder.entryTime.text = visitor.entryTime
        if (visitor.exitTime == "0001-01-01T00:00:00") {
            holder.exitTime.text = "Still Inside"
        } else {
            holder.exitTime.text = visitor.exitTime
        }
        holder.name.text = visitor.name
        holder.idNumber.text = visitor.idNumber
        holder.visitPurpose.text = visitor.visitPurpose
        holder.remark.text = visitor.remark
        holder.contactNumber.text = visitor.contactNumber
        holder.company.text = visitor.companyName
        holder.accessCardNumber.text = visitor.accessCardNumber
        if (!(visitor.registrationStatus == null)) {
            holder.registrationStatus.visibility = View.VISIBLE
            holder.registrationStatus.apply {
                text = visitor.registrationStatus
                setBackgroundResource(
                    if (visitor.registrationStatus == "Whitelisted") R.drawable.whitelist_background
                    else R.drawable.blacklist_background
                )
            }
        } else {
            holder.registrationStatus.visibility = View.INVISIBLE
        }
        holder.itemView.setOnClickListener({
            onCallDetailView(visitor)
        })

        holder.exitTime.setOnLongClickListener {
            if (visitor.exitTime == "0001-01-01T00:00:00") {
                showAlertDialog(holder.itemView.context, visitor)
            }
            true

        }
    }

    override fun getItemCount() = visitors.size

    private fun showAlertDialog(context: Context, visitor: Visitor) {
        AlertDialog.Builder(context)
            .setTitle("Check out Visitor?")
            .setMessage("Are you sure you want to Check out this visitor?")
            .setPositiveButton("Yes") { _, _ ->
                onPositiveClick(visitor)
            }
            .setNegativeButton("No", null)
            .show()
    }

    class VisitorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val entryTime: TextView = view.findViewById(R.id.tv_entry_time)
        val exitTime: TextView = view.findViewById(R.id.tv_exit_time)
        val name: TextView = view.findViewById(R.id.tv_name)
        val idNumber: TextView = view.findViewById(R.id.tv_id_number)
        val visitPurpose: TextView = view.findViewById(R.id.tv_visit_purpose)
        val remark: TextView = view.findViewById(R.id.tv_remark)
        val contactNumber: TextView = view.findViewById(R.id.tv_contact_number)
        val company: TextView = view.findViewById(R.id.tv_company_name)
        val accessCardNumber: TextView = view.findViewById(R.id.tv_access_card_number)
        val registrationStatus: TextView = view.findViewById(R.id.tv_registration)
    }
}