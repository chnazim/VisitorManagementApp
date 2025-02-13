package com.bennellin.app.visitormanagementapp.tab.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.databinding.ActivityDetailViewBinding
import com.bennellin.app.visitormanagementapp.general.SharedPreferenceManager
import com.bennellin.app.visitormanagementapp.general.utils
import com.bennellin.app.visitormanagementapp.tab.adapter.VisitHistoryAdapter
import com.bennellin.app.visitormanagementapp.tab.adapter.VisitorAdapterDashboard
import com.bennellin.app.visitormanagementapp.tab.network.RetrofitInstance
import com.bennellin.app.visitormanagementapp.tab.network.models.Visitor
import com.bennellin.app.visitormanagementapp.utils.Bitmaps
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailViewBinding
    private lateinit var visitor: Visitor
    private lateinit var visitHistoryAdapter: VisitHistoryAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        binding = ActivityDetailViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        visitor = (intent.getSerializableExtra("visitor") as? Visitor)!!

        binding.txtName.text = "" + visitor.name
        binding.txtPhone.text = "Phone:" + visitor.contactNumber
        binding.txtEmail.text = "Email:" + visitor.Email
        binding.txtRemark.text = "Description:" + visitor.remark
        binding.txtCompany.text = "Company Name:" + visitor.companyName
        displayPhoto(visitor.profilePicture)


        binding.recyclerViewVisitHistory.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewVisitHistory.setHasFixedSize(true)

        callVisitHistoryApi(SharedPreferenceManager.getAuthToken("auth_token"), visitor.visitorID)

        binding.closeButton.setOnClickListener({
            callBackPressed()
//           callBackPage()
        })

        if (visitor.exitTime == "0001-01-01T00:00:00") {
            binding.btnCheckOut.visibility = View.VISIBLE
        } else {
            binding.btnCheckOut.visibility = View.GONE
        }

        if (!(visitor.registrationStatus == null)) {
            binding.txtWhitelist.visibility = View.VISIBLE
            binding.txtWhitelist.apply {
                text = visitor.registrationStatus
                setBackgroundResource(
                    if (visitor.registrationStatus == "Whitelisted") R.drawable.whitelist_background
                    else R.drawable.blacklist_background
                )
            }
        } else {
            binding.txtWhitelist.visibility = View.INVISIBLE
        }


        binding.btnCheckOut.setOnClickListener({
            callCheckOutApi(visitor)
        })
        binding.closeButton.setOnClickListener({
            val intent = Intent(
                this@DetailViewActivity,
                HomeActivityTab::class.java
            )
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        })

    }

    private fun callVisitHistoryApi(authToken: String?, visitorID: String) {
        val call = RetrofitInstance.apiService.getVisitHistory("Bearer $authToken", visitorID)

        call.enqueue(object : Callback<List<Visitor>> {
            override fun onResponse(call: Call<List<Visitor>>, response: Response<List<Visitor>>) {
                if (response.isSuccessful) {
                    println("api call successful ${response.body()}")
                    val visitorList = response.body() ?: emptyList()
                    visitHistoryAdapter = VisitHistoryAdapter(visitorList)
                    val divider = DividerItemDecoration(
                        this@DetailViewActivity,
                        DividerItemDecoration.VERTICAL
                    )
                    binding.recyclerViewVisitHistory.addItemDecoration(divider)
                    binding.recyclerViewVisitHistory.adapter = visitHistoryAdapter
                } else {
                    println("Response Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Visitor>>, t: Throwable) {
                println("onFailure Error: ${t.message}")
            }

        })

    }

    private fun callCheckOutApi(visitor: Visitor) {
        val authToken = SharedPreferenceManager.getAuthToken("auth_token")
        Log.d("tag", "idEid: ${visitor.idEidReading}")

        val call =
            RetrofitInstance.apiService.checkOutVisitor("Bearer $authToken", visitor.idEidReading)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    println("api call successful ${response.body()}")
                    Toast.makeText(
                        this@DetailViewActivity,
                        "Visitor Checked Out Successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    callBackPage()
                } else {
                    Toast.makeText(
                        this@DetailViewActivity,
                        "Something went wrong, Please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                    println("Response Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(
                    this@DetailViewActivity,
                    "Something went wrong, Please try again later",
                    Toast.LENGTH_SHORT
                ).show()
                println("onFailure Error: ${t.message}")
            }

        })
    }

    private fun callBackPressed() {
        finish()
    }

    private fun callBackPage() {
        val intent = Intent(this@DetailViewActivity, HomeActivityTab::class.java)
        intent.setFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
        )
        startActivity(intent)
    }

    private fun displayPhoto(cardHolderPhoto: String) {
        if (cardHolderPhoto == null) {
            return
        } //if()

        val photo = Base64.decode(cardHolderPhoto, Base64.DEFAULT)
        if (photo == null || photo.size <= 0) {
            return
        } //if()

        //Create  a bitmap.
        //set to the imageview
        binding.imgProfile.setImageBitmap(
            Bitmaps.decodeSampledBitmapFromBytes(
                photo, 150, 150
            )
        )
    } //
}