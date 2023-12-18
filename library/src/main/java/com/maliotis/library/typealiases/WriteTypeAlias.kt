package com.maliotis.library.typealiases

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcBarcode
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV

/** Created by petrosmaliotis on 03/06/2020. */

typealias WriteNdefInterface = (Tag) -> Ndef

typealias WriteNfcAInterface = (NfcA) -> Unit

typealias WriteNfcBInterface = (NfcB) -> Unit

typealias WriteNfcVInterface = (NfcV) -> Unit

typealias WriteNfcFInterface = (NfcF) -> Unit

typealias WriteMifareClassicInterface = (MifareClassic) -> Unit

typealias WriteMifareUltralightInterface = (MifareUltralight) -> Unit

typealias WriteIsoDepInterface = (IsoDep) -> Unit

typealias WriteNfcBarcodeInterface = (NfcBarcode) -> Unit