package com.bennellin.app.visitormanagementapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.databinding.ActivityVisitorDetailBinding

class VisitorDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisitorDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVisitorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
//        setContentView(R.layout.activity_visitor_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val purposes = arrayOf("Business", "Personal", "Meeting", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, purposes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.purposeOfVisit.adapter = adapter

        binding.saveButton.setOnClickListener {
            val tokenIntent = Intent(this, TokenDisplayActivity::class.java)
            startActivity(tokenIntent)
        }


    }
}