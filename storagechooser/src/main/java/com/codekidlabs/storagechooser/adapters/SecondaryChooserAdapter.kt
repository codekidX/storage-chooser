package com.codekidlabs.storagechooser.adapters

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.codekidlabs.storagechooser.R
//import com.codekidlabs.storagechooser.StorageChooser
import com.codekidlabs.storagechooser.fragments.OverviewDialogFragment
import com.codekidlabs.storagechooser.utils.FileUtil
import com.codekidlabs.storagechooser.utils.ResourceUtil
import com.codekidlabs.storagechooser.utils.ThumbnailUtil

import java.util.ArrayList

class SecondaryChooserAdapter(private val storagesList: MutableList<String>, private val mContext: Context) : BaseAdapter() {
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
        val currentFile = prefixPath + "/" + storagesList[i]
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val rootView = inflater.inflate(R.layout.row_custom_paths, viewGroup, false)

        val pathFolderIcon = rootView.findViewById<ImageView>(R.id.path_folder_icon)
        val videoPlayIcon = rootView.findViewById<ImageView>(R.id.video_play_icon)

        if (FileUtil.isDir(currentFile)) {
            applyFolderTint(pathFolderIcon)
        }

        thumbnailUtil.init(pathFolderIcon, videoPlayIcon, currentFile)

        val storageName = rootView.findViewById<TextView>(R.id.storage_name)
        storageName.text = storagesList[i]

//        if (listTypeface != null) {
//            storageName.typeface = OverviewDialogFragment.getSCTypeface(mContext, listTypeface,
//                    fromAssets)
//        }


//        storageName.setTextColor(scheme[StorageChooser.Theme.SEC_TEXT_INDEX])

        if (selectedPaths.contains(i)) {
            rootView.setBackgroundColor(resourceUtil.primaryColorWithAlpha)
        }

        return rootView
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
