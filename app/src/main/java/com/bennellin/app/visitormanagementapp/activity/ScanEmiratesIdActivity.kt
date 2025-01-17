package com.bennellin.app.visitormanagementapp.activity

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bennellin.app.visitormanagementapp.R
import java.io.IOException

class ScanEmiratesIdActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scan_emirates_id)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_LONG).show()
            finish() // Terminate if NFC is not available
        }
    }

    override fun onResume() {
        super.onResume()
        enableForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        disableForegroundDispatch()
    }

    private fun enableForegroundDispatch() {
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val filters = arrayOf<IntentFilter>()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, null)
    }

    private fun disableForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null && NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)

            Log.d("TAG", "NFC_tag: $tag")
            tag?.let { handleTag(it) }
        }
    }

    private fun handleTag(tag: Tag) {
        val isoDep = IsoDep.get(tag)
        isoDep?.let {
            try {
                isoDep.connect()
                readEmiratesIdData(isoDep)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isoDep.close()
            }
        }
    }

    private fun readEmiratesIdData(isoDep: IsoDep) {
        try {
            // Example APDU command to select Emirates ID applet (dummy value)
            val selectCommand = byteArrayOf(
                0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(), 0x0A.toByte(),
                0xA0.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte(),
                0x51.toByte(), 0x43.toByte(), 0x30.toByte(), 0x31.toByte()
            )
            val response = isoDep.transceive(selectCommand)

            // Process the response from the card and extract public data
            val emiratesIdData = processEmiratesIdResponse(response)
            println("Emirates ID Data: $emiratesIdData")

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun processEmiratesIdResponse(response: ByteArray): String {
        // Dummy processing logic, this needs to be adapted based on the actual structure of the response
        // Example: Convert the byte array to a readable string
        return response.joinToString(separator = "") { byte -> "%02x".format(byte) }
    }

}