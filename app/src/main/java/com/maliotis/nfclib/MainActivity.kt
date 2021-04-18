package com.maliotis.nfclib

import android.content.Intent
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.maliotis.library.NfcTech
import com.maliotis.library.NfcTech.NDEF_FORMATTABLE
import com.maliotis.library.NfcTech.NFCA
import com.maliotis.library.factories.NFCFactory
import com.maliotis.library.interfaces.ConnectInterface
import com.maliotis.library.nfc.ReadNFC
import com.maliotis.library.nfc.WriteNFC
import com.maliotis.library.typealiases.ReadNfcAInterface
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var readNFC: ReadNFC
    lateinit var writeNFC: WriteNFC

    var readAction = true
    var writeAction = false

    override fun onResume() {
        super.onResume()
        val readNfcFactory = NFCFactory.create<ReadNFC>(this)
        val writeNfcFactory = NFCFactory.create<WriteNFC>(this)

        readNFC = readNfcFactory.getNFC()
        readNFC?.enableNFCInForeground()

        // No need to enable NFC in foreground again..
        writeNFC = writeNfcFactory.getNFC()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ...
        // ...


    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            readNFC.connect(intent)
            readNFC.nfcTech = NfcTech.NDEF
            val payload = readNFC.payload()
            Log.d("TAG", "onNewIntent: ${payload}")

        } else if (intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            // Determine the action

            if (writeAction) {

                writeNFC.connect(intent)

                writeNFC.write("Hello from NFCLib :)")

                writeNFC.close()
            }

            if (readAction) {
                readNFC.nfcTech = NFCA
                readNFC.connect(intent)

                val payload = readNFC.payload()
                // after accesing payload
                payload.forEach {
                    Log.d("TAG", "onNewIntent: $it")
                }
            }

        }


    }
}
