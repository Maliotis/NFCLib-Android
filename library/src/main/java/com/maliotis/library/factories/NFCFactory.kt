package com.maliotis.library.factories

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.nfc.NfcManager
import com.maliotis.library.interfaces.NFC
import com.maliotis.library.nfc.ReadNFC
import com.maliotis.library.nfc.WriteNFC
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass

/**
 * Created by petrosmaliotis on 28/05/2020.
 */
abstract class NFCFactory<T: NFC> {
    abstract fun getNFC(activity: Activity): T
    abstract fun handleIntent(intent: Intent)


    companion object {

        @JvmStatic
        @PublishedApi internal
        var nfcManager: NfcManager? = null

        inline fun <reified T: NFC>create(activity: Activity): T {
            this.nfcManager = activity.getSystemService(Context.NFC_SERVICE) as NfcManager

            return when (T::class) {
                ReadNFC::class -> ReadNFCFactory().getNFC(activity) as T
                WriteNFC::class -> WriteNFCFactory().getNFC(activity) as T
                else -> throw IllegalArgumentException()
            }
        }

        /**
         * Java alternative method
         */
        @JvmStatic
        fun <T: NFC> create(typeOf: Class<T>, activity: Activity): T {
            this.nfcManager = activity.getSystemService(Context.NFC_SERVICE) as NfcManager

            return when (typeOf) {
                ReadNFC::javaClass -> ReadNFCFactory().getNFC(activity) as T
                WriteNFC::javaClass -> WriteNFCFactory().getNFC(activity) as T
                else -> throw IllegalArgumentException()
            }
        }


    }

}