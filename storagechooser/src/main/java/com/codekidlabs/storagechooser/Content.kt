package com.codekidlabs.storagechooser

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Content class handles changes to dialog's view and whatever is in it
 */

@Parcelize
data class Content(var selectLabel: String = "Select",
    var createLabel: String = "Create",
    var newFolderLabel: String = "New Folder",
    var cancelLabel: String = "Cancel",
    var overviewHeading: String = "Choose Storage",
    var internalStorageText: String = "Internal Storage",
    var freeSpaceText: String = "%s free",
    var folderCreatedToastText: String = "Folder Created",
    var folderErrorToastText: String = "Error occured while creating folder. Try again.",
    var textFieldHintText: String = "Folder Name",
    var textFieldErrorText: String = "Empty Folder Name"
) : Parcelable
