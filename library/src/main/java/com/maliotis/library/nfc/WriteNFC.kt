package com.maliotis.library.nfc

import android.app.Activity
import android.content.Intent
import android.nfc.*
import android.nfc.NdefRecord.*
import android.nfc.tech.MifareClassic
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.TagTechnology
import android.util.Log
import com.maliotis.library.NfcTech
import com.maliotis.library.factories.WriteConnectFactory
import com.maliotis.library.interfaces.ConnectInterface
import com.maliotis.library.interfaces.WriteNFCI
import com.maliotis.library.typealiases.WriteNdefInterface
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

/**
 * Created by petrosmaliotis on 29/05/2020.
 */
class WriteNFC: WriteNFCI {
    private val TAG = WriteNFC::class.java.canonicalName

    override var activityContext: Activity? = null
    override var nfcManager: NfcManager? = null
    override var nfcTech: Class<out TagTechnology> = NfcTech.NDEF

    private var tagTechnology: TagTechnology? = null

    var ndefFunction: WriteNdefInterface = WriteConnectFactory.ndefFunction

    /**
     * Use that only if Ndef or Ndef Formattable is set
     */
    var tnf = TNF_WELL_KNOWN
    var typeRecord = RTD_TEXT

    var localeLanguage: Locale = Locale.ENGLISH
    var utfEncoding: Charset = Charsets.UTF_8
    var applicationMIME = "application/com.example.yourapp".toByteArray(Charsets.US_ASCII)

    var connectInterface: ConnectInterface = object: ConnectInterface {
        override fun attemptConnect(tag: Tag) {
            val technologies = tag.techList
            val tagTechs = listOf(*technologies)
            val nfcTechName = nfcTech.canonicalName

            if (tagTechs.contains(nfcTechName) && nfcTech == NfcTech.NDEF) {
                tagTechnology = ndefFunction(tag)

            } else if (tagTechs.contains(nfcTechName) && nfcTech == NfcTech.NDEF_FORMATTABLE) {

            } else if (tagTechs.contains(nfcTechName) && nfcTech == NfcTech.MIFARE_CLASSIC) {
                // TODO
            } else if (tagTechs.contains(nfcTechName) && nfcTech == NfcTech.MIFARE_ULTRALIGHT) {
                // TODO
            }
        }

    }

    /**
     * Connects to the Tag without closing the connection
     */
    override fun connect(intent: Intent) {
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        connectInterface.attemptConnect(tag!!)
    }

    override fun close() {
        tagTechnology?.close()
    }

    // NDEF

    override fun write(content: String): Boolean {
        // construct content
        val payload = constructContent(content)
        // write it to tag
        val succeeded = writeToTag(payload)
        Log.d(TAG, "write: write to tag was successful")
        return succeeded
    }

    override fun write(content: ByteArray): Boolean {
        val payload = constructContent(content)
        // write it to tag
        val succeeded = writeToTag(payload)
        Log.d(TAG, "write: write to tag was successful")
        return succeeded
    }

    override fun write(message: NdefMessage): Boolean {
        val succeeded = writeToTag(message)
        Log.d(TAG, "write: write to tag was successful")
        return succeeded
    }

    private fun constructContent(content: String): Any {
        var ndefRecord: NdefRecord
        if (nfcTech == Ndef::class.java || nfcTech == NdefFormatable::class.java) {
            // return ndef record
            if (tnf == TNF_WELL_KNOWN) {
                ndefRecord = when {
                    Arrays.equals(typeRecord, RTD_TEXT) -> createTextRecord(content)
                    Arrays.equals(typeRecord, RTD_URI) -> createUriRecord(content)
                    else -> return Any()
                }
                return NdefMessage(ndefRecord)
            } else return Any()
        }

        return Any()
    }

    private fun constructContent(content: ByteArray): Any {
        var ndefRecord: NdefRecord
        if (nfcTech == Ndef::class.java || nfcTech == NdefFormatable::class.java) {
            when (tnf) {
                TNF_WELL_KNOWN -> {
                    ndefRecord = NdefRecord(tnf, typeRecord, ByteArray(0), content)
                }
                TNF_MIME_MEDIA -> {
                    ndefRecord = NdefRecord(tnf, applicationMIME, ByteArray(0), content)
                }
                TNF_ABSOLUTE_URI -> {
                    val emptyByteArray = ByteArray(0)
                    // Android treats the type as the payload in that case
                    ndefRecord = NdefRecord(tnf, content, emptyByteArray, emptyByteArray)
                }
                else -> {
                    return Any()
                }
            }
        } else return Any()

        return NdefMessage(ndefRecord)
    }

    // NDEF Helpers

    private fun createTextRecord(payload: String): NdefRecord {
        val langBytes = localeLanguage.language.toByteArray(Charsets.US_ASCII)
        val textBytes = payload.toByteArray(utfEncoding)
        val utfBit: Int = if (utfEncoding == Charsets.UTF_8) 0 else 1 shl 7
        val status = (utfBit + langBytes.size).toChar()
        val data = ByteArray(1 + langBytes.size + textBytes.size)
        data[0] = status.toByte()
        System.arraycopy(langBytes, 0, data, 1, langBytes.size)
        System.arraycopy(textBytes, 0, data, 1 + langBytes.size, textBytes.size)
        return NdefRecord(tnf, typeRecord, ByteArray(0), data)
    }

    private fun createUriRecord(payload: String): NdefRecord {
        val payloadByteArray = payload.toByteArray(utfEncoding)
        val emptyByteArray = ByteArray(0)
        return NdefRecord(tnf, payloadByteArray, emptyByteArray, emptyByteArray)
    }

    private fun writeToTag(content: Any): Boolean {
        if (content !is NdefMessage) return false

        when (nfcTech) {
            Ndef::class.java -> {
                val tag = tagTechnology as Ndef
                if (tag.maxSize < content.toByteArray().size) {
                    // Message to large to write to Nfc Tag
                    return false
                }
                return if (tag.isWritable) {
                    if (tag.isConnected) {
                        try {
                            // Attempt to write message
                            tag.writeNdefMessage(content)
                            true
                        } catch (e: IOException){
                            e.printStackTrace()
                            false
                        }

                    } else false // Nfc tag is not connected

                } else false // Nfc tag is read-only
            }
            NdefFormatable::class.java -> {
                val tag = tagTechnology as NdefFormatable
                return if (tag.isConnected) {
                    try {
                        // Attempt to write message
                        tag.format(content)
                        true
                    } catch (e: IOException){
                        e.printStackTrace()
                        false
                    }
                } else false // Nfc tag is not connected
            }
        }
        return false
    }

    // Mifare Classic

    // Mifare Classic Authenticate

    override fun authAllSectorsMifareClassicKeyA(keyA: ByteArray?): Boolean {
        // TODO("Not yet implemented")
        // Stub
        return true
    }

    override fun authAllSectorsMifareClassicKeyB(keyB: ByteArray?): Boolean {
        // TODO("Not yet implemented")
        // Stub
        return true
    }

    override fun authAllSectorsMifareClassicKeyAB(keyA: ByteArray?, keyB: ByteArray?): Boolean {
        // TODO("Not yet implemented")
        // Stub
        return true
    }

    override fun authSectorWithKeyA(sector: Int, keyA: ByteArray?): Boolean {
        // TODO("Not yet implemented")
        // Stub
        return true
    }

    override fun authSectorWithKeyB(sector: Int, keyB: ByteArray?): Boolean {
        // TODO("Not yet implemented")
        // Stub
        return true
    }

    override fun authSectorWithKeyAB(sector: Int, keyA: ByteArray?, keyB: ByteArray?): Boolean {
        // TODO("Not yet implemented")
        // Stub
        return true
    }

    // Mifare Classic Write

    override fun writePayloadToAllBlocks(byteArray: ByteArray): Boolean {
        // TODO("Not yet implemented")
        // Stub
        return true
    }

    override fun writeBlock(blockIndex: Int, byteArray: ByteArray): Boolean {
        // TODO("Not yet implemented")
        // Stub
        return true
    }

    // Mifare Classic Block/Sector

    override fun getBlockCount(): Int {
        // TODO("Not yet implemented")
        // Stub
        return 1
    }

    override fun getBlockCountInSector(sectorIndex: Int): Int {
        // TODO("Not yet implemented")
        // Stub
        return 1
    }

    override fun getSectorCount(): Int {
        // TODO("Not yet implemented")
        // Stub
        return 1
    }

    override fun sectorToBlock(sectorIndex: Int): Int {
        // TODO("Not yet implemented")
        // Stub
        return 1
    }

    // Mifare Helpers

    /**
     * Auth all sectors
     * If one fails to authenticate will return false
     */
    private fun authMifareClassic(content: String): Any {
        val mifareClassic = tagTechnology as MifareClassic
        val size = mifareClassic.size
        when (size) {
            MifareClassic.SIZE_MINI -> { // 5 sectors, each with 4 blocks
                // auth all of the sectors with the default key
                for (i in 0 until 5) {
                    val auth = authenticateSectors(mifareClassic, i)
                    if (!auth) return false
                }

            }
            MifareClassic.SIZE_1K -> { // 16 sectors, each with 4 blocks
                for (i in 0 until 16) {
                    val auth = authenticateSectors(mifareClassic, i)
                    if (!auth) return false
                }

            }
            MifareClassic.SIZE_2K -> { // 32 sectors, each with 4 blocks
                for (i in 0 until 32) {
                    val auth = authenticateSectors(mifareClassic, i)
                    if (!auth) return false
                }

            }
            MifareClassic.SIZE_4K -> { // 40 sectors. The first 32 sectors contain 4 blocks and the
                // last 8 sectors contain 16 blocks
                for (i in 0 until 40) {
                    val auth = authenticateSectors(mifareClassic, i)
                    if (!auth) return false
                }
            }
        }

        return true
    }

    /**
     * We use 2 keys per sector to authenticate.
     * First block of the first sector is always reserved.
     *
     * The last block of each sector or the sector trailer is reserved as well
     * 12 bytes are reserved for KeyA and KeyB (if only KeyA or KeyB is in use then only 6 bytes
     * are reserved), 3 bytes are reserved for the access conditions, the remaining bytes(7)/byte(1)
     * can be used to store user data.
     */
    private fun authenticateSectors(mifareClassic: MifareClassic, i: Int, byteArray: ByteArray? = null,
                                    keyA: Boolean = true, keyB: Boolean = true): Boolean {
        var authA = false
        var authB = false

        if (byteArray != null) {
            if (keyA) authA = mifareClassic.authenticateSectorWithKeyA(i, MifareClassic.KEY_DEFAULT)
            if (keyB) authB = mifareClassic.authenticateSectorWithKeyB(i, MifareClassic.KEY_DEFAULT)
        }

        // if auth with byteArray failed try the default keys
        if ((!authA && keyA) || (!authB && keyB)) {
            authA = mifareClassic.authenticateSectorWithKeyA(i, MifareClassic.KEY_DEFAULT)
            authB = mifareClassic.authenticateSectorWithKeyB(i, MifareClassic.KEY_DEFAULT)
            if ((!authA && keyA) || (!authB && keyB)) {
                authA = mifareClassic.authenticateSectorWithKeyA(
                    i,
                    MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY
                )
                authB = mifareClassic.authenticateSectorWithKeyB(
                    i,
                    MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY
                )
                if ((!authA && keyA) || (!authB && keyB)) {
                    authA = mifareClassic.authenticateSectorWithKeyA(i, MifareClassic.KEY_NFC_FORUM)
                    authB = mifareClassic.authenticateSectorWithKeyB(i, MifareClassic.KEY_NFC_FORUM)
                }
            }
        }

        return authA && authB
    }


}