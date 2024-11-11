package com.bennellin.app.visitormanagementapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.databinding.ActivityScanWithMrzBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
class ScanWithMRZ : AppCompatActivity() {

    private lateinit var binding: ActivityScanWithMrzBinding
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var imageCapture: ImageCapture
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanWithMrzBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
//        setContentView(R.layout.activity_scan_with_mrz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Camera Executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Capture image button
        binding.captureButton.setOnClickListener {
            takePhoto()
        }
    }

    // Starts the camera using CameraX and sets up the image analyzer
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        // Create a file to save the image
        val photoFile = File(externalMediaDirs.first(), "photo.jpg")

        // Set up the output options
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    imageUri = Uri.fromFile(photoFile)
                    processImage(imageUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@ScanWithMRZ,
                        "Photo capture failed: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun processImage(imageUri: Uri?) {
        imageUri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, it)
            val inputImage = InputImage.fromBitmap(bitmap, 0)

            // Use ML Kit's text recognizer to process the image
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    // Extract details and navigate to DisplayActivity
                    Log.d("RecognizedText", visionText.text)
                    navigateToDisplayActivity(visionText.text)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    private fun navigateToDisplayActivity(recognizedText: String) {
        val intent = Intent(this, DisplayDetail::class.java).apply {
            putExtra("recognized_text", recognizedText)
        }
        startActivity(intent)
    }

    @androidx.camera.core.ExperimentalGetImage
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // Use ML Kit's text recognizer to process the image
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    // Extract details from the recognized text
                    extractEmiratesIdDetails(visionText.text)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
                .addOnCompleteListener {
                    imageProxy.close()  // Close the image when done processing
                }
        }
    }

    // Function to extract Emirates ID details from recognized text
    @SuppressLint("SetTextI18n")
    private fun extractEmiratesIdDetails(text: String) {
        val lines = text.split("\n")

        var emiratesId = ""
        var name = ""
        var dateOfBirth = ""
        var expirationDate = ""

        // Regex for extracting Emirates ID and dates
        val idRegex = Regex("\\b[0-9]{15}\\b")
        val dateRegex = Regex("\\b[0-9]{2}/[0-9]{2}/[0-9]{4}\\b")

        // Parse through the text lines to extract relevant information
        for (line in lines) {
            // Match Emirates ID (15 digits)
            if (idRegex.containsMatchIn(line)) {
                emiratesId = idRegex.find(line)?.value ?: ""
            }

            // Match dates (e.g., date of birth or expiration date)
            if (dateRegex.containsMatchIn(line)) {
                if (dateOfBirth.isEmpty()) {
                    dateOfBirth = dateRegex.find(line)?.value ?: ""
                } else {
                    expirationDate = dateRegex.find(line)?.value ?: ""
                }
            }

            // Assuming the name is written in uppercase letters
            if (line == line.uppercase() && name.isEmpty()) {
                name = line
            }
        }

//        // Display the extracted details in the UI
//        binding.mrzTextView.text = """
//            Emirates ID: $emiratesId
//            Name: $name
//            Date of Birth: $dateOfBirth
//            Expiration Date: $expirationDate
//        """.trimIndent()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}