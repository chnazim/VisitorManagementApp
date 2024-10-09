package com.bennellin.app.visitormanagementapp.activity

import ae.emiratesid.idcard.toolkit.Toolkit
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcV
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.databinding.ActivityScanBinding
import com.bennellin.app.visitormanagementapp.general.InitializeToolkitTask
import java.io.IOException

class ScanActivity : AppCompatActivity() {

    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var binding: ActivityScanBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

//        setContentView(R.layout.activity_scan)

        val gateId = intent.getIntExtra("gate_id", -1)
        val gateName = intent.getStringExtra("gate_name")

        binding.gateName.text = "Gate Name : $gateName"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initialize()

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (!nfcAdapter.isEnabled) {
            Toast.makeText(this, "Please enable NFC in the settings", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initialize() {
        val initializeToolkitTask: InitializeToolkitTask =
            InitializeToolkitTask(mInitializationListener)
        initializeToolkitTask.execute()
    }

    private val mInitializationListener: InitializeToolkitTask.InitializationListener =
        object : InitializeToolkitTask.InitializationListener {
            override fun onToolkitInitialized(isSuccessful: Boolean, statusMessage: String) {

                if (isSuccessful) {
//                    showMessage("$statusMessage\nPlease tap your NFC card to read public data.")

                } else {
//                    showMessage(statusMessage)
                }
            }
        }


    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
//        val configString = readConfigFromAssets(this, "config_li")

        val configString = buildConfigParams()
//        val configFileNames = listOf(
//            "config_ag",
//            "config_li",
//            "config_lv_qa",
//            "config_pg",
//            "config_tk_qa",
//            "config_vg_qa"
//        )

//        val configString = readMultipleConfigsFromAssets(this, configFileNames)

        Log.d("TAG", "configString: $configString")

        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent?.action) {
            intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)?.let { tag ->
                // Initialize the toolkit
                try {
                    val toolkit = Toolkit(true, configString)
                    toolkit.setNfcMode(tag)
                    toolkit.readerWithEmiratesID

                    val visitorIntent = Intent(this, VisitorDetailActivity::class.java)
                    startActivity(visitorIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        "Error initializing Toolkit: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                    val visitorIntent = Intent(this, VisitorDetailActivity::class.java)
                    startActivity(visitorIntent)
                }

            }
        }
    }

    fun readConfigFromAssets(context: Context, fileName: String): String {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            ""
        }
    }

    fun readMultipleConfigsFromAssets(context: Context, fileNames: List<String>): List<String> {
        val configs = mutableListOf<String>()

        for (fileName in fileNames) {
            try {
                context.assets.open(fileName).use { inputStream ->
                    val size = inputStream.available()
                    val buffer = ByteArray(size)
                    inputStream.read(buffer)
                    configs.add(String(buffer, Charsets.UTF_8))
                }
            } catch (ex: IOException) {
                ex.printStackTrace() // Log the exception
                configs.add("") // Add an empty string for failed files or handle it as needed
            }
        }

        return configs
    }


    private fun buildConfigParams(): String {
        val stringBuilder = StringBuilder()

        val configFileNames = listOf(
            "config_ag",
            "config_li",
            "config_lv_qa",
            "config_pg",
            "config_tk_qa",
            "config_vg_qa"
        )

        // Append various strings
        stringBuilder.appendLine("vg_url = http://vg-pre-prod.ica.gov.ae/ValidationGatewayService")
        stringBuilder.appendLine("vg_connection_timeout = 60")
        stringBuilder.appendLine("read_publicdata_offline = true")
        stringBuilder.appendLine("agent_tls_enabled = false")
        stringBuilder.appendLine("enable_digital_signature_service = false")

        return stringBuilder.toString()
    }

    private fun readEmiratesId(tag: Tag) {
        val nfcV = NfcV.get(tag)
        try {
            nfcV.connect()

            // Replace with the actual command for reading Emirates ID
            val command = byteArrayOf(/* Your command to read data */)
            val response = nfcV.transceive(command)

            // Parse the response and extract the necessary data
            val emiratesIdData = parseEmiratesIdResponse(response)

            Toast.makeText(this, "Emirates ID Data: $emiratesIdData", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error reading data: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            try {
                nfcV.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun parseEmiratesIdResponse(response: ByteArray): String {
        // Implement your parsing logic based on the expected response format
        return response.toString(Charsets.UTF_8) // Example conversion
    }
}
