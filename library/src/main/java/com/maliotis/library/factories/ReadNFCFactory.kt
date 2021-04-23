package com.maliotis.library.factories

import android.app.Activity
import android.content.Intent
import com.maliotis.library.nfc.ReadNFC

/**
 * Created by petrosmaliotis on 29/05/2020.
 */
class ReadNFCFactory() : NFCFactory<ReadNFC>() {

    override fun getNFC(activity: Activity): ReadNFC {
        val readNFC = ReadNFC()
        readNFC.let {
            it.activityContext = activity
            it.nfcManager = nfcManager
        }

        return readNFC
    }

    override fun handleIntent(intent: Intent) {
        TODO("Not yet implemented")
    }
}