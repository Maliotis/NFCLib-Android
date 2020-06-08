package com.maliotis.library.typealiases

import android.nfc.NdefMessage
import android.nfc.Tag
import android.nfc.tech.*

/**
 * Created by petrosmaliotis on 03/06/2020.
 */

typealias WriteNdefInterface = (Tag) -> Ndef

typealias WriteNfcAInterface = (NfcA) -> Unit

typealias WriteNfcBInterface = (NfcB) -> Unit

typealias WriteNfcVInterface = (NfcV) -> Unit

typealias WriteNfcFInterface = (NfcF) -> Unit

typealias WriteMifareClassicInterface = (MifareClassic) -> Unit

typealias WriteMifareUltralightInterface = (MifareUltralight) -> Unit

typealias WriteIsoDepInterface = (IsoDep) -> Unit

typealias WriteNfcBarcodeInterface = (NfcBarcode) -> Unit