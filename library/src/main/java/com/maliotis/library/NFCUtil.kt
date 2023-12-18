package com.maliotis.library

import android.nfc.NdefRecord
import android.nfc.NdefRecord.TNF_ABSOLUTE_URI
import java.nio.charset.Charset
import java.util.*


/** Create record with type [NdefRecord.TNF_WELL_KNOWN] */
internal fun createRecordText(text: String, locale: Locale, encodeInUtf8: Boolean): NdefRecord {

    val langBytes: ByteArray = locale.language.toByteArray(Charsets.US_ASCII)
    val utfEncoding: Charset = if (encodeInUtf8) Charsets.UTF_8 else Charset.forName("UTF-16")
    val textBytes: ByteArray = text.toByteArray(utfEncoding)
    val utfBit = if (encodeInUtf8) 0 else 1 shl 7
    val status = (utfBit + langBytes.size).toChar()
    val data = ByteArray(1 + langBytes.size + textBytes.size)
    data[0] = status.toByte()
    System.arraycopy(langBytes, 0, data, 1, langBytes.size)
    System.arraycopy(textBytes, 0, data, 1 + langBytes.size, textBytes.size)
    return NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), data)
}

internal fun createRecordURI(
    uri: String,
    charset: Charset = Charset.forName("US-ASCII")
): NdefRecord {
    return ByteArray(0).let { emptyByteArray ->
        NdefRecord(
            TNF_ABSOLUTE_URI,
            uri.toByteArray(charset),
            emptyByteArray,
            emptyByteArray
        )
        //"https://developer.android.com/index.html"
    }
}

