package com.codekidlabs.storagechooser.adapters

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.BaseAdapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView

import com.codekidlabs.storagechooser.Config
import com.codekidlabs.storagechooser.R
import com.codekidlabs.storagechooser.animators.MemorybarAnimation
import com.codekidlabs.storagechooser.exceptions.MemoryNotAccessibleException
import com.codekidlabs.storagechooser.models.Storages
import com.codekidlabs.storagechooser.utils.MemoryUtil

class OverviewAdapter(private val storageList: MutableList<Storages>, private val mContext: Context, private val mConfig: Config) : BaseAdapter() {

    // VIEWS
    private lateinit var memoryStatus: TextView
    private lateinit var storageName: TextView
    private lateinit var memoryBar: ProgressBar
    private lateinit var storageIcon: AppCompatImageView

    // VARS
    private var memoryPercentile: Int = -1

    override fun getCount(): Int {
        return storageList.size
    }

    override fun getItem(i: Int): Any {
        return storageList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rootView = inflater.inflate(R.layout.row_storage, viewGroup, false)

        //for animation set current position to provide animation delay
        storageName = rootView.findViewById(R.id.storage_name)
        storageIcon = rootView.findViewById(R.id.storage_icon)
        memoryStatus = rootView.findViewById<TextView>(R.id.memory_status)
        memoryBar = rootView.findViewById(R.id.memory_bar)

        // new scaled memorybar - following the new google play update!
        memoryBar.scaleY = mConfig.memoryBarHeight

        val storages = storageList[i]
        val str = SpannableStringBuilder(storages.storageTitle + " (" + storages.memoryTotalSize + ")")

        str.setSpan(StyleSpan(Typeface.ITALIC), getSpannableIndex(str), str.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val availableText = String.format(mConfig.content.freeSpaceText, storages.memoryAvailableSize)
        storageName.text = str
        memoryStatus.text = availableText

        DrawableCompat.setTint(memoryBar.progressDrawable, ContextCompat.getColor(this.mContext, R.color.colorAccent))

        // if don't show memory bar then hide memory bar
        if(!mConfig.showMemoryBar) {
            memoryStatus.visibility = View.GONE
        } else {
            // try getting remaining percentage of memory bar
            try {
                memoryPercentile = getPercentile(storages.storagePath)
                // if it is a valid percentage run animation
                if (memoryPercentile != -1) {
                    memoryBar.max = 100
                    memoryBar.progress = memoryPercentile
                    runMemorybarAnimation(i)
                } else {
                    // hide
                    memoryBar.visibility = View.GONE
                }
            } catch (e: MemoryNotAccessibleException) {
                e.printStackTrace()
                memoryStatus.visibility = View.GONE
            }
        }

        applyDarkModeColors()

        return rootView

    }

    private fun applyDarkModeColors() {
        if(mConfig.darkMode) {
            val white = ContextCompat.getColor(mContext, R.color.dark_mode_text)
            storageName.setTextColor(white)
            storageIcon.setColorFilter(white, PorterDuff.Mode.SRC_IN)
            memoryStatus.setTextColor(ContextCompat.getColor(mContext, R.color.dark_mode_secondary_text))
        } else {
            val black = ContextCompat.getColor(mContext, android.R.color.black)
            storageName.setTextColor(black)
            storageIcon.setColorFilter(black, PorterDuff.Mode.SRC_IN)
            memoryStatus.setTextColor(black)
        }
    }

    private fun runMemorybarAnimation(pos: Int) {
        val animation = MemorybarAnimation(memoryBar, 0, memoryPercentile)
        animation.duration = 500
        animation.interpolator = AccelerateDecelerateInterpolator()

        if (pos > 0) {
            animation.startOffset = 300
        }

        memoryBar.startAnimation(animation)
    }

    /**
     * return the spannable index of character '('
     *
     * @param str SpannableStringBuilder to apply typeface changes
     * @return index of '('
     */
    private fun getSpannableIndex(str: SpannableStringBuilder): Int {
        return str.toString().indexOf("(") + 1
    }

    /**
     * calculate percentage of memory left for memorybar
     *
     * @param path use same statfs
     * @return integer value of the percentage with amount of storage used
     */
    @Throws(MemoryNotAccessibleException::class)
    private fun getPercentile(path: String): Int {
        val memoryUtil = MemoryUtil()
        val percent: Int

        val availableMem = memoryUtil.getAvailableMemorySize(path)
        val totalMem = memoryUtil.getTotalMemorySize(path)

        if (totalMem > 0) {
            percent = (100 - availableMem * 100 / totalMem).toInt()
        } else {
            throw MemoryNotAccessibleException("Cannot compute memory for $path")
        }

        return percent
    }

    /**
     * remove KiB,MiB,GiB text that we got from MemoryUtil.getAvailableMemorySize() &
     * MemoryUtil.getTotalMemorySize()
     *
     * @param size String in the format of user readable string, with MB, GiB .. suffix
     * @return integer value of the percentage with amount of storage used
     */
    private fun getMemoryFromString(size: String): Long {
        val mem: Long

        if (size.contains("MiB")) {
            mem = Integer.parseInt(size.replace(",", "").replace("MiB", "")).toLong()
        } else if (size.contains("GiB")) {
            mem = Integer.parseInt(size.replace(",", "").replace("GiB", "")).toLong()
        } else {
            mem = Integer.parseInt(size.replace(",", "").replace("KiB", "")).toLong()
        }

        Log.d("TAG", "Memory:$mem")
        return mem
    }
}
