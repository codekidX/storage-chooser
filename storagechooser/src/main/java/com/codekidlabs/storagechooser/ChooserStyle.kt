package com.codekidlabs.storagechooser

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.android.parcel.Parcelize


@Parcelize
class ChooserStyle(
        // colors
        @ColorRes var accentColor: Int,
        @ColorRes var overviewTextColor: Int,


        // fonts
        var headingTypeface: String,
        var listTypeface: String,
        var loadFontFromAssets: Boolean
) : Parcelable {

    constructor() : this(
            accentColor = R.color.colorAccent,
            overviewTextColor = android.R.color.white,
            headingTypeface = "",
            listTypeface = "",
            loadFontFromAssets = false
    )
}