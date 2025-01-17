package com.bennellin.app.visitormanagementapp.tab.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bennellin.app.visitormanagementapp.Logger.Logger
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.general.InitializeToolkitTask
import com.bennellin.app.visitormanagementapp.general.SharedPreferenceManager
import com.bennellin.app.visitormanagementapp.tab.fragments.DashboardFragment
import com.bennellin.app.visitormanagementapp.tab.fragments.ReportsFragment


class HomeActivityTab : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var pageTitle: TextView
    private lateinit var profileImage: ImageView
    private lateinit var actionButton: ImageView
    private lateinit var toolKitInit: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(R.layout.activity_home_tab)

        // Initialize the DrawerLayout and Toolbar elements
        drawerLayout = findViewById(R.id.drawer_layout)
        pageTitle = findViewById(R.id.page_title)
        profileImage = findViewById(R.id.profile_image)
        actionButton = findViewById(R.id.actionButton)
        toolKitInit = findViewById(R.id.initializeToolKit)

        // Load DashboardFragment by default
        replaceFragment(DashboardFragment(), "Dashboard")

        // Sidebar options
        findViewById<LinearLayout>(R.id.option_dashboard).setOnClickListener {
            replaceFragment(DashboardFragment(), "Dashboard")
            drawerLayout.closeDrawers()
        }

        findViewById<LinearLayout>(R.id.option_reports).setOnClickListener {
            replaceFragment(ReportsFragment(), "Reports")
            drawerLayout.closeDrawers()
        }

        // Set action for profile image
        profileImage.setOnClickListener {
            // Show profile options or navigate to profile page
            showProfileOptions()
        }

        // Set action for the action button
        actionButton.setOnClickListener {
            // Perform action based on the current fragment
            performAction()
        }

        toolKitInit.setOnClickListener {
            initializeToolKit()
        }
    }

    private fun initializeToolKit() {
        InitializeToolkitTask { isSuccess, message ->
            if (isSuccess) {
                Toast.makeText(
                    this@HomeActivityTab,
                    "Initialization Successful",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@HomeActivityTab,
                    "Initialization Failed: $message", Toast.LENGTH_LONG
                ).show()
            }
        }.execute()
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        pageTitle.text = title
    }

    private fun showProfileOptions() {
        // Implement profile options (e.g., settings, logout, etc.)

        AlertDialog.Builder(this).apply {
            setTitle("Logout")
            setMessage("Are you sure you want to log out?")
            setPositiveButton("Yes") { dialog, which ->
                // User clicked Yes, proceed with logout
                SharedPreferenceManager.saveLoggedIn("is_logged_in", false)
                val intent = Intent(this@HomeActivityTab, LoginActivity::class.java)
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
    }

    private fun performAction() {
        // Implement specific action logic (e.g., show notifications, download, etc.)

        Toast.makeText(this, "Scan EID Activity ", Toast.LENGTH_SHORT).show()

        val intent = Intent(this@HomeActivityTab, EidScanActivityNew::class.java)
        startActivity(intent)
    }
}
