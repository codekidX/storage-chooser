package com.codekidlabs.storagechooser.utils


import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils

import com.codekidlabs.storagechooser.R

class ResourceUtil(private val context: Context) {

    val primaryColorWithAlpha: Int
        get() = getAppliedAlpha(getColor(R.color.colorPrimary))

    fun getColor(id: Int): Int {
        return ContextCompat.getColor(context, id)
    }

    fun applyAlpha(color: Int, alpha: Int) = ColorUtils.setAlphaComponent(color, alpha)

    fun getAppliedAlpha(color: Int): Int {
        return ColorUtils.setAlphaComponent(color, 50)
    }
}
