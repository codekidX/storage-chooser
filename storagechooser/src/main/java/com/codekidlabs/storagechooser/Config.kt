package com.codekidlabs.storagechooser

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Config of storage-chooser to pass through the various chooser activities
 */
@Parcelize
data class Config(var memoryBarHeight: Float = 1.0f,
                  var type: ChooserType) : Parcelable