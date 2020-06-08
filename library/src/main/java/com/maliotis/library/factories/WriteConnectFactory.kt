package com.maliotis.library.factories

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.tech.Ndef
import com.maliotis.library.typealiases.WriteNdefInterface
import java.lang.RuntimeException
import java.nio.charset.Charset
import java.util.*

/**
 * Created by petrosmaliotis on 03/06/2020.
 */
class WriteConnectFactory {
    companion object {

        internal val ndefFunction: WriteNdefInterface = {
            val tagTechnology = Ndef.get(it)
            tagTechnology.connect()
            tagTechnology
        }
    }
}