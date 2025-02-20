package com.bennellin.app.visitormanagementapp.tab.activity

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.databinding.ActivityEidscanOtgBinding
import com.bennellin.app.visitormanagementapp.tab.fragments.PublicDataReadingFragment
import com.bennellin.app.visitormanagementapp.tab.otg.CardReaderHelper
import com.bennellin.app.visitormanagementapp.tab.tasks.CardReaderConnectionTask

class EIDScanOTGActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEidscanOtgBinding
    private lateinit var cardReaderHelper: CardReaderHelper
    private var fragment: Fragment? = null

    //    private lateinit var usbManager: UsbManager
    private val ACTION_USB_PERMISSION = "com.bennellin.app.visitormanagementapp.USB_PERMISSION"

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        binding = ActivityEidscanOtgBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        val filter = IntentFilter(ACTION_USB_PERMISSION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(usbPermissionReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(usbPermissionReceiver, filter)
        }
        cardReaderHelper = CardReaderHelper(this)

        checkForUsbDevice()


    }

    private fun loadFragment() {
        fragment = PublicDataReadingFragment().apply {
            arguments = Bundle().apply {
                putString("source", "OTG")
            }
        }
        supportFragmentManager.beginTransaction().replace(
            R.id.root,
            fragment as PublicDataReadingFragment
        ).commit()

        Handler(Looper.getMainLooper()).post {
            (fragment as? PublicDataReadingFragment)?.setNfcMode(null)
        }
    }


    private fun checkForUsbDevice() {
        val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList

        Log.d("tag", usbManager.deviceList.toString())
        for (device in deviceList.values) {
            if (device.vendorId == 1839 && device.productId == 45312) { // Modify based on your reader

                val permissionIntent = PendingIntent.getBroadcast(
                    this, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE
                )

                if (!usbManager.hasPermission(device)) {
                    usbManager.requestPermission(device, permissionIntent)
                    Log.d("CardReader", "Requesting USB Permission...")
                } else {
                    cardReaderHelper.connectToReader(device)
                    Log.d("CardReader", "USB Permission Already Granted")
                    // Call readEmiratesID after successfully connecting the reader

                    val cardReaderConnectionTask =
                        CardReaderConnectionTask(connectToolkitListener, true)
                    cardReaderConnectionTask.execute()
                    loadFragment()
                }
                break
            }
        }
    }

    private val connectToolkitListener = object : CardReaderConnectionTask.ConnectToolkitListener {
        override fun onToolkitConnected(status: Int, isConnectFlag: Boolean, message: String) {
            if (!isConnectFlag) {
                Toast.makeText(this@EIDScanOTGActivity, "Disconnected", Toast.LENGTH_SHORT).show()
            }
        }
    }


//    private fun readEmiratesID() {
//        // Step 1: Power on the card (if needed)
////        powerOnCard()
//
//        // Step 2: Select Emirates ID application
//        val selectCardApdu = byteArrayOf(
//            0x00,
//            0xA4.toByte(),
//            0x04,
//            0x00,
//            0x10,
//            0xD2.toByte(),
//            0x76,
//            0x00,
//            0x00,
//            0x01,
//            0x44,
//            0x10,
//            0x01,
//            0x00,
//            0x00,
//            0x00,
//            0x01,
//            0x01,
//            0x02
//        )
////        sendApduCommand(selectCardApdu)
//
//        // Step 3: Read Emirates ID Number
//        val idNumber = sendApduCommand(byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00, 0x10))
//        Log.d("CardReader", "ID Number: ${parseData(idNumber)}")
//
////        // Step 4: Read Full Name
////        val fullName = sendApduCommand(byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x10, 0x20))
////        Log.d("CardReader", "Full Name: ${parseData(fullName)}")
////
////        // Step 5: Read Expiry Date
////        val expiryDate = sendApduCommand(byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x30, 0x10))
////        Log.d("CardReader", "Expiry Date: ${parseData(expiryDate)}")
//    }

//    private fun powerOnCard() {
//        try {
//            // Attempt using a different reset type or add a delay
//            val result = cardReaderHelper.reader.power(0, Reader.CARD_WARM_RESET)
//            if (result != null) {
//                Log.d("CardReader", "Card powered on successfully.")
//                // Adding a small delay to ensure the card is ready
//                Thread.sleep(500)  // Sleep for 500ms (adjust based on your reader's requirements)
//            } else {
//                Log.e("CardReader", "Failed to power on card.")
//            }
//        } catch (e: Exception) {
//            Log.e("CardReader", "Error powering on card: $e")
//        }
//    }
//
//    private fun powerOnCard() {
//        var retryCount = 0
//        var success = false
//
//        while (retryCount < 3 && !success) {
//            try {
//                val result = cardReaderHelper.reader.power(0, Reader.CARD_COLD_RESET)
//                if (result != null) {
//                    Log.d("CardReader", "Card powered on successfully.")
//                    success = true
//                    readEmiratesID()
//                } else {
//                    Log.e("CardReader", "Failed to power on card.")
//                }
//            } catch (e: RemovedCardException) {
//                Log.e("CardReader", "Card removed unexpectedly.")
//                retryCount++
//                Thread.sleep(1000) // Wait 1 second before retrying
//            } catch (e: Exception) {
//                Log.e("CardReader", "Error powering on card: ${e.message}")
//                retryCount++
//                Thread.sleep(1000) // Wait 1 second before retrying
//            }
//        }
//
//        if (!success) {
//            Log.e("CardReader", "Failed to power on card after multiple attempts.")
//        }
//    }


    private fun sendApduCommand(command: ByteArray): ByteArray {
        val response = ByteArray(256) // Response buffer, you can increase the size if needed
        var retryCount = 0

        while (retryCount < 3) {  // Retry 3 times if needed
            try {
                // Wait for the card to be ready
                Thread.sleep(500) // Adjust this if the card needs more time

                val bytesRead = cardReaderHelper.reader.transmit(
                    0,
                    command,
                    command.size,
                    response,
                    response.size
                )

                if (bytesRead < 0) {
                    Log.e("CardReader", "Error in APDU transmission")
                    retryCount++
                    continue  // Retry the command
                }

                // Successfully transmitted, return the response
                Log.d("CardReader", "APDU Command Successful, Bytes Read: $bytesRead")
                return response.copyOf(bytesRead) // Trim the response to actual length
            } catch (e: Exception) {
                Log.e("CardReader", "Error sending APDU command: ${e.message}")
                retryCount++
                Thread.sleep(500)  // Delay before retrying
            }
        }

        // If all retries fail, return an empty byte array
        Log.e("CardReader", "All retries failed for APDU transmission")
        return byteArrayOf()
    }


//    private fun sendApduCommand(command: ByteArray): ByteArray {
//        val response = ByteArray(256) // Response buffer
//        try {
//            // Add a small delay to ensure the card is in the correct state
//            Thread.sleep(200)  // Sleep for 200ms (adjust based on your needs)
//
//            val bytesRead =
//                cardReaderHelper.reader.transmit(0, command, command.size, response, response.size)
//
//            if (bytesRead < 0) {
//                Log.e("CardReader", "Error in APDU transmission")
//                return byteArrayOf() // Return empty byte array on error
//            }
//            return response.copyOf(bytesRead) // Trim response to actual length
//        } catch (e: Exception) {
//            Log.e("CardReader", "Error sending APDU command: ${e.message}")
//            return byteArrayOf()
//        }
//    }

    private fun parseData(data: ByteArray): String {
        return data.filter { it.toInt() > 0 } // Remove empty bytes
            .map { it.toChar() } // Convert bytes to characters
            .joinToString("")
            .trim()
    }

    override fun onDestroy() {
        super.onDestroy()
        cardReaderHelper.closeReader()
    }

    private val usbPermissionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.let {
                            cardReaderHelper.connectToReader(it)
                            // After permission is granted, call readEmiratesID
//                            readEmiratesID() // Added this line
                            val cardReaderConnectionTask =
                                CardReaderConnectionTask(connectToolkitListener, true)
                            cardReaderConnectionTask.execute()
                            loadFragment()

                        }
                    } else {
                        Log.e("CardReader", "USB Permission Denied")
                    }
                }
            }
        }
    }
}
