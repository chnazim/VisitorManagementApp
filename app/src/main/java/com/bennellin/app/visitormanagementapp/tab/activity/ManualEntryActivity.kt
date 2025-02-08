package com.bennellin.app.visitormanagementapp.tab.activity

import android.content.Intent
import android.content.pm.ActivityInfo
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
import com.bennellin.app.visitormanagementapp.databinding.ActivityEidscanBinding
import com.bennellin.app.visitormanagementapp.databinding.ActivityManualEntryBinding
import com.bennellin.app.visitormanagementapp.general.SharedPreferenceManager
import com.bennellin.app.visitormanagementapp.tab.network.RetrofitInstance
import com.bennellin.app.visitormanagementapp.tab.network.models.CheckInApiRequestBody
import com.bennellin.app.visitormanagementapp.tab.network.models.VisitPurpose
import com.bennellin.app.visitormanagementapp.tab.network.models.VisitorType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManualEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManualEntryBinding
    private var selectedVisitorType: VisitorType? = null
    private var selectedVisitPurpose: VisitPurpose? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        binding = ActivityManualEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()



        getVisitorType(SharedPreferenceManager.getAuthToken("auth_token"))
        getVisitPurpose(SharedPreferenceManager.getAuthToken("auth_token"))

        binding.buttonCheckIn.setOnClickListener {
//            Toast.makeText(this, "call check-in api", Toast.LENGTH_SHORT).show()\

            callCheckInApi(
                SharedPreferenceManager.getAuthToken("auth_token")
            )

        }

        binding.buttonCancel.setOnClickListener({
            onBackPressed()
        })

    }

    private fun callCheckInApi(authToken: String?) {
        val requestBody = CheckInApiRequestBody(
            NameEnglish = "${binding.fullNameEnglish.text}",
            NameArabic = "",
            IdNumber = "${binding.eidNumber.text}",
            IdCardNumber = "${binding.cardNumber.text}",
            IssueDate = "${binding.issueDate.text}",
            ExpiryDate = "${binding.expiryDate.text}",
            DateOfBirth = "${binding.dateOfBirth.text}",
            NationalityCode = "${binding.nationality.text}",
            Email = "${binding.emailId.text}",
            Phone = "${binding.mobileNumber.text}",
            RawData = "",
            ProfilePictureBase64 = "",
            VisitorType = selectedVisitorType!!.VisitorType,
            VisitPurpose = selectedVisitPurpose!!.VisitPurpose,
            IdAccessCard = "${binding.accessCardNumber.text}",
            Remarks = "${binding.remarks.text}"
        )

        val call = RetrofitInstance.apiService.checkInRequest("Bearer $authToken", requestBody)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@ManualEntryActivity, "Checked-in Successfully", Toast.LENGTH_SHORT
                    ).show()
                    println("Success: ${response.body()}")
                    redirectToHome()
                } else {
                    Toast.makeText(
                        this@ManualEntryActivity,
                        "Something went wrong, please try again later..",
                        Toast.LENGTH_SHORT
                    ).show()
                    println("Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(
                    this@ManualEntryActivity,
                    "Something went wrong, please try again later..",
                    Toast.LENGTH_SHORT
                ).show()
                println("Failure: ${t.message}")
            }
        })


    }


    private fun getVisitPurpose(authToken: String?) {
        val call = RetrofitInstance.apiService.getVisitorPurpose("Bearer $authToken")

        call.enqueue(object : Callback<List<VisitPurpose>> {
            override fun onResponse(
                call: Call<List<VisitPurpose>>, response: Response<List<VisitPurpose>>
            ) {
                if (response.isSuccessful) {
                    println("api call successful ${response.body()}")
                    val visitPurposeList = response.body() ?: emptyList()

                    visitPurposeList.sortedBy { it.SortOrder }
                    val visitorPurpose = visitPurposeList.map { it.VisitPurpose }

                    val adapter = ArrayAdapter(
                        this@ManualEntryActivity, R.layout.spinner_item_layout, visitorPurpose
                    )
                    adapter.setDropDownViewResource(R.layout.spinner_item_layout)
                    binding.visitPurpose.adapter = adapter

                    binding.visitPurpose.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?, view: View?, position: Int, id: Long
                            ) {
                                selectedVisitPurpose = visitPurposeList[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                selectedVisitPurpose = null
                            }

                        }

                } else {
                    println("Response Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<VisitPurpose>>, t: Throwable) {
                println("onFailure Error: ${t.message}")
            }

        })

    }

    private fun getVisitorType(authToken: String?) {
        val call = RetrofitInstance.apiService.getVisitorType("Bearer $authToken")

        call.enqueue(object : Callback<List<VisitorType>> {
            override fun onResponse(
                call: Call<List<VisitorType>>, response: Response<List<VisitorType>>
            ) {
                if (response.isSuccessful) {
                    println("api call successful ${response.body()}")
                    val visitorTypeList = response.body() ?: emptyList()

                    visitorTypeList.sortedBy { it.SortOrder }
                    val visitorTypes = visitorTypeList.map { it.VisitorType }

                    val adapter = ArrayAdapter(
                        this@ManualEntryActivity, R.layout.spinner_item_layout, visitorTypes
                    )
                    adapter.setDropDownViewResource(R.layout.spinner_item_layout)
                    binding.visitorType.adapter = adapter

                    binding.visitorType.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?, view: View?, position: Int, id: Long
                            ) {
                                selectedVisitorType = visitorTypeList[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                selectedVisitorType = null
                            }

                        }

                } else {
                    println("Response Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<VisitorType>>, t: Throwable) {
                println("onFailure Error: ${t.message}")
            }

        })

    }


    private fun redirectToHome() {
        startActivity(
            Intent(
                this, HomeActivityTab::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }
}