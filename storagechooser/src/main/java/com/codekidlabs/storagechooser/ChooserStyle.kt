package com.codekidlabs.storagechooser

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.android.parcel.Parcelize


@Parcelize
class ChooserStyle(
        // colors
        var overviewStyle: OverviewStyle,
        var pickerStyle: PickerStyle
) : Parcelable {

    constructor() : this(
            overviewStyle = OverviewStyle(),
            pickerStyle = PickerStyle()
    )


    @Parcelize
    class OverviewStyle(
            @ColorRes var backgroundColor: Int
    ): Parcelable {

        constructor() : this(
                backgroundColor = R.color.dark_mode_bg
        )
    }

    @Parcelize
    class PickerStyle(
            @ColorRes var backgroundColor: Int
    ): Parcelable {

        constructor() : this(
                backgroundColor = R.color.dark_mode_bg
        )
    }
}