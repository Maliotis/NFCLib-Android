package com.maliotis.library.interfaces

import android.nfc.NdefMessage
import android.nfc.Tag
import android.nfc.tech.*

/**
 * Created by petrosmaliotis on 29/05/2020.
 */
interface ConnectInterface {
    fun attemptConnect(tag: Tag)

}