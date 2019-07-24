package com.codekidlabs.storagechooser.adapters

import android.content.Context
import android.graphics.Typeface

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.BaseAdapter
import android.widget.ProgressBar
import android.widget.TextView

import com.codekidlabs.storagechooser.Config
import com.codekidlabs.storagechooser.Content
import com.codekidlabs.storagechooser.R
import com.codekidlabs.storagechooser.animators.MemorybarAnimation
import com.codekidlabs.storagechooser.exceptions.MemoryNotAccessibleException
import com.codekidlabs.storagechooser.fragments.OverviewDialogFragment
import com.codekidlabs.storagechooser.models.Storages
import com.codekidlabs.storagechooser.utils.MemoryUtil

class OverviewAdapter(private val storagesList: MutableList<Storages>, private val mContext: Context, private val config: Config) : BaseAdapter() {
    private var memoryBar: ProgressBar? = null

    private val mContent: Content = config.content

    override fun getCount(): Int {
        return storagesList.size
    }

    override fun getItem(i: Int): Any {
        return storagesList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        memoryPercentile = -1
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rootView = inflater.inflate(R.layout.row_storage, viewGroup, false)

        //for animation set current position to provide animation delay
        val storageName = rootView.findViewById<TextView>(R.id.storage_name)
        val memoryStatus = rootView.findViewById<TextView>(R.id.memory_status)
        memoryBar = rootView.findViewById(R.id.memory_bar)

        // new scaled memorybar - following the new google play update!
        memoryBar!!.scaleY = config.memoryBarHeight

        val storages = storagesList[i]
        val str = SpannableStringBuilder(storages.storageTitle + " (" + storages.memoryTotalSize + ")")

        str.setSpan(StyleSpan(Typeface.ITALIC), getSpannableIndex(str), str.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val availableText = String.format(mContent.freeSpaceText, storages.memoryAvailableSize)
        storageName.text = str

        storageName.setTextColor(ContextCompat.getColor(this.mContext, android.R.color.black))
        memoryStatus.text = availableText

//        if (listTypeface != null) {
//            storageName.typeface = OverviewDialogFragment.getSCTypeface(mContext, listTypeface,
//                    fromAssets)
//            memoryStatus.typeface = OverviewDialogFragment.getSCTypeface(mContext, listTypeface,
//                    fromAssets)
//        }

        memoryStatus.setTextColor(ContextCompat.getColor(this.mContext, android.R.color.black))
        DrawableCompat.setTint(memoryBar!!.progressDrawable, ContextCompat.getColor(this.mContext, R.color.colorAccent))

        try {
            memoryPercentile = getPercentile(storages.storagePath)
        } catch (e: MemoryNotAccessibleException) {
            e.printStackTrace()
        }

        // THE ONE AND ONLY MEMORY BAR
        if (this.config.showMemoryBar && memoryPercentile != -1) {
            memoryBar!!.max = 100
            memoryBar!!.progress = memoryPercentile
            runMemorybarAnimation(i)
        } else {
            memoryBar!!.visibility = View.GONE
        }

//        if (false) {
//            memoryStatus.visibility = View.GONE
//            storageName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
//        }

        return rootView

    }

    private fun runMemorybarAnimation(pos: Int) {
        val animation = MemorybarAnimation(memoryBar, 0, memoryPercentile)
        animation.duration = 500
        animation.interpolator = AccelerateDecelerateInterpolator()

        if (pos > 0) {
            animation.startOffset = 300
        }

        memoryBar!!.startAnimation(animation)
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

    companion object {

        private var memoryPercentile: Int = 0
    }
}
