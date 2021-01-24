package com.codekidlabs.storagechooser.models

import java.io.File

/**
 * A model that is used as the element to store any data regarding storage volumes/disks
 */
class Storage(pathname: String) : File(pathname) {

    lateinit var type: StorageType
    lateinit var storageName: String
    lateinit var totalHumanizedMemory: String
    lateinit var availHumanizedMemory: String
}

enum class StorageType {
    INTERNAL, EXTERNAL, USB
}