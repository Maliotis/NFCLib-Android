package com.maliotis.library.factories

import android.content.Intent
import com.maliotis.library.nfc.WriteNFC

/**
 * Created by petrosmaliotis on 29/05/2020.
 */
class WriteNFCFactory: NFCFactory<WriteNFC>() {

    override fun getNFC(): WriteNFC {
        val writeNFC = WriteNFC()
        writeNFC.let {
            it.activityContext = activityContext
            it.nfcManager = nfcManager
        }

        return writeNFC
    }

    override fun handleIntent(intent: Intent) {
        TODO("Not yet implemented")
    }
}