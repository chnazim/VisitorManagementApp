package com.bennellin.app.visitormanagementapp.tab.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Base64
import android.util.Log
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
import com.bennellin.app.visitormanagementapp.general.SharedPreferenceManager
import com.bennellin.app.visitormanagementapp.tab.network.RetrofitInstance
import com.bennellin.app.visitormanagementapp.tab.network.models.CheckInApiRequestBody
import com.bennellin.app.visitormanagementapp.tab.network.models.VisitPurpose
import com.bennellin.app.visitormanagementapp.tab.network.models.VisitorType
import com.bennellin.app.visitormanagementapp.utils.Bitmaps
import org.w3c.dom.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.xml.parsers.DocumentBuilderFactory


class EIDScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEidscanBinding
    private var selectedVisitorType: VisitorType? = null
    private var selectedVisitPurpose: VisitPurpose? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        binding = ActivityEidscanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
//        setContentView(R.layout.activity_eidscan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val receivedData = intent.getStringExtra("eid_data")


//        val gson = GsonBuilder()
//            .excludeFieldsWithoutExposeAnnotation()
//            .create()
//
//        val cardPublicData: CardPublicData = gson.fromJson(
//            receivedData,
//            CardPublicData::class.java
//        )

        Log.d("tag", "Eid data $receivedData")

        val extractedData = parseXML(receivedData!!.trimIndent())


        binding.eidNumber.setText(extractedData.idNumber)
        binding.cardNumber.setText(extractedData.cardNumber)

        binding.fullNameEnglish.setText("" + extractedData.fullNameEnglish)
        binding.fullNameArabic.setText("" + extractedData.fullNameArabic)
        binding.mobileNumber.setText("" + extractedData.mobilePhoneNumber)
        binding.issueDate.setText("" + extractedData.issueDate)
        binding.expiryDate.setText("" + extractedData.expiryDate)
        binding.dateOfBirth.setText("" + extractedData.dateOfBirth)
        binding.emailId.setText("" + extractedData.email)
        binding.companyName.setText("" + extractedData.companyNameEnglish)
        binding.nationality.setText("" + extractedData.nationality)


        displayPhoto(extractedData.cardHolderPhoto)

        getVisitorType(SharedPreferenceManager.getAuthToken("auth_token"))
        getVisitPurpose(SharedPreferenceManager.getAuthToken("auth_token"))


        binding.buttonCheckIn.setOnClickListener {
//            Toast.makeText(this, "call check-in api", Toast.LENGTH_SHORT).show()\

            callCheckInApi(
                SharedPreferenceManager.getAuthToken("auth_token"),
                extractedData,
                receivedData
            )

        }

        binding.buttonCancel.setOnClickListener {
            startActivity(
                Intent(
                    this, HomeActivityTab::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            finish()
        }

    }

    private fun callCheckInApi(
        authToken: String?,
        extractedData: displayData,
        receivedData: String
    ) {

        val requestBody = CheckInApiRequestBody(
            NameEnglish = " ${extractedData.fullNameEnglish}",
            NameArabic = " ${extractedData.fullNameArabic}",
            IdNumber = extractedData.idNumber,
            IdCardNumber = extractedData.cardNumber,
            IssueDate = extractedData.issueDate,
            ExpiryDate = extractedData.expiryDate,
            DateOfBirth = extractedData.dateOfBirth,
            NationalityCode = extractedData.nationality,
            Email = extractedData.email,
            Phone = extractedData.mobilePhoneNumber,
            RawData = receivedData,
            ProfilePictureBase64 = " ${extractedData.cardHolderPhoto}",
            VisitorType = selectedVisitorType!!.VisitorType,
            VisitPurpose = selectedVisitPurpose!!.VisitPurpose,
            IdAccessCard = " ${binding.accessCardNumber.text}",
            Remarks = "${binding.remarks.text}"
        )

        val call = RetrofitInstance.apiService.checkInRequest("Bearer $authToken", requestBody)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@EIDScanActivity, "Checked-in Successfully", Toast.LENGTH_SHORT
                    ).show()
                    println("Success: ${response.body()}")
                    redirectToHome()
                } else {
                    Toast.makeText(
                        this@EIDScanActivity,
                        "Something went wrong, please try again later..",
                        Toast.LENGTH_SHORT
                    ).show()
                    println("Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(
                    this@EIDScanActivity,
                    "Something went wrong, please try again later..",
                    Toast.LENGTH_SHORT
                ).show()
                println("Failure: ${t.message}")
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
                        this@EIDScanActivity, R.layout.spinner_item_layout, visitorPurpose
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
                        this@EIDScanActivity, R.layout.spinner_item_layout, visitorTypes
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


    data class displayData(
        val cardNumber: String,
        val idNumber: String,
        val fullNameEnglish: String,
        val fullNameArabic: String,
        val mobilePhoneNumber: String,
        val issueDate: String,
        val expiryDate: String,
        val nationality: String,
        val dateOfBirth: String,
        val companyNameEnglish: String,
        val email: String,
        val cardHolderPhoto: String
    )

    fun parseXML(xmlString: String): displayData {
        // Create a document builder
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()

        // Parse the input XML string
        val inputStream = xmlString.byteInputStream()
        val document: Document = builder.parse(inputStream)

        // Normalize the document to avoid issues with nested elements
        document.documentElement.normalize()

        // Extract the required fields from the XML
        val cardNumber = document.getElementsByTagName("CardNumber").item(0).textContent
        val idNumber = document.getElementsByTagName("IDNumber").item(0).textContent
        val fullNameEnglish =
            document.getElementsByTagName("FullNameEnglish").item(0).textContent.replace(",", " ")
        val fullNameArabic =
            document.getElementsByTagName("FullNameArabic").item(0).textContent.replace(",", " ")
        val mobilePhoneNumber =
            document.getElementsByTagName("MobilePhoneNumber").item(0).textContent
        val issueDate = document.getElementsByTagName("IssueDate").item(0).textContent
        val expiryDate = document.getElementsByTagName("ExpiryDate").item(0).textContent
        val nationality = document.getElementsByTagName("NationalityEnglish").item(0).textContent
        val dateOfBirth = document.getElementsByTagName("DateOfBirth").item(0).textContent
        val companyNameEnglish =
            document.getElementsByTagName("CompanyNameEnglish").item(0).textContent
        val email = document.getElementsByTagName("Email").item(0).textContent
        val cardHolderPhoto = document.getElementsByTagName("CardHolderPhoto").item(0).textContent


        return displayData(
            cardNumber,
            idNumber,
            fullNameEnglish,
            fullNameArabic,
            mobilePhoneNumber,
            issueDate,
            expiryDate,
            nationality,
            dateOfBirth,
            companyNameEnglish,
            email,
            cardHolderPhoto
        )
    }

    fun extractData(input: String): Map<String, String> {
        // Define regex patterns for required fields
        val patterns = mapOf(
            "Card Number" to "Card Number\\s*=\\s*(\\S+)",
            "ID Number" to "ID Number\\s*=\\s*(\\S+)",
            "Full Name" to "Full Name\\s*=\\s*([A-Z ,]+)",
            "Email" to "Email\\s*=\\s*([^\\s]+)",
            "Mobile Phone No" to "Mobile Phone No\\s*=\\s*(\\d+)"
        )

        // Extract data using the regex patterns
        return patterns.mapValues { (_, pattern) ->
            Regex(pattern).find(input)?.groupValues?.get(1) ?: "Not Found"
        }
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
        binding.profileImageView.setImageBitmap(
            Bitmaps.decodeSampledBitmapFromBytes(
                photo, 150, 150
            )
        )
    } //
}