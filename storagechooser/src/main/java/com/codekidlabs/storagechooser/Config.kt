package com.codekidlabs.storagechooser

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Config of storage-chooser to pass through the various chooser activities
 */
@Parcelize
data class Config(var memoryBarHeight: Float,
                  var type: ChooserType,
                  var selection: StorageChooser2.Selection,
                  var cancellation: StorageChooser2.Cancellation,
                  var theme: SCTheme,
                  var saveSelection: Boolean,
                  var sessionable: Boolean,
                  internal var sessionPath: String) : Parcelable {



    constructor() : this(sessionPath = "",
            memoryBarHeight = 1.0f,
            type = ChooserType.BASIC,
            selection = ActionWireFrame(),
            cancellation = ActionWireFrame(),
            theme = SCTheme(),
            saveSelection = false,
            sessionable = false)

}

internal class ActionWireFrame : StorageChooser2.Selection, StorageChooser2.Cancellation {
    override fun onCancel(lastOpenedPath: String) {
        return
    }

    override fun onSingleSelection(path: String) {
        return
    }

    override fun onMultipleSelection(paths: List<String>) {
        return
    }

}
