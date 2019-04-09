package com.codekidlabs.storagechooser

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.android.parcel.Parcelize

@Parcelize
class SCTheme(@ColorRes var accentColor: Int) : Parcelable {

    constructor() : this(accentColor = R.color.colorAccent)
}