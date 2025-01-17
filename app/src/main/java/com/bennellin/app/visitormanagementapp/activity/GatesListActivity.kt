package com.bennellin.app.visitormanagementapp.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.adapters.GateAdapter
import com.bennellin.app.visitormanagementapp.databinding.ActivityGatesListBinding
import com.bennellin.app.visitormanagementapp.models.GateModel

class GatesListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGatesListBinding
    private lateinit var itemAdapter: GateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGatesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_gates_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val itemList = listOf(
            GateModel(
                1001,
                "GATE 1",
            ),
            GateModel(
                1002,
                "GATE 2",
            ),
            GateModel(
                1003,
                "GATE 3",
            ),
            GateModel(
                1004,
                "GATE 4",
            ),
            GateModel(
                1005,
                "GATE 5",
            ),
            GateModel(
                1006,
                "GATE 6",
            ),
        )

        itemAdapter = GateAdapter(itemList, this)
        binding.recyclerView.adapter = itemAdapter
    }
}