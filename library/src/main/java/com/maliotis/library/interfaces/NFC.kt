package com.maliotis.library.interfaces

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.TagTechnology
import com.maliotis.library.NFCEnums

/**
 * Created by petrosmaliotis on 28/05/2020.
 */
interface NFC {
    var activityContext: Activity?
    var nfcManager: NfcManager?
    var nfcTech: Class<out TagTechnology>


    /**
     * By enabling Nfc in foreground the app will be consuming all Nfc Intents.
     */
    fun enableNFCInForeground() {
        val pendingIntent = PendingIntent.getActivity(
            activityContext, 0,
            Intent(activityContext, activityContext?.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        val ndefDetected = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        val techDetected = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        val filters = arrayOf(techDetected, tagDetected, ndefDetected)

        val TechLists = arrayOf(arrayOf(Ndef::class.java.name), arrayOf(NdefFormatable::class.java.name))

        nfcManager?.defaultAdapter?.enableForegroundDispatch(activityContext, pendingIntent, filters, TechLists)
    }

    /**
     * Disabled Nfc in foreground and will only get intents related to that app.
     */
    fun disableNFCInForeground(nfcAdapter: NfcAdapter, activity: Activity) {
        nfcAdapter.disableForegroundDispatch(activity)
    }

    /**
     * Connects to the NFC TAG
     */
    fun connect(intent: Intent)

    /**
     * Returns whether Nfc is supported and if it's enabled or not.
     */
    fun nfcSupport(): NFCEnums {
        val manager = activityContext?.getSystemService(Context.NFC_SERVICE) as NfcManager
        val adapter = manager.defaultAdapter
        return if (adapter != null && adapter.isEnabled) {
            // Yes NFC available
            NFCEnums.SUPPORTED_ENABLED
        } else if (adapter != null && !adapter.isEnabled) {
            //NFC is not enabled.Need to enable by the user.
            NFCEnums.SUPPORTED_DISABLED
        } else {
            //NFC is not supported
            NFCEnums.NOT_SUPPORTED
        }
    }
}