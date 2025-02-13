package com.bennellin.app.visitormanagementapp.tab.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.general.InitializeToolkitTask
import com.bennellin.app.visitormanagementapp.general.SharedPreferenceManager
import com.bennellin.app.visitormanagementapp.tab.fragments.DashboardFragment
import com.bennellin.app.visitormanagementapp.tab.fragments.ReportsFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeActivityTab : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var pageTitle: TextView
    private lateinit var profileImage: ImageView
    private lateinit var menuButton: ImageView
    private lateinit var actionButton: ImageView
    private lateinit var toolKitInit: ImageView
    private lateinit var sideDrawer: LinearLayout
    private lateinit var optionDashboard: LinearLayout
    private lateinit var optionReports: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(R.layout.activity_home_tab)

        // Initialize the DrawerLayout and Toolbar elements
        drawerLayout = findViewById(R.id.drawer_layout)
        pageTitle = findViewById(R.id.page_title)
        profileImage = findViewById(R.id.profile_image)
        actionButton = findViewById(R.id.actionButton)
        menuButton = findViewById(R.id.menu_button)
        toolKitInit = findViewById(R.id.initializeToolKit)
        sideDrawer = findViewById(R.id.sideDrawer)
        optionDashboard = findViewById(R.id.option_dashboard)
        optionReports = findViewById(R.id.option_reports)


        // Load DashboardFragment by default
        if (savedInstanceState == null) {
            replaceFragment(DashboardFragment(), "Dashboard")
        }

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(sideDrawer)
        }

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })


        // Handle Dashboard Click
        optionDashboard.setOnClickListener {
            replaceFragment(DashboardFragment(), "Dashboard")
            drawerLayout.closeDrawers() // Close the drawer

        }

        // Handle Reports Click
        optionReports.setOnClickListener {
            replaceFragment(ReportsFragment(), "Reports")
            drawerLayout.closeDrawers() // Close the drawer

        }

//        // Sidebar options
//        findViewById<LinearLayout>(R.id.option_dashboard).setOnClickListener {
//            replaceFragment(DashboardFragment(), "Dashboard")
//            drawerLayout.closeDrawers()
//        }
//
//        findViewById<LinearLayout>(R.id.option_reports).setOnClickListener {
//            replaceFragment(ReportsFragment(), "Reports")
//            drawerLayout.closeDrawers()
//        }

        // Set action for profile image
        profileImage.setOnClickListener {
            showProfileOptions(it)
        }

        // Set action for the action button
        actionButton.setOnClickListener {

            initializeToolKit()

            lifecycleScope.launch {
                delay(500)
                showAlertForOption(it)
            }

//            initializeToolKit()
//            Toast.makeText(this, "Please wait while initializing...", Toast.LENGTH_SHORT).show()
//
//            lifecycleScope.launch {
//                delay(1000)
//                initializeToolKit()
//                lifecycleScope.launch {
//                    delay(2000)
//                    performAction()
//                }
//            }
        }

        toolKitInit.setOnClickListener {
            initializeToolKit()
        }
    }

    private fun showAlertForOption(view: View) {
        val popupMenu = PopupMenu(this, view)

        // Inflate menu
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.scan_options_menu, popupMenu.menu)

        try {
            val fields = popupMenu.javaClass.getDeclaredFields()
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field.get(popupMenu)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons =
                        classPopupHelper.getMethod("setForceShowIcon", Boolean::class.java)
                    setForceIcons.invoke(menuPopupHelper, true)
                    break

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Set item click listener
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_eid_nfc -> {
                    initializeToolKit()
                    lifecycleScope.launch {
                        delay(1000)
                        performAction()
                    }
                    true
                }

                R.id.menu_eid_otg -> {
                    callEIDScanOTG()
                    true
                }

                R.id.menu_manual -> {
                    callManualEntry()
                    true
                }

                else -> false
            }
        }

        // Show the PopupMenu
        popupMenu.show()
    }

    private fun callEIDScanOTG() {
        val intent = Intent(this@HomeActivityTab, EIDScanOTGActivity::class.java)
        startActivity(intent)
    }

    private fun callManualEntry() {
        val intent = Intent(this@HomeActivityTab, ManualEntryActivity::class.java)
        startActivity(intent)
    }

    private fun initializeToolKit() {
        InitializeToolkitTask { isSuccess, message ->
            if (isSuccess) {
//                Toast.makeText(
//                    this@HomeActivityTab,
//                    "Initialization Successful",
//                    Toast.LENGTH_SHORT
//                ).show()
                Log.d("tag", "Toolkit Initialization Successful")
            } else {
//                Toast.makeText(
//                    this@HomeActivityTab,
//                    "Initialization Failed: $message", Toast.LENGTH_LONG
//                ).show()
                Log.d("tag", "Toolkit Initialization Failed $message")
            }
        }.execute()
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitAllowingStateLoss()
        pageTitle.text = title
    }

    private fun showProfileOptions(view: View) {
        val popupMenu = PopupMenu(this, view)

        // Inflate menu
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.profile_options_menu, popupMenu.menu)

        try {
            val fields = popupMenu.javaClass.getDeclaredFields()
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field.get(popupMenu)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons =
                        classPopupHelper.getMethod("setForceShowIcon", Boolean::class.java)
                    setForceIcons.invoke(menuPopupHelper, true)

                    break

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Set item click listener
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_profile -> {
                    Toast.makeText(this, "Profile page", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.menu_settings -> {
                    Toast.makeText(this, "Settings page", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.menu_logout -> {
                    callLogoutAlert()
                    true
                }

                else -> false
            }
        }

        // Show the PopupMenu
        popupMenu.show()
    }


    private fun callLogoutAlert() {
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
        intent.putExtra("TYPE", 5)
        startActivity(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(sideDrawer)) {
            drawerLayout.closeDrawer(sideDrawer)
        } else {
            super.onBackPressed()
        }
    }
}
