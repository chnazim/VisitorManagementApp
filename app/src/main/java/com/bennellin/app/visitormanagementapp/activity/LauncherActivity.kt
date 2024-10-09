package com.bennellin.app.visitormanagementapp.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.general.SharedPreferenceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class LauncherActivity : AppCompatActivity() {

    private val REQUEST_CODE_READ_BASIC_PHONE_STATE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(R.layout.activity_launcher)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkAndRequestReadBasicPhoneStatePermission()

    }

    private fun checkAndRequestReadBasicPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_BASIC_PHONE_STATE")
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf("android.permission.READ_BASIC_PHONE_STATE"),
                REQUEST_CODE_READ_BASIC_PHONE_STATE
            )
        } else {
            // Permission is already granted, proceed with your logic
            val isLoggedIn = SharedPreferenceManager.isLoggedIn("is_logged_in")

            lifecycleScope.launch {
                delay(5000) // 5 seconds delay
                if (isLoggedIn) {
                    val intent = Intent(this@LauncherActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@LauncherActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
                finish()
            }
//            proceedWithPhoneState()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_READ_BASIC_PHONE_STATE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                proceedWithPhoneState()
            } else {
                Toast.makeText(this, "Phone state permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun proceedWithPhoneState() {
        // Handle phone state logic here (e.g., accessing IMEI, phone number)
        Toast.makeText(this, "Basic phone state permission granted", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LauncherActivity::class.java)
        startActivity(intent)
        finish()
    }

}