# NFCLib-Android
An easy to use NFC library for Android to read Ndef-Tags in the foreground. It's meant to be used by inexperienced and experienced developers alike as it allows full customization. 

## Setup

Gradle: [![](https://jitpack.io/v/Maliotis/NFCLib-Android.svg)](https://jitpack.io/#Maliotis/NFCLib-Android)

```gradle
repositories {
  mavenCentral()
  google()
  maven { url 'https://jitpack.io' }
}

dependencies {
	implementation 'com.github.Maliotis:NFCLib-Android:${latestVersion}'
}
```

## Usage

### Add NFC permissions in your manifest

```xml
<uses-permission android:name="android.permission.NFC" />
```

### A simple usage of the library to read from a tag

*Don't enable NFC in the foreground in the `onCreate` method instead enable it in `onResume` or during time i.e press of a button*

```Kotlin

private lateinit var readNFC: ReadNFC

override fun onResume() {
        super.onResume()
        //...
        readNFC = NFCFactory.create<ReadNFC>(this).getNFC()
        readNFC.enableNFCInForeground()
}

override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        //...
        if (intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            readNFC.connect(intent)
            val payload = readNFC.payload()
            payload.forEach {
                Log.d("TAG", "onNewIntent: $it")
            }

        }
}
```

### Write to a tag

```Kotlin
private lateinit var writeNFC: WriteNFC

override fun onResume() {
        super.onResume()
        //...
        writeNFC = NFCFactory.create<WriteNFC>(this).getNFC()
        writeNFC.enableNFCInForeground()
}

override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        //...
        if (intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            writeNFC.connect(intent)
            val succeeded = writeNFC.write("Hello from NfcLib :)")
            Log.d("TAG", "onNewIntent: succeeded = $succeeded")
            // when in write mode close the connection
            writeNFC.close()
        }
}
```

### A combination of read and write 

*Don't attempt to read directly after a `write` operation, remove the tag and `read` in the next tag discovery*

```Kotlin
private lateinit var readNFC: ReadNFC
private lateinit var writeNFC: WriteNFC
private var flagRead = true

override fun onResume() {
        super.onResume()
        readNFC = NFCFactory.create<ReadNFC>(this).getNFC()
        writeNFC = NFCFactory.create<WriteNFC>(this).getNFC()
        // use only one object to enable nfc in foreground
        writeNFC.enableNFCInForeground()
}

override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        //...
        if (intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            if (flagRead) {
                // read from tag
                readNFC.connect(intent)
                val payload = readNFC.payload()
                payload.forEach {
                    Log.d("TAG", "onNewIntent: $it")
                }
            } else {
                // write to tag
                writeNFC.connect(intent)
                val succeeded = writeNFC.write("Hello from NfcLib :)")
                Log.d("TAG", "onNewIntent: succeeded = $succeeded")
                writeNFC.close()
            }

        }
}

```

## Customization

The library allows for full customization by overriding the `connectInterface` in each write and read operations from here on you are in full control of the tags and the connections.

```Kotlin
 override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            // Determine the action

            if (writeAction) {

                // Handle the connection interface i.e implement behavior for NfcA, MIFARE e.t.c
                writeNFC.connectInterface = object: ConnectInterface {
                    override fun attemptConnect(tag: Tag) {
                        //TODO("Not yet implemented")
                    }

                }
                // Handle the Ndef tag connection
                writeNFC.ndefFunction = {
                    // return the Ndef Tag
                }

                writeNFC.connect(intent)
                
                writeNFC.nfcTech = NfcTech.NDEF
                writeNFC.localeLanguage = Locale.ENGLISH // Change the language
                writeNFC.tnf = NdefRecord.TNF_ABSOLUTE_URI // Change the TNF value
                writeNFC.utfEncoding = Charsets.UTF_16 // Change the encoding
                // Type record is used only in TNF_WELL_KNOWN
                writeNFC.typeRecord = NdefRecord.RTD_URI // Change the type record

                writeNFC.write("Hello from NFCLib :)")

                // Check the write(String) implementation for passing byteArray
                //writeNFC.write(/*ByteArray*/)

                // Check the write(String) implementation for passing NdefMessage
                //writeNFC.write(/*NdefMessage*/)

                // In write transaction DON'T forget to close the connection
                writeNFC.close()
            }

            if (readAction) {

                // Change the nfc technology
                readNFC.nfcTech = NfcTech.NDEF
                // Handle the connection interface i.e implement behavior for NfcA, MIFARE e.t.c
                readNFC.connectInterface = object: ConnectInterface {
                    override fun attemptConnect(tag: Tag) {
                        //TODO("Not yet implemented")
                    }

                }
                // Handle the Ndef tag connection and access to the payload
                readNFC.ndefFunction = {
                    // the function requires to return the Ndef message
                }
                readNFC.connect(intent)
                
                val message = readNFC.message() // return the NdefMessage
                val records = readNFC.records() // return the NdefRecords
                
                val payload = readNFC.payload()
                payload.forEach {
                    Log.d("TAG", "onNewIntent: $it")
                }
            }

        }


}

```

### An example implementation of `connectInterface` in write operation

The below example has been taken from the library, use it as a guide

```Kotlin
writeNFC.connectInterface = object: ConnectInterface {
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

```

### An example implementation of `connectInterface` in read operation

The below example has been taken from the library, use it as a guide

```Kotlin
readNFC.connectInterface = object: ConnectInterface {
        override fun attemptConnect(tag: Tag) {
            val technologies = tag.techList
            val tagTechs = listOf(*technologies)
            val nfcTechName = nfcTech.canonicalName

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
```

### An example of `ndefFunction` implementation in read operation

The below example has been taken from the library, use it as a guide

```Kotlin
readNFC.ndefFunction = { tag ->
            val ndefTag = Ndef.get(tag)
            var ndefMessage: NdefMessage? = null
            ndefTag.let {
                it.connect()
                ndefMessage = it.cachedNdefMessage
                it.close()
            }
            if (ndefMessage == null) throw RuntimeException("Couldn't connect to nfc tag\n" +
                    "Ndef Message is null\n" +
                    "Exiting..")
            ndefMessage!!
}
```

### An example of `ndefFunction` implementation in write operation

The below example has been taken from the library, use it as a guide

```Kotlin
writeNFC.ndefFunction = {
            val tagTechnology = Ndef.get(it)
            tagTechnology.connect()
            tagTechnology
}
```

### For further reading please visit anrdroid developers site [Android-NFC](https://developer.android.com/guide/topics/connectivity/nfc)

## License
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
