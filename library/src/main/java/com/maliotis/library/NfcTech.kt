package com.maliotis.library

import android.nfc.tech.*
import androidx.annotation.StringDef

/**
 * Created by petrosmaliotis on 29/05/2020.
 */


object NfcTech {
     val NDEF: Class<Ndef> = Ndef::class.java
     val NDEF_FORMATTABLE: Class<NdefFormatable> = NdefFormatable::class.java
     val NFCA: Class<NfcA> = NfcA::class.java
     val NFCB: Class<NfcB> = NfcB::class.java
     val NFCF: Class<NfcF> = NfcF::class.java
     val NFCV: Class<NfcV> = NfcV::class.java
     val NFC_BARCODE: Class<NfcBarcode> = NfcBarcode::class.java
     val ISO_DEP: Class<IsoDep> = IsoDep::class.java
     val MIFARE_CLASSIC: Class<MifareClassic> = MifareClassic::class.java
     val MIFARE_ULTRALIGHT: Class<MifareUltralight> = MifareUltralight::class.java
}

