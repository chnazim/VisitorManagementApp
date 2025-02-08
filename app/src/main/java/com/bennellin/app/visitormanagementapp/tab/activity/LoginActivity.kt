package com.bennellin.app.visitormanagementapp.tab.activity

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.databinding.ActivityLoginTabViewBinding
import com.bennellin.app.visitormanagementapp.general.SharedPreferenceManager
import com.bennellin.app.visitormanagementapp.models.AuthResponse
import com.bennellin.app.visitormanagementapp.tab.network.RetrofitInstance
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    private lateinit var binding: ActivityLoginTabViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding = ActivityLoginTabViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managePermissionsAndInit()

        org.apache.xml.security.Init.init(this)

        binding.loginButton.setOnClickListener {
            val userName = binding.userName.text.toString()
            val password = binding.password.text.toString()
            if (userName.isNotEmpty() && password.isNotEmpty()) {
                authenticateUser(userName, password)
            } else {
                Toast.makeText(this, "Please provide username and password.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun managePermissionsAndInit() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                if (!Environment.isExternalStorageManager()) {
                    requestManageAllFilesPermission()
                } else {
                    initializeApp()
                }
            }
            Build.VERSION.SDK_INT in Build.VERSION_CODES.M until Build.VERSION_CODES.R -> {
                if (!hasLegacyPermissions()) {
                    requestLegacyPermissions()
                } else {
                    initializeApp()
                }
            }
            else -> {
                initializeApp()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun hasLegacyPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun requestLegacyPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestManageAllFilesPermission() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "This app requires access to manage files on your device.",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Grant") {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
        }.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initializeApp()
            } else {
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permissions Required")
            .setMessage("The app cannot function without the necessary permissions.")
            .setPositiveButton("Grant Permissions") { _, _ -> managePermissionsAndInit() }
            .setNegativeButton("Exit") { _, _ -> finish() }
            .show()
    }

    private fun initializeApp() {
        val appDirectory = File(Environment.getExternalStorageDirectory(), "MyApp")
        if (!appDirectory.exists()) {
            appDirectory.mkdirs()
        }
        // Additional initialization code here
    }

    private fun authenticateUser(userName: String, password: String) {
        val call = RetrofitInstance.apiService.authenticate(userName, password)
        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(
                call: Call<AuthResponse>,
                response: Response<AuthResponse>
            ) {
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    loadMainActivity(authResponse?.access_token, userName, password)
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadMainActivity(accessToken: String?, userName: String, password: String) {
        SharedPreferenceManager.saveLoggedIn("is_logged_in", true)
        SharedPreferenceManager.saveAuthToken("auth_token", accessToken!!)
        SharedPreferenceManager.saveUserName("user_name", userName)
        SharedPreferenceManager.savePassword("password", password)
        startActivity(Intent(this, HomeActivityTab::class.java))
        finish()
    }
}










//package com.bennellin.app.visitormanagementapp.tab.activity
//
//import android.Manifest
//import android.annotation.TargetApi
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.content.res.AssetManager
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.provider.Settings
//import android.view.View
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.bennellin.app.visitormanagementapp.R
//import com.bennellin.app.visitormanagementapp.databinding.ActivityLoginTabViewBinding
//import com.bennellin.app.visitormanagementapp.general.SharedPreferenceManager
//import com.bennellin.app.visitormanagementapp.general.utils
//import com.bennellin.app.visitormanagementapp.models.AuthResponse
//import com.bennellin.app.visitormanagementapp.tab.network.ApiService
//import com.bennellin.app.visitormanagementapp.tab.network.RetrofitInstance
//import com.bennellin.app.visitormanagementapp.Logger.Logger
//import com.bennellin.app.visitormanagementapp.general.MyApp
//import com.google.android.material.snackbar.Snackbar
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//import java.io.InputStream
//import java.io.OutputStream
//
//class LoginActivity : AppCompatActivity() {
//
//    companion object {
//        private const val CALLBACK_NUMBER = 100
//    }
//    private lateinit var binding: ActivityLoginTabViewBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        window.decorView.systemUiVisibility =
//            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//        binding = ActivityLoginTabViewBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        enableEdgeToEdge()
////        setContentView(R.layout.activity_login_tab_view)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//        org.apache.xml.security.Init.init(this)
//        when {
//            Build.VERSION.SDK_INT in Build.VERSION_CODES.M until Build.VERSION_CODES.S -> {
//                checkPermissions(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_PHONE_STATE
//                )
//            }
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//                checkPermissions(
//                    Manifest.permission.READ_MEDIA_IMAGES,
//                    Manifest.permission.READ_PHONE_STATE
//                )
//                if (!Environment.isExternalStorageManager()) {
//                    Snackbar.make(
//                        findViewById(android.R.id.content),
//                        "Permission needed!",
//                        Snackbar.LENGTH_INDEFINITE
//                    ).setAction("Settings") {
//                        navigateToManageAllFilesAccess()
//                    }.show()
//                }
//            }
//            else -> {
//                init()
//            }
//        }
//
//        val path = applicationInfo.nativeLibraryDir
//        Logger.d(path)
//        val file = File(path)
//        if (file.exists()) {
//            file.list()?.forEach { name ->
//                Logger.d(name)
//            } ?: Logger.e("Libs is empty")
//        }
//
//        binding.loginButton.setOnClickListener {
//            val userName = binding.userName.text.toString()
//            val password = binding.password.text.toString()
//
////            val retrofit = Retrofit.Builder()
////                .baseUrl(utils.BASE_URL) // Replace with your base URL
////                .addConverterFactory(GsonConverterFactory.create())
////                .build()
//
////            val apiService = retrofit.create(ApiService::class.java)
//
//            if (userName.isNotEmpty() && password.isNotEmpty()) {
//                val call = RetrofitInstance.apiService.authenticate(userName, password)
//
//                call.enqueue(object : Callback<AuthResponse> {
//                    override fun onResponse(
//                        call: Call<AuthResponse>,
//                        response: Response<AuthResponse>
//                    ) {
//                        if (response.isSuccessful) {
//                            val authResponse = response.body()
//                            authResponse?.let {
//                                // Successfully parsed response
//                                println("Access Token: ${it.access_token}")
//                                println("Token Type: ${it.token_type}")
//                                println("Expires In: ${it.expires_in}")
//                                println("User Name: ${it.userName}")
//                                println("Issued: ${it.issued}")
//                                println("Expires: ${it.expires}")
//                            }
//                            loadMainActivity(authResponse?.access_token, userName, password)
//                        } else {
//                            // Handle error response
//                            println("Error: ${response.errorBody()?.string()}")
//
//                        }
//                    }
//
//                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
//                        // Handle network or other errors
//                        println("Failure: ${t.message}")
//                    }
//                })
//            } else {
//                Toast.makeText(this, "Please provide userName and Password", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
//    }
//
//    private fun loadMainActivity(accessToken: String?, userName: String, password: String) {
//        SharedPreferenceManager.saveLoggedIn("is_logged_in", true)
//        SharedPreferenceManager.saveAuthToken("auth_token", accessToken!!)
//        SharedPreferenceManager.saveUserName("user_name", userName)
//        SharedPreferenceManager.savePassword("password", password)
//        startActivity(Intent(this, HomeActivityTab::class.java))
//        finish()
//
//    }
//
//    @TargetApi(Build.VERSION_CODES.M)
//    private fun checkPermissions(vararg permissions: String) {
//        val toBeRequested = permissions.filter {
//            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
//        }
//
//        if (toBeRequested.isNotEmpty()) {
//            requestPermissions(toBeRequested.toTypedArray(), CALLBACK_NUMBER)
//        } else {
//            init()
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == CALLBACK_NUMBER) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                init()
//            } else {
//                Toast.makeText(this, "Can't proceed without Permission", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
//    private fun init() {
//        val outDir = File(MyApp.path)
//        if (!outDir.exists()) {
//            Logger.d("Directory not found _________ ${outDir.absolutePath}")
//            outDir.mkdirs()
//        }
//    }
//
//    private fun configure(isOverWrite: Boolean) {
//        val assetManager: AssetManager = assets
//        val files = try {
//            assetManager.list("")
//        } catch (e: IOException) {
//            Logger.e("Failed to get asset file list: ${e.localizedMessage}")
//            null
//        }
//
//        val outDir = File(MyApp.path)
//        if (!outDir.exists()) {
//            Logger.d("Directory not found _________ ${outDir.absolutePath}")
//            outDir.mkdirs()
//        }
//
//        files?.forEach { filename ->
//            val outFile = File("${outDir.absolutePath}/$filename")
//            if (!isOverWrite && outFile.exists()) {
//                Logger.d("Output file exists ___________ ${outFile.absolutePath}")
//                return@forEach
//            }
//
//            try {
//                assetManager.open(filename).use { input ->
//                    FileOutputStream(outFile).use { output ->
//                        copyFile(input, output)
//                    }
//                }
//            } catch (e: IOException) {
//                Logger.e("Failed to copy asset file: $filename ${e.localizedMessage}")
//            }
//        }
//    }
//
//    private fun copyFile(input: InputStream, output: OutputStream) {
//        val buffer = ByteArray(1024)
//        var read: Int
//        while (input.read(buffer).also { read = it } != -1) {
//            output.write(buffer, 0, read)
//        }
//    }
//    @RequiresApi(Build.VERSION_CODES.R)
//    private fun navigateToManageAllFilesAccess() {
//        try {
//            val uri = Uri.parse("package:${applicationContext.packageName}")
//            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
//            startActivity(intent)
//        } catch (ex: Exception) {
//            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//            startActivity(intent)
//        }
//    }
//
//}