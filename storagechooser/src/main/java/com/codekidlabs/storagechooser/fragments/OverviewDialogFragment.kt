package com.codekidlabs.storagechooser.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.storage.StorageManager
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.codekidlabs.storagechooser.*
import com.codekidlabs.storagechooser.adapters.OverviewAdapter

import com.codekidlabs.storagechooser.models.Storages
import com.codekidlabs.storagechooser.utils.DiskUtil
import com.codekidlabs.storagechooser.utils.FileUtil
import com.codekidlabs.storagechooser.utils.MemoryUtil

import java.io.File
import java.util.ArrayList


class OverviewDialogFragment : DialogFragment() {
    private lateinit var dialogTitle: TextView
    private var mLayout: View? = null
    private var mContainer: ViewGroup? = null

    private var storagesList: MutableList<Storages> = mutableListOf()
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

    private fun getLayout(inflater: LayoutInflater, container: ViewGroup?): View {
        // safe check if config from parcelable is accessible
        mConfig = arguments!!.getParcelable("config") as Config
        // init storage-chooser content [localization]
        mLayout = inflater.inflate(R.layout.storage_list, container, false)
        mHolderView = mLayout!!.findViewById(R.id.overview_container)
        initListView(mLayout!!)

        dialogTitle = mLayout!!.findViewById(R.id.dialog_title)
        viewDivider = mLayout!!.findViewById(R.id.storage_view_divider)
        dialogTitle.text = mConfig.content.overviewHeading

        applyDarkModeColors()

        return mLayout!!
    }

    /**
     * storage listView related code in this block
     */
    private fun initListView(view: View) {
        val listView = view.findViewById<ListView>(R.id.storage_list_view)

        // we need to populate before to get the internal storage path in list
        populateList()

        // TODO this adapter params can be improved
        listView.adapter = OverviewAdapter(storagesList, mContext, mConfig)


        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            val dirPath = evaluatePath(i)

            if(File(dirPath).canRead()) {

                if (mConfig.saveSelection) {
                    pref.edit().putString(StorageChooser2.SC_SAVED_PATH, dirPath).apply()
                }

                if(mConfig.type == ChooserType.BASIC) {
                    mConfig.selection.onSingleSelection(dirPath)
                    this@OverviewDialogFragment.dismiss()
                } else {
                    DiskUtil.showSecondaryChooser(dirPath, mConfig, activity!!.supportFragmentManager)
                    this@OverviewDialogFragment.dismiss()
                }

            } else {
                Toast.makeText(activity, R.string.toast_not_readable, Toast.LENGTH_SHORT).show()
            }
        }

    }


    /**
     * evaluates path with respect to the list click position
     *
     * @param i position in list
     * @return String with the required path for developers
     */
    private fun evaluatePath(i: Int): String {
        return if (i == 0) {
            Environment.getExternalStorageDirectory().absolutePath
        } else {
            "/storage/" + storagesList[i].storageTitle
        }
    }

    /**
     * populate storageList with necessary storages with filter applied
     */
    private fun populateList() {
        val storageDir = File("/storage")
        val internalStoragePath = Environment.getExternalStorageDirectory().absolutePath

        val volumeList = storageDir.listFiles()

        val storages = Storages()

        // just add the internal storage and avoid adding emulated henceforth
        storages.storageTitle = mConfig.content.internalStorageText

        storages.storagePath = internalStoragePath
        storages.memoryTotalSize = memoryUtil.formatSize(memoryUtil.getTotalMemorySize(internalStoragePath))
        storages.memoryAvailableSize = memoryUtil.formatSize(memoryUtil.getAvailableMemorySize(internalStoragePath))
        storagesList.add(storages)


        for (f in volumeList) {
            if (f.name != MemoryUtil.SELF_DIR_NAME
                    && f.name != MemoryUtil.EMULATED_DIR_KNOX
                    && f.name != MemoryUtil.EMULATED_DIR_NAME
                    && f.name != MemoryUtil.SDCARD0_DIR_NAME
                    && f.name != MemoryUtil.CONTAINER) {
                val sharedStorage = Storages()
                val fPath = f.absolutePath
                sharedStorage.storageTitle = f.name
                sharedStorage.memoryTotalSize = memoryUtil.formatSize(memoryUtil.getTotalMemorySize(fPath))
                sharedStorage.memoryAvailableSize = memoryUtil.formatSize(memoryUtil.getAvailableMemorySize(fPath))
                sharedStorage.storagePath = fPath
                storagesList.add(sharedStorage)
            }
        }

    }

    private fun applyDarkModeColors() {
        if (mConfig.darkMode) {
            mHolderView.setBackgroundColor(ContextCompat.getColor(mContext, mConfig.style.overviewStyle.backgroundColor))
            viewDivider.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dark_mode_divider))
            dialogTitle.setTextColor(ContextCompat.getColor(mContext, R.color.dark_mode_text))
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        mConfig.cancellation.onOverviewCancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.activity?.let {
            mContext = it.applicationContext
            val d = Dialog(it, R.style.DialogTheme)
            val layout = getLayout(LayoutInflater.from(activity!!.applicationContext), mContainer)
            d.setContentView(layout)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(d.window!!.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            d.window!!.attributes = lp
            return  d
        }
        mContext = this.activity!!.applicationContext
        return Dialog(this.activity!!.applicationContext, R.style.DialogTheme)
    }
}
