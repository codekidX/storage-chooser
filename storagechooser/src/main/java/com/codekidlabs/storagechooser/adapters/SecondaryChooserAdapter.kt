package com.codekidlabs.storagechooser.adapters

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.codekidlabs.storagechooser.Config

import com.codekidlabs.storagechooser.R
//import com.codekidlabs.storagechooser.StorageChooser
import com.codekidlabs.storagechooser.fragments.OverviewDialogFragment
import com.codekidlabs.storagechooser.utils.FileUtil
import com.codekidlabs.storagechooser.utils.MemoryUtil
import com.codekidlabs.storagechooser.utils.ResourceUtil
import com.codekidlabs.storagechooser.utils.ThumbnailUtil
import java.io.File

import java.util.ArrayList

class SecondaryChooserAdapter(
        private val storagesList: MutableList<File>,
        private val mContext: Context,
        private val mConfig: Config) : BaseAdapter() {

    private lateinit var storageDesc: TextView
    private lateinit var storageName: TextView
    var selectedPaths: ArrayList<Int>
    var prefixPath: String = ""
    private val thumbnailUtil: ThumbnailUtil
    private val resourceUtil: ResourceUtil


    init {

        // create instance once
        thumbnailUtil = ThumbnailUtil(mContext)
        resourceUtil = ResourceUtil(mContext)
        selectedPaths = ArrayList()
    }

    override fun getCount(): Int {
        return storagesList.size
    }

    override fun getItem(i: Int): Any {
        return storagesList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View? {
        val currentFile = prefixPath + "/" + storagesList[i].name
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val rootView = inflater.inflate(R.layout.row_custom_paths, viewGroup, false)

        val pathFolderIcon = rootView.findViewById<ImageView>(R.id.path_folder_icon)
        val videoPlayIcon = rootView.findViewById<ImageView>(R.id.video_play_icon)

        if (FileUtil.isDir(currentFile)) {
            applyFolderTint(pathFolderIcon)
        }

        thumbnailUtil.init(pathFolderIcon, videoPlayIcon, currentFile)

        storageName = rootView.findViewById(R.id.storage_name)
        storageDesc = rootView.findViewById(R.id.storage_desc)
        storageName.text = storagesList[i].name
        storageDesc.text = getDescription(storagesList[i])

//        if (listTypeface != null) {
//            storageName.typeface = OverviewDialogFragment.getSCTypeface(mContext, listTypeface,
//                    fromAssets)
//        }


//        storageName.setTextColor(scheme[StorageChooser.Theme.SEC_TEXT_INDEX])

        if (selectedPaths.contains(i)) {
            rootView.setBackgroundColor(resourceUtil.primaryColorWithAlpha)
        }

        applyDarkModeColors()

        return rootView
    }

    private fun getDescription(f: File): String {
        if (f.isDirectory) {
            val childCount = f.listFiles().size
            if (childCount > 1) {
                return "Directory • $childCount files"
            } else if(childCount == 1) {
                return "Directory • $childCount file"
            }
            return "Directory"
        }
        return MemoryUtil().formatSize(f.length())
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

    private fun applyDarkModeColors() {
        if(mConfig.darkMode) {
            storageName.setTextColor(ContextCompat.getColor(mContext, R.color.dark_mode_text))
            storageDesc.setTextColor(ContextCompat.getColor(mContext, R.color.dark_mode_secondary_text))
        }
    }

    private fun applyFolderTint(im: ImageView) {
//        im.setColorFilter(scheme[StorageChooser.Theme.SEC_FOLDER_TINT_INDEX])
    }

    override fun isEnabled(position: Int): Boolean {
        return shouldEnable
    }

    companion object {

        var shouldEnable = true
    }

}
