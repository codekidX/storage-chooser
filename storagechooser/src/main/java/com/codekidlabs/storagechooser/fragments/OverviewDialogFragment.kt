package com.codekidlabs.storagechooser.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.codekidlabs.storagechooser.ChooserType
import com.codekidlabs.storagechooser.Config
import com.codekidlabs.storagechooser.R
import com.codekidlabs.storagechooser.StorageChooser2
import com.codekidlabs.storagechooser.adapters.OverviewAdapter
import com.codekidlabs.storagechooser.models.Storage
import com.codekidlabs.storagechooser.models.StorageType
import com.codekidlabs.storagechooser.utils.DiskUtil
import com.codekidlabs.storagechooser.utils.MemoryUtil
import java.io.File
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


class OverviewDialogFragment : DialogFragment() {
    private lateinit var dialogTitle: TextView
    private var mLayout: View? = null
    private var mContainer: ViewGroup? = null

    private var storagesList: MutableList<Storage> = mutableListOf()
    private lateinit var viewDivider: View
    private val TAG = javaClass.name
    private val memoryUtil = MemoryUtil()

    private lateinit var mConfig: Config
    private lateinit var mHolderView: LinearLayout

    private lateinit var pref: SharedPreferences
    private lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContainer = container
        pref = PreferenceManager.getDefaultSharedPreferences(mContext)

        return if (showsDialog) {
            super.onCreateView(inflater, container, savedInstanceState)
        } else getLayout(inflater, container)
    }

    private fun getLayout(inflater: LayoutInflater, container: ViewGroup?): View? {
        // safe check if config from parcelable is accessible
        arguments?.getParcelable<Config>("config")?.let {
            mConfig = it
        }
        // init storage-chooser content [localization]
        mLayout = inflater.inflate(R.layout.storage_list, container, false)
        mLayout?.findViewById<LinearLayout>(R.id.overview_container)?.let {
            mHolderView = it
            initListView(it)

            dialogTitle = it.findViewById(R.id.dialog_title)
            viewDivider = it.findViewById(R.id.storage_view_divider)
            dialogTitle.text = mConfig.content.overviewHeading
        }

        return mLayout
    }

    /**
     * storage listView related code in this block
     */
    private fun initListView(view: View) {
        val listView = view.findViewById<ListView>(R.id.storage_list_view)
        populateList()

        listView.adapter = OverviewAdapter(storagesList, mContext, mConfig)

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            val storage = storagesList.get(i)
            if(storage.canRead()) {
                if (mConfig.saveSelection) {
                    pref.edit()
                            .putString(StorageChooser2.SC_SAVED_PATH, storage.absolutePath)
                            .apply()
                }

                if(mConfig.type == ChooserType.BASIC) {
                    mConfig.selection.onSingleSelection(storage.absolutePath)
                    this@OverviewDialogFragment.dismiss()
                } else {
                    DiskUtil.showSecondaryChooser(storage.absolutePath,
                            mConfig, requireActivity().supportFragmentManager)
                    this@OverviewDialogFragment.dismiss()
                }
            } else {
                Toast.makeText(activity, R.string.toast_not_readable, Toast.LENGTH_SHORT).show()
            }
        }

    }

    /**
     * populate storageList with necessary storages with filter applied
     */
    private fun populateList() {
        ContextCompat.getExternalFilesDirs(mContext, null).let {
            it.forEach { f ->
                val prefPath = f.absolutePath.split("/Android")[0]
                if (!prefPath.endsWith("0")) {
                    val extStorage = Storage(prefPath)
                    extStorage.type = StorageType.EXTERNAL
                    extStorage.storageName = "Ext: ${extStorage.name}"
                    extStorage.totalHumanizedMemory = memoryUtil.formatSize(
                            memoryUtil.getTotalMemorySize(prefPath)
                    )
                    extStorage.availHumanizedMemory = memoryUtil.formatSize(
                            memoryUtil.getAvailableMemorySize(prefPath)
                    )
                    storagesList.add(extStorage)
                } else {
                    val intStorage = Storage(prefPath)
                    intStorage.type = StorageType.INTERNAL
                    intStorage.storageName = "Internal Storage"
                    intStorage.totalHumanizedMemory = memoryUtil.formatSize(
                            memoryUtil.getTotalMemorySize(prefPath))
                    intStorage.availHumanizedMemory = memoryUtil.formatSize(
                            memoryUtil.getAvailableMemorySize(prefPath))
                    storagesList.add(intStorage)
                }
            }
        }

        // USB storages
        val drives: ArrayList<String> = ArrayList()
        val reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*".toRegex()
        var s = ""
        try {
            val process = ProcessBuilder().command("mount")
                    .redirectErrorStream(true).start()
            process.waitFor()
            val `is`: InputStream = process.inputStream
            val buffer = ByteArray(1024)
            while (`is`.read(buffer) != -1) {
                s += String(buffer)
            }
            `is`.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val lines = s.split("\n".toRegex()).toTypedArray()
        for (line in lines) {
            if (!line.toLowerCase(Locale.US).contains("asec") && line.matches(reg)) {
                val parts = line.split(" ".toRegex()).toTypedArray()
                for (path in parts) {
                    if (path.startsWith(File.separator) && !path.toLowerCase(Locale.US).contains("vold")) {
                        drives.add(path)
                    }
                }
            }
        }

        // Remove previously found storages
        val ids: ArrayList<String> = ArrayList()
        for (st in storagesList) {
            val parts = st.path.split(File.separator.toRegex()).toTypedArray()
            ids.add(parts[parts.size - 1])
        }
        for (i in drives.indices.reversed()) {
            val parts = drives[i].split(File.separator.toRegex()).toTypedArray()
            val id = parts[parts.size - 1]
            if (ids.contains(id)) drives.removeAt(i)
        }


        drives.forEach { drivePath ->
            val usb = Storage(drivePath)
            usb.type = StorageType.USB
            usb.storageName = "USB: ${usb.name}"
            usb.availHumanizedMemory = memoryUtil.formatSize(
                    memoryUtil.getAvailableMemorySize(drivePath))
            usb.totalHumanizedMemory = memoryUtil.formatSize(
                    memoryUtil.getTotalMemorySize(drivePath))
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        mConfig.cancellation.onOverviewCancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mContext = requireContext()
        val d = Dialog(requireActivity(), R.style.DialogTheme)
        getLayout(LayoutInflater.from(mContext), mContainer)?.let {
            d.setContentView(it)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(d.window?.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            d.window?.attributes = lp
        }
        return  d
    }
}
