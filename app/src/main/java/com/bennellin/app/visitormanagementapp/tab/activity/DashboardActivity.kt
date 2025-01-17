package com.bennellin.app.visitormanagementapp.tab.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.tab.adapter.VisitorAdapter

class DashboardActivity : AppCompatActivity() {
    private lateinit var visitorRecyclerView: RecyclerView
    private lateinit var filterFromDate: EditText
    private lateinit var filterToDate: EditText
    private lateinit var filterSearch: EditText
    private lateinit var resetButton: Button
    private lateinit var searchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize Views
        filterFromDate = findViewById(R.id.filterFromDate)
        filterToDate = findViewById(R.id.filterToDate)
        filterSearch = findViewById(R.id.filterSearch)
        resetButton = findViewById(R.id.resetButton)
        searchButton = findViewById(R.id.searchButton)
        visitorRecyclerView = findViewById(R.id.visitorRecyclerView)

        // RecyclerView Setup
//        visitorRecyclerView.layoutManager = LinearLayoutManager(this)
//        val visitorAdapter = VisitorAdapter(getDummyVisitors())
//        visitorRecyclerView.adapter = visitorAdapter

        // Button Actions
        resetButton.setOnClickListener {
            filterFromDate.text.clear()
            filterToDate.text.clear()
            filterSearch.text.clear()
        }

        searchButton.setOnClickListener {
            // Add filter logic here
        }
    }

    private fun getDummyVisitors(): List<Visitor> {
        return listOf(
            Visitor("John Doe", "02/01/25 12:45AM", "Still Inside", "Meeting", "Whitelisted"),
            Visitor("Jane Smith", "15/12/24 09:35AM", "15/12/24 04:20PM", "Meeting", "Blacklisted"),
            Visitor("Ronan Sloan", "10/12/24 01:25PM", "10/12/24 03:15PM", "Meeting", "Whitelisted")
        )
    }
}

data class Visitor(
    val name: String,
    val entryTime: String,
    val exitTime: String,
    val visitPurpose: String,
    val accessCardStatus: String
)