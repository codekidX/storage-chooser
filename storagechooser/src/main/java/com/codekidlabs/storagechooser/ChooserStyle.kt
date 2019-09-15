package com.codekidlabs.storagechooser

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.android.parcel.Parcelize


@Parcelize
class ChooserStyle(
        // colors
        @ColorRes var accentColor: Int,
        @ColorRes var overviewTextColor: Int,
        @ColorRes var darkModeBgColor: Int

) : Parcelable {

    constructor() : this(
            accentColor = R.color.colorAccent,
            overviewTextColor = android.R.color.white,
            darkModeBgColor = R.color.dark_mode_bg
    )
}