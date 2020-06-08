package com.maliotis.library.typealiases

import android.nfc.NdefMessage
import android.nfc.Tag
import android.nfc.tech.*

/**
 * Created by petrosmaliotis on 03/06/2020.
 */

typealias ReadNdefInterface = (Tag) -> NdefMessage

typealias ReadNfcAInterface = (Tag) -> Unit

typealias ReadNfcBInterface = (Tag) -> Unit

typealias ReadNfcVInterface = (Tag) -> Unit

typealias ReadNfcFInterface = (Tag) -> Unit

typealias ReadMifareClassicInterface = (Tag) -> Unit

typealias ReadMifareUltralightInterface = (Tag) -> Unit

typealias ReadIsoDepInterface = (Tag) -> Unit

typealias ReadNfcBarcodeInterface = (Tag) -> Unit