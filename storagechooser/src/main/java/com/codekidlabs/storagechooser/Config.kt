package com.codekidlabs.storagechooser

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Config of storage-chooser to pass through the various chooser activities
 */
@Parcelize
data class Config(var memoryBarHeight: Float,
                  var type: ChooserType,
                  var selection: StorageChooser.Selection,
                  var cancellation: StorageChooser.Cancellation) : Parcelable {

    constructor() : this(memoryBarHeight = 1.0f,
            type = ChooserType.BASIC,
            selection = ActionWireFrame(),
            cancellation = ActionWireFrame())

}

internal class ActionWireFrame : StorageChooser.Selection, StorageChooser.Cancellation {
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
