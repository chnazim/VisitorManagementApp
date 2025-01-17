package com.bennellin.app.visitormanagementapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bennellin.app.visitormanagementapp.models.AuthRequest
import com.bennellin.app.visitormanagementapp.models.AuthResponse
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.general.RetrofitInstance
import com.bennellin.app.visitormanagementapp.general.SharedPreferenceManager
import com.bennellin.app.visitormanagementapp.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

//    @Inject
//    lateinit var preferenceManager: PreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
//        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.loginButton.setOnClickListener {
            //callApi
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                loadMainActivity("token")

//                val authRequest = AuthRequest(
//                    userName = email,
//                    password = password,
//                    grandTpe = "password"
//                )
//                RetrofitInstance.api.authenticateUser(authRequest).enqueue(object :
//                    Callback<AuthResponse> {
//                    override fun onResponse(
//                        call: Call<AuthResponse>,
//                        response: Response<AuthResponse>
//                    ) {
//                        if (response.isSuccessful) {
//                            val authResponse = response.body()
//                            Log.d("AuthResponse", "Access Token: ${authResponse?.access_token}")
//                            // Handle the response as needed
//                            loadMainActivity(authResponse?.access_token)
//                        } else {
//                            Log.e("AuthResponse", "Error: ${response.code()}")
//                        }
//                    }
//
//                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
//                        Log.e("AuthResponse", "Failure: ${t.message}")
//                    }
//                })
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun loadMainActivity(accessToken: String?) {
        SharedPreferenceManager.saveLoggedIn("is_logged_in", true)
        SharedPreferenceManager.saveAuthToken("auth_token", accessToken!!)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}