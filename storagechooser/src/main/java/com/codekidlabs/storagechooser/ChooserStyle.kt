package com.codekidlabs.storagechooser

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.android.parcel.Parcelize


@Parcelize
class ChooserStyle(
        @ColorRes var backgroundColor: Int,
        @ColorRes var textColor: Int,
        @ColorRes var secondaryTextColor: Int,
        @ColorRes var accentColor: Int
) : Parcelable {

    constructor() : this(
            backgroundColor = android.R.color.white,
            textColor = R.color.dark_mode_bg,
            secondaryTextColor = R.color.dark_mode_secondary_bg,
            accentColor = R.color.colorAccent
    )
}