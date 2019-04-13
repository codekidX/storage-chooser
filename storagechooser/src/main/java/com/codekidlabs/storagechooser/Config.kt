package com.codekidlabs.storagechooser

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Config of storage-chooser to pass through the various chooser activities
 */
@Parcelize
data class Config(var memoryBarHeight: Float,
                  var canAddFolder: Boolean,
                  var showHidden: Boolean,
                  var skipOverview: Boolean,
                  var threshold: String,
                  var type: ChooserType,
                  var content: Content,
                  var filterExtensions: String,
                  var selection: StorageChooser2.Selection,
                  var cancellation: StorageChooser2.Cancellation,
                  var theme: SCTheme,
                  var multiSelect: Boolean,
                  var saveSelection: Boolean,
                  var sessionable: Boolean,
                  internal var sessionPath: String) : Parcelable {



    constructor() : this(sessionPath = "",
            memoryBarHeight = 0.0f,
            skipOverview = false,
            canAddFolder = false,
            showHidden = false,
            threshold = "",
            multiSelect = true,
            filterExtensions = "",
            content = Content(),
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
