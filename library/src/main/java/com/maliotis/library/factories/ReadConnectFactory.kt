package com.maliotis.library.factories


import android.nfc.NdefMessage
import android.nfc.tech.Ndef
import com.maliotis.library.typealiases.ReadNdefInterface
import com.maliotis.library.typealiases.ReadNfcAInterface
import java.lang.RuntimeException

/**
 * Created by petrosmaliotis on 29/05/2020.
 */
class ReadConnectFactory {

    companion object {

        internal val ndefFunction: ReadNdefInterface = { tag ->
            val ndefTag = Ndef.get(tag)
            var ndefMessage: NdefMessage? = null
            ndefTag.let {
                it.connect()
                ndefMessage = it.cachedNdefMessage
                it.close()
            }
            if (ndefMessage == null) throw RuntimeException("Couldn't connect to nfc tag\n" +
                    "Ndef Message is null\n" +
                    "Exiting..")
            ndefMessage!!
        }

        val nfcAFunction: ReadNfcAInterface = {

        }
    }
}