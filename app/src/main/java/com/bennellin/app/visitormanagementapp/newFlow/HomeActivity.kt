package com.bennellin.app.visitormanagementapp.newFlow

import VisitorAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.databinding.ActivityHomeBinding
import com.bennellin.app.visitormanagementapp.general.SharedPreferenceManager
import com.bennellin.app.visitormanagementapp.newFlow.dataClass.Visitor

class HomeActivity : AppCompatActivity() {


    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val visitorList = listOf(
            Visitor("Gilly Janzen", "784-1985-15256937-4", "2024-10-01 15:30", "Entrance 1"),
            Visitor("Gilly Janzen", "784-1985-15256937-4", "2024-10-01 15:30", "Entrance 1"),
            Visitor("Gilly Janzen", "784-1985-15256937-4", "2024-10-01 15:30", "Entrance 1"),
            Visitor("Gilly Janzen", "784-1985-15256937-4", "2024-10-01 15:30", "Entrance 1"),
            Visitor("Gilly Janzen", "784-1985-15256937-4", "2024-10-01 15:30", "Entrance 1"),
        )

        binding.recentScansRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.recentScansRecyclerView.adapter = VisitorAdapter(visitorList)

        binding.scanNowButton.setOnClickListener {
            startActivity(Intent(this, ScanResultActivity::class.java))
            finish()
        }
        binding.homeButton.setOnClickListener({
            startActivity(Intent(this@HomeActivity, HomeActivity::class.java))
            finish()
        })
        binding.logout.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Logout")
                setMessage("Are you sure you want to log out?")
                setPositiveButton("Yes") { dialog, which ->
                    // User clicked Yes, proceed with logout
                    SharedPreferenceManager.saveLoggedIn("is_logged_in", false)
                    val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                setNegativeButton("No") { dialog, which ->
                    // User clicked No, dismiss the dialog
                    dialog.dismiss()
                }
                show()
            }
            true
        }
    }
}