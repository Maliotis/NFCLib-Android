package com.maliotis.library.nfc

import android.app.Activity
import android.content.Intent
import android.nfc.*
import android.nfc.NdefRecord.*
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.nfc.tech.TagTechnology
import android.util.Log
import com.maliotis.library.NfcTech.MIFARE_CLASSIC
import com.maliotis.library.NfcTech.MIFARE_ULTRALIGHT
import com.maliotis.library.NfcTech.NDEF
import com.maliotis.library.NfcTech.NDEF_FORMATTABLE
import com.maliotis.library.NfcTech.NFCA
import com.maliotis.library.NfcTech.NFCB
import com.maliotis.library.NfcTech.NFCF
import com.maliotis.library.NfcTech.NFCV
import com.maliotis.library.factories.ReadConnectFactory
import com.maliotis.library.interfaces.ConnectInterface
import com.maliotis.library.interfaces.ReadNFCI
import com.maliotis.library.typealiases.ReadNdefInterface
import com.maliotis.library.typealiases.ReadNfcAInterface
import java.util.*
import kotlin.experimental.and

/**
 * Created by petrosmaliotis on 29/05/2020.
 */
class ReadNFC internal constructor(): ReadNFCI {
    override var activityContext: Activity? = null
    override var nfcManager: NfcManager? = null

    override var nfcTech: Class<out TagTechnology> = NDEF
    internal var ndefMessage: NdefMessage? = null
    // Language code from Text based payload
    var languageCode = ""

    var ndefFunction: ReadNdefInterface = ReadConnectFactory.ndefFunction
    var nfcAFunction: ReadNfcAInterface = ReadConnectFactory.nfcAFunction


    /**
     * Override this method to implement your own behaviour
     */
    var connectInterface: ConnectInterface = object: ConnectInterface {
        override fun attemptConnect(tag: Tag) {
            val technologies = tag.techList
            val tagTechs = listOf(*technologies)
            val nfcTechName = nfcTech.canonicalName

            Log.d("TAG", "attemptConnect: tagTechs = ${tagTechs.toString()}")

            if (tagTechs.contains(nfcTechName) && nfcTech == NDEF) {
                ndefMessage = ndefFunction(tag)

            } else if (tagTechs.contains(nfcTechName) && nfcTech == NFCA) {
                nfcAFunction(tag)

            } else if (tagTechs.contains(nfcTechName) && nfcTech == NFCB) {
                // TODO
            } else if (tagTechs.contains(nfcTechName) && nfcTech == NFCV) {
                // TODO
            } else if (tagTechs.contains(nfcTechName) && nfcTech == NFCF) {
                // TODO
            } else if (tagTechs.contains(nfcTechName) && nfcTech == MIFARE_CLASSIC) {
                // TODO
            } else if (tagTechs.contains(nfcTechName) && nfcTech == MIFARE_ULTRALIGHT) {
                // TODO
            }
        }

    }

    /**
     * Connects to read the cachedMessage and closes the connection immediately
     */
    override fun connect(intent: Intent) {
        val tag: Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        connectInterface.attemptConnect(tag)
    }

    /**
     * Returns the NDEFMessage.
     */
    override fun message(): NdefMessage? {
        return ndefMessage
    }

    /**
     * Returns the NDEFRecords.
     */
    override fun records(): Array<NdefRecord>? {
        return ndefMessage?.records
    }

    /**
     * Returns the payload (content) of the NFC TAG.
     */
    override fun payload(): Array<String> {

        // init with empty strings
        val payload = Array<String>(ndefMessage?.records?.size ?: 0) { "" }

        ndefMessage?.records?.forEachIndexed { index,  ndefRecord ->
            val payloadBytes = ndefRecord.payload
            when (ndefRecord.tnf) {
                // Absolute URI can be either URN, URI, URL
                TNF_ABSOLUTE_URI -> {
                    Log.d("TAG", "payload: TNF_ABSOLUTE_URI")
                    val type = String(ndefRecord.type, Charsets.UTF_8)
                    // the type will contain a literal URI
                    // i.e http://schemas.xmlsoap.org/soap/envelope/
                    // android treats the type as if it was the payload
                    payload[index] = type
                    val pd = String(ndefRecord.payload, Charsets.UTF_8)
                }

                TNF_EMPTY -> {
                    Log.d("TAG", "payload: TNF_EMPTY")
                }

                TNF_EXTERNAL_TYPE -> {
                    Log.d("TAG", "payload: TNF_EXTERNAL_TYPE")
                    val type = String(ndefRecord.type, Charsets.UTF_8)
                    // type will probably be android.com:pkg
                }

                TNF_MIME_MEDIA -> {
                    Log.d("TAG", "payload: TNF_MIME_MEDIA")
                    val type = String(ndefRecord.type, Charsets.UTF_8)
                    Log.d("TAG", "payload: type = $type")
                    if (type == "text/html") {
                        payload[index] = String(ndefRecord.payload, Charsets.UTF_8)
                    } else if (type == "text/json") {

                    } else if (type == "image/gif") {

                    } else {
                        payload[index] = String(ndefRecord.payload, Charsets.UTF_8)
                    }
                }

                // chunked record
                // concat the message from all records
                TNF_UNCHANGED -> {
                    Log.d("TAG", "payload: TNF_UNCHANGED")
                }

                TNF_UNKNOWN -> {
                    Log.d("TAG", "payload: TNF_UNKNOWN")
                }

                TNF_WELL_KNOWN -> {
                    Log.d("TAG", "payload: TNF_WELL_KNOWN")
                    val type = ndefRecord.type
                    if (Arrays.equals(type, RTD_TEXT)) {

                        // Text
                        val isUTF8: Boolean = payloadBytes[0] and 0x080.toByte() == 0.toByte() //status byte: bit 7 indicates encoding (0 = UTF-8, 1 = UTF-16)
                        val languageLength: Int = (payloadBytes[0] and 0x03F.toByte()).toInt() //status byte: bits 5..0 indicate length of language code
                        val textLength: Int = payloadBytes.size - 1 - languageLength
                        val utf = if (isUTF8) Charsets.UTF_8 else Charsets.UTF_16
                        val langCode = String(payloadBytes, 1, languageLength, Charsets.US_ASCII)// not sure about ASCII here
                        payload[index] = String(payloadBytes, 1 + languageLength, textLength, utf)

                    } else if (Arrays.equals(type, RTD_URI)) {
                        // URI
                        val prefix = getUriPrefix(payloadBytes = payloadBytes)
                        var prefixLength = 0
                        // Prefix can denote an action: tel number send mail e.t.c
                        if (prefix.isNotEmpty()) prefixLength = 1
                        val length = payloadBytes.size - prefixLength
                        val uriPayload = String(payloadBytes, 1, length, Charsets.UTF_8)
                        payload[index] = prefix + uriPayload

                    } else if (Arrays.equals(type, RTD_SMART_POSTER)) {
                        // Smart Poster
                    } else if (Arrays.equals(type, RTD_ALTERNATIVE_CARRIER)) {
                        // TODO
                    } else if (Arrays.equals(type, RTD_HANDOVER_CARRIER)) {
                        // TODO
                    } else if (Arrays.equals(type, RTD_HANDOVER_REQUEST)) {
                        // TODO
                    } else if (Arrays.equals(type, RTD_HANDOVER_SELECT)) {
                        // TODO
                    }
                }

            }
        }

        return payload
    }

    private fun getUriPrefix(payloadBytes: ByteArray): String {
        val prefix = payloadBytes[0]
        return when (prefix) {
            0x00.toByte() -> ""
            0x01.toByte() -> "http://www."
            0x02.toByte() -> "https://www."
            0x03.toByte() -> "http:/"
            0x04.toByte() -> "https://"
            0x05.toByte() -> "tel://"
            0x06.toByte() -> "mailto://"
            0x07.toByte() -> "ftp://anonymous:anonymous@"
            0x08.toByte() -> "ftp://ftp."
            0x09.toByte() -> "ftps://"
            0x0A.toByte() -> "sftp://"
            0x0B.toByte() -> "smb://"
            0x0C.toByte() -> "nfs://"
            0x0D.toByte() -> "ftp://"
            0x0E.toByte() -> "dav://"
            0x0F.toByte() -> "news:"
            0x10.toByte() -> "telnet://"
            0x11.toByte() -> "imap:"
            0x12.toByte() -> "rtsp://"
            0x13.toByte() -> "urn:"
            0x14.toByte() -> "pop:"
            0x15.toByte() -> "sip:"
            0x16.toByte() -> "sips:"
            0x17.toByte() -> "tftp:"
            0x18.toByte() -> "btspp://"
            0x19.toByte() -> "btl2cap://"
            0x1A.toByte() -> "btgoep://"
            0x1B.toByte() -> "tcpobex://"
            0x1C.toByte() -> "irdaobex://"
            0x1D.toByte() -> "file://"
            0x1E.toByte() -> "urn:epc:id:"
            0x1F.toByte() -> "urn:epc:tag:"
            0x20.toByte() -> "urn:epc:pat:"
            0x21.toByte() -> "urn:epc:raw:"
            0x22.toByte() -> "urn:epc:"
            else -> ""
        }

    }


}