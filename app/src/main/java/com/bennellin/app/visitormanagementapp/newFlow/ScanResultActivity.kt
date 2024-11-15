package com.bennellin.app.visitormanagementapp.newFlow

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.databinding.ActivityScanResultBinding
import com.bennellin.app.visitormanagementapp.general.SharedPreferenceManager

class ScanResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        setContentView(R.layout.activity_scan_result)
        binding = ActivityScanResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.saveButton.setOnClickListener({
            Toast.makeText(this, "Save api call functionality and print token", Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(this@ScanResultActivity, HomeActivity::class.java))
            finish()
        })
        binding.logout.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Logout")
                setMessage("Are you sure you want to log out?")
                setPositiveButton("Yes") { dialog, which ->
                    // User clicked Yes, proceed with logout
                    SharedPreferenceManager.saveLoggedIn("is_logged_in", false)
                    val intent = Intent(this@ScanResultActivity, LoginActivity::class.java)
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
        binding.homeButton.setOnClickListener({
            startActivity(Intent(this@ScanResultActivity, HomeActivity::class.java))
            finish()
        })
    }
}