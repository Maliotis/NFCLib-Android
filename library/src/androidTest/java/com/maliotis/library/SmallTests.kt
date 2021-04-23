package com.maliotis.library


import android.nfc.NdefMessage
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.maliotis.library.factories.NFCFactory
import com.maliotis.library.factories.ReadNFCFactory
import com.maliotis.library.factories.WriteNFCFactory
import com.maliotis.library.nfc.ReadNFC
import com.maliotis.library.nfc.WriteNFC
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SmallTests {

    @Test
    fun nfcFactory_returns_readNfc() {
        val scenario = launchActivity<TestActivityForNFCLib>()
        scenario.onActivity { activity ->
            val readNFCFactory = NFCFactory.create<ReadNFC>(activity)
            assertThat(readNFCFactory::class.java).isEqualTo(ReadNFCFactory::class.java)
        }
    }

    @Test
    fun nfcFactory_returns_writeNfc() {
        val scenario = launchActivity<TestActivityForNFCLib>()
        scenario.onActivity { activity ->
            val writeNFCFactory = NFCFactory.create<WriteNFC>(activity)
            assertThat(writeNFCFactory::class.java).isEqualTo(WriteNFCFactory::class.java)
        }
    }

    @Test
    fun test_payload_with_simple_text() {
        val text = "Hello from NfcLib :)"
        val record = createRecordText(text, Locale.ENGLISH, true)
        val myNdefMessage = NdefMessage(record) // create an NDEF message

        val readNFC = ReadNFC()
        readNFC.ndefMessage = myNdefMessage
        val payload = readNFC.payload()
        assertThat(payload[0]).isEqualTo(text)
    }


    @Test
    fun test_payload_with_simple_uri() {
        val uri = "https://developer.android.com/index.html"
        val record = createRecordURI(uri)
        val myNdefMessage = NdefMessage(record) // create an NDEF message

        val readNFC = ReadNFC()
        readNFC.ndefMessage = myNdefMessage
        val payload = readNFC.payload()
        assertThat(payload[0]).isEqualTo(uri)
    }

    @Test
    fun test_payload_with_multiple_records_text() {
        val textFirstRecord = "Simple text for 1st record"
        val textSecondRecord = "Simple text for 2nd record"

        val firstRecord = createRecordText(textFirstRecord, Locale.ENGLISH, true)
        val secondRecord = createRecordText(textSecondRecord, Locale.ENGLISH, true)

        val ndefMessage = NdefMessage(firstRecord, secondRecord)
        val readNFC = ReadNFC()
        readNFC.ndefMessage = ndefMessage
        val payload = readNFC.payload()
        assertThat(payload[0]).isEqualTo(textFirstRecord)
        assertThat(payload[1]).isEqualTo(textSecondRecord)
    }

    @Test
    fun test_payload_with_multiple_records_uri() {
        val uriFirstRecord = "https://developer.android.com/index.html"
        val uriSecondRecord = "https://developer.android.com/guide/topics/connectivity/nfc/nfc"

        val firstRecord = createRecordURI(uriFirstRecord)
        val secondRecord = createRecordURI(uriSecondRecord)

        val ndefMessage = NdefMessage(firstRecord, secondRecord)
        val readNFC = ReadNFC()
        readNFC.ndefMessage = ndefMessage
        val payload = readNFC.payload()
        assertThat(payload[0]).isEqualTo(uriFirstRecord)
        assertThat(payload[1]).isEqualTo(uriSecondRecord)
    }

}
