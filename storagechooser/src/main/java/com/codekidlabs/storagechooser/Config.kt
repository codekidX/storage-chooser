package com.codekidlabs.storagechooser

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Config of storage-chooser to pass through the various chooser activities
 */
@Parcelize
data class Config(var memoryBarHeight: Float,
                  var showMemoryBar: Boolean,
                  var canAddFolder: Boolean,
                  var showHidden: Boolean,
                  var skipOverview: Boolean,
                  var type: ChooserType,
                  var content: Content,
                  var filterExtensions: MutableList<String>,
                  var selection: StorageChooser2.Selection,
                  var cancellation: StorageChooser2.Cancellation,
                  var style: ChooserStyle,
                  var multiSelect: Boolean,
                  var saveSelection: Boolean,
                  var sessionable: Boolean,
                  var darkMode: Boolean,
                  internal var sessionPath: String) : Parcelable {

    constructor() : this(1.0f,
            true,
            false,
            false,
            false,
            ChooserType.BASIC,
            Content(),
            mutableListOf(),
            ActionWireFrame(),
            ActionWireFrame(),
            ChooserStyle(),
            false,
            false,
            false,
            false,
            ""
            )

}

internal class ActionWireFrame : StorageChooser2.Selection, StorageChooser2.Cancellation {
    override fun onOverviewCancel() {
        return
    }

    override fun onCancel() {
        return
    }

    override fun onSingleSelection(path: String) {
        return
    }

    override fun onMultipleSelection(paths: List<String>) {
        return
    }

}
