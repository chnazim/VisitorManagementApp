package com.bennellin.app.visitormanagementapp.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.databinding.ActivityDisplayDetailBinding

class DisplayDetail : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
//        setContentView(R.layout.activity_display_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val recognizedText = intent.getStringExtra("recognized_text") ?: ""
        val linesList: List<String> =
            recognizedText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

        binding.textViewRecognizedData.text = extractEmiratesIdDetails(linesList)
    }



    private fun extractEmiratesIdDetails(lines: List<String>): String {
        var emiratesId = ""
        var name = ""
        var dateOfBirth = ""
        var issuingDate = ""
        var expirationDate = ""

        // Regex for extracting Emirates ID (adjusted for more flexibility)
        val idRegex = Regex("\\d{3}-\\d{4}-\\d{6}-\\d{1}")
        // Regex for extracting dates (dd/MM/yyyy)
        val dateRegex = Regex("\\d{2}/\\d{2}/\\d{4}")

        // Log the recognized lines for debugging
        Log.d("RecognizedTextLines", lines.joinToString("\n"))

        // Iterate through each line
        for (i in lines.indices) {
            val line = lines[i].trim() // Trim whitespace from each line

            Log.d("LineBeingProcessed", line) // Log each line being processed

            // Extract Emirates ID
            if (idRegex.containsMatchIn(line)) {
                emiratesId = idRegex.find(line)?.value ?: ""
                Log.d("ExtractedEmiratesID", emiratesId) // Log the extracted ID
            }

            // Extract Name
            if (line.startsWith("Name:", ignoreCase = true)) {
                name = line.substringAfter("Name:").trim().takeIf { it.isNotEmpty() } ?: ""
                Log.d("ExtractedName", name) // Log the extracted name
            }

            // Extract Date of Birth
            if (line.startsWith("Date of Birth:", ignoreCase = true)) {
                dateOfBirth = line.substringAfter("Date of Birth:").trim().takeIf { it.isNotEmpty() } ?: ""
                Log.d("ExtractedDateOfBirth", dateOfBirth) // Log the extracted date of birth
            } else if (dateOfBirth.isEmpty() && dateRegex.containsMatchIn(line)) {
                dateOfBirth = dateRegex.find(line)?.value ?: ""
                Log.d("ExtractedDateOfBirthFromPattern", dateOfBirth)
            }

            // Extract Issuing Date
            if (line.startsWith("Issuing Date", ignoreCase = true)) {
                issuingDate = line.substringAfter("Issuing Date").trim().takeIf { it.isNotEmpty() } ?: ""
                Log.d("ExtractedIssuingDate", issuingDate) // Log the extracted issuing date
            } else if (issuingDate.isEmpty() && dateRegex.containsMatchIn(line)) {
                issuingDate = dateRegex.find(line)?.value ?: ""
                Log.d("ExtractedIssuingDateFromPattern", issuingDate)
            }

            // Extract Expiration Date
            if (line.startsWith("Expiry Date", ignoreCase = true)) {
                expirationDate = line.substringAfter("Expiry Date").trim().takeIf { it.isNotEmpty() } ?: ""
                Log.d("ExtractedExpirationDate", expirationDate) // Log the extracted expiration date
            } else if (expirationDate.isEmpty() && dateRegex.containsMatchIn(line)) {
                expirationDate = dateRegex.find(line)?.value ?: ""
                Log.d("ExtractedExpirationDateFromPattern", expirationDate)
            }

            // Handling multiline cases for ID Number and Date of Birth
            if (emiratesId.isEmpty() && line.contains("ID Number", ignoreCase = true)) {
                emiratesId = lines.getOrNull(i + 1)?.trim() ?: ""
                Log.d("ExtractedEmiratesIDFromNextLine", emiratesId) // Log the extracted ID from next line
            }

            // If Date of Birth is not found yet, check for the next line
            if (dateOfBirth.isEmpty() && line.contains("Date of Birth", ignoreCase = true)) {
                dateOfBirth = lines.getOrNull(i + 1)?.trim() ?: ""
                Log.d("ExtractedDateOfBirthFromNextLine", dateOfBirth) // Log the extracted date of birth from next line
            }
        }

        // Final log of all extracted details
        Log.d("EmiratesIDDetails", "Emirates ID: $emiratesId")
        Log.d("EmiratesIDDetails", "Name: $name")
        Log.d("EmiratesIDDetails", "Date of Birth: $dateOfBirth")
        Log.d("EmiratesIDDetails", "Issuing Date: $issuingDate")
        Log.d("EmiratesIDDetails", "Expiration Date: $expirationDate")

        return """
        Emirates ID: $emiratesId
        Name: $name
        Date of Birth: $dateOfBirth
        Issuing Date: $issuingDate
        Expiration Date: $expirationDate
    """.trimIndent()
    }



}