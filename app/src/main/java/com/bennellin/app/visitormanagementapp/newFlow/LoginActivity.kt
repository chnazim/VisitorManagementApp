package com.bennellin.app.visitormanagementapp.newFlow

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.databinding.ActivityLoginNewBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginNewBinding

    val items = listOf("Entrance 1", "Entrance 2", "Entrance 3", "Entrance 4")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//        setContentView(R.layout.activity_login_new)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationSpinner.adapter = adapter

        binding.locationSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    Toast.makeText(
                        this@LoginActivity,
                        "Selected: $selectedItem",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }

        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

    }
}