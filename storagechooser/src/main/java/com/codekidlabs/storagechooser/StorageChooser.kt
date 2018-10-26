package com.codekidlabs.storagechooser

import android.app.Activity

/**
 * Main Builder for storage-chooser
 */

class StorageChooser(private val activity: Activity, private val config: Config) {

    fun show() {
        when (this.config.type) {
            ChooserType.DIRECTORY -> "hello"
            else -> {
            }
        }
    }
}