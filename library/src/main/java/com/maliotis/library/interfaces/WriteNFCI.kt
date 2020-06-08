package com.maliotis.library.interfaces

import android.nfc.NdefMessage

/**
 * Created by petrosmaliotis on 29/05/2020.
 */
interface WriteNFCI: NFC {

    // NDEF
    /**
     * Write the [content] as [android.nfc.NdefRecord.RTD_TEXT] into the NTAG
     */
    fun write(content: String): Boolean

    /**
     * Write the given [content] into the NTAG
     */
    fun write(content: ByteArray): Boolean

    /**
     * Write the given [message] into the NTAG
     */
    fun write(message: NdefMessage): Boolean

    // Mifare Classic authenticate methods
    /**
     * Authenticate all sectors with the [keyA].
     * By default the keys are null and authenticate with one of the keys:
     * [android.nfc.tech.MifareClassic.KEY_NFC_FORUM], [android.nfc.tech.MifareClassic.KEY_DEFAULT],
     * [android.nfc.tech.MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY].
     */
    fun authAllSectorsMifareClassicKeyA(keyA: ByteArray? = null): Boolean

    /**
     * Authenticate all sectors with the [keyB].
     * By default the keys are null and authenticate with one of the keys:
     * [android.nfc.tech.MifareClassic.KEY_NFC_FORUM], [android.nfc.tech.MifareClassic.KEY_DEFAULT],
     * [android.nfc.tech.MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY].
     */
    fun authAllSectorsMifareClassicKeyB(keyB: ByteArray? = null): Boolean

    /**
     * Authenticate all sectors with the [keyA] and [keyB].
     * By default the keys are null and authenticate with one of the keys:
     * [android.nfc.tech.MifareClassic.KEY_NFC_FORUM], [android.nfc.tech.MifareClassic.KEY_DEFAULT],
     * [android.nfc.tech.MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY].
     */
    fun authAllSectorsMifareClassicKeyAB(keyA: ByteArray? = null, keyB: ByteArray? = null): Boolean

    /**
     * Authenticate the given [sector] with the [keyA].
     * By default the keys are null and authenticate with one of the keys:
     * [android.nfc.tech.MifareClassic.KEY_NFC_FORUM], [android.nfc.tech.MifareClassic.KEY_DEFAULT],
     * [android.nfc.tech.MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY].
     */
    fun authSectorWithKeyA(sector: Int, keyA: ByteArray? = null): Boolean

    /**
     * Authenticate the given [sector] with the [keyB].
     * By default the keys are null and authenticate with one of the keys:
     * [android.nfc.tech.MifareClassic.KEY_NFC_FORUM], [android.nfc.tech.MifareClassic.KEY_DEFAULT],
     * [android.nfc.tech.MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY].
     */
    fun authSectorWithKeyB(sector: Int, keyB: ByteArray? = null): Boolean

    /**
     * Authenticate the given [sector] with the [keyA] and [keyB].
     * By default the keys are null and authenticate with one of the keys:
     * [android.nfc.tech.MifareClassic.KEY_NFC_FORUM], [android.nfc.tech.MifareClassic.KEY_DEFAULT],
     * [android.nfc.tech.MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY].
     */
    fun authSectorWithKeyAB(sector: Int, keyA: ByteArray? = null, keyB: ByteArray? = null): Boolean
    // Mifare Classic write methods
    /**
     * Calculate the available/usable memory in the Mifare Classic smart card/ticket
     * and distribute the the given [byteArray] evenly starting from the first available empty block
     */
    fun writePayloadToAllBlocks(byteArray: ByteArray): Boolean

    /**
     * Write 16 byte block
     */
    fun writeBlock(blockIndex: Int, byteArray: ByteArray): Boolean

    // Mifare Classic helper methods
    /**
     * Return the total number of Mifare Classic blocks
     */
    fun getBlockCount(): Int

    /**
     * Return the number of blocks in the given sector
     */
    fun getBlockCountInSector(sectorIndex: Int): Int

    /**
     * Return the number of Mifare Classic sectors
     */
    fun getSectorCount(): Int

    /**
     * Return the block of a given sector
     */
    fun sectorToBlock(sectorIndex: Int): Int

    /**
     * Close the connection
     */
    fun close()

}