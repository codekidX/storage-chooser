package com.codekidlabs.storagechooser.models

import java.io.File

/**
 * A model that is used as the element to store any data regarding storage volumes/disks
 */
class Storage(pathname: String) : File(pathname) 