package com.bennellin.app.visitormanagementapp.tab.otg

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import com.acs.smartcard.Reader

class CardReaderHelper(context: Context) {
    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    val reader = Reader(usbManager) // Make reader accessible

    fun connectToReader(device: UsbDevice): Boolean {
        return if (usbManager.hasPermission(device)) {
            reader.open(device)
            Log.d("CardReader", "Connected to ACS Reader")
            true
        } else {
            Log.e("CardReader", "No permission to access USB device")
            false
        }
    }

    fun closeReader() {
        reader.close()
    }
}
