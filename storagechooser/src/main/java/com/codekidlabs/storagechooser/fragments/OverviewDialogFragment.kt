package com.codekidlabs.storagechooser.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.codekidlabs.storagechooser.*
import com.codekidlabs.storagechooser.adapters.OverviewAdapter

import com.codekidlabs.storagechooser.models.Storages
import com.codekidlabs.storagechooser.utils.FileUtil
import com.codekidlabs.storagechooser.utils.MemoryUtil

import java.io.File
import java.util.ArrayList


class OverviewDialogFragment : DialogFragment() {
    private var mLayout: View? = null
    private var mContainer: ViewGroup? = null

    private var storagesList: MutableList<Storages>? = null
    private val customStoragesList: List<String>? = null
    private val TAG = javaClass.name
    private val memoryUtil = MemoryUtil()
    private val fileUtil = FileUtil()

    private lateinit var mConfig: Config
    private var mContent: Content? = null

    // day night flag
    // TODO: provide a default day and night theme for storage chooser
    private val mChooserMode: Int = 0

    // delaying secondary chooser
    private var mHandler: Handler? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContainer = container
        return if (showsDialog) {
            super.onCreateView(inflater, container, savedInstanceState)
        } else getLayout(inflater, container)
    }

    private fun getLayout(inflater: LayoutInflater, container: ViewGroup?): View {
        // safe check if config from parcelable is accessible
        mConfig = arguments.let {
            if(it == null) {
                it?.getParcelable("test") as Config
            } else {
                Config()
            }
        }
        mHandler = Handler()
        // init storage-chooser content [localization]
        if (mConfig!!.content == null) {
            mContent = Content()
        } else {
            mContent = mConfig!!.content
        }
        mLayout = inflater.inflate(R.layout.storage_list, container, false)
        initListView(activity!!.applicationContext, mLayout!!, mConfig!!.showMemoryBar)

        if (mContent!!.overviewHeading != null) {
            val dialogTitle = mLayout!!.findViewById<TextView>(R.id.dialog_title)
//            dialogTitle.setTextColor(mConfig!!.scheme[OVERVIEW_TEXT_INDEX])
            dialogTitle.text = mContent!!.overviewHeading

            // set heading typeface
//            if (mConfig!!.headingFont != null) {
//                dialogTitle.typeface = getSCTypeface(activity!!.applicationContext,
//                        mConfig!!.headingFont,
//                        mConfig!!.isHeadingFromAssets)
//            }
        }

//        mLayout!!.findViewById<View>(R.id.header_container).setBackgroundColor(
//                mConfig!!.scheme[OVERVIEW_HEADER_INDEX])
//        mLayout!!.findViewById<View>(R.id.overview_container).setBackgroundColor(
//                mConfig!!.scheme[OVERVIEW_BG_INDEX])

        return mLayout!!
    }

    /**
     * storage listView related code in this block
     */
    private fun initListView(context: Context, view: View, shouldShowMemoryBar: Boolean) {
        val listView = view.findViewById<ListView>(R.id.storage_list_view)

        // we need to populate before to get the internal storage path in list
        populateList()

        // TODO this adapter params can be improved
        listView.adapter = OverviewAdapter(storagesList!!, this.activity!!.applicationContext, this.mConfig)


//        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
//            val dirPath = evaluatePath(i)
//
//            if (File(dirPath).canRead()) {
//                // if allowCustomPath is called then directory chooser will be the default secondary dialog
//                if (mConfig!!.type != ChooserType.BASIC) {
//                    // if developer wants to apply threshold
//                    if (mConfig!!.isApplyThreshold) {
//                        startThresholdTest(i)
//                    } else {
//
//                        if (BUILD_DEBUG) {
//                            mHandler!!.postDelayed({ DiskUtil.showSecondaryChooser(dirPath, mConfig!!) }, 250)
//                        } else {
//                            DiskUtil.showSecondaryChooser(dirPath, mConfig!!)
//                        }
//
//
//                    }
//                } else {
//                    if (mConfig!!.saveSelection) {
//                        var preDef: String? = mConfig!!.predefinedPath
//                        // if dev forgot or did not add '/' at start add it to avoid errors
//                        var preDirPath: String? = null
//
//                        if (preDef != null) {
//                            if (!preDef.startsWith("/")) {
//                                preDef = "/$preDef"
//                            }
//                            preDirPath = dirPath + preDef
//                            DiskUtil.saveChooserPathPreference(mConfig!!.preference, preDirPath)
//                        } else {
//                            Log.w(TAG, "Predefined path is null set it by .withPredefinedPath() to builder. Saving root directory")
//                            DiskUtil.saveChooserPathPreference(mConfig!!.preference, preDirPath)
//                        }
//                    } else {
//                        //Log.d("StorageChooser", "Chosen path: " + dirPath);
//                        if (mConfig!!.isApplyThreshold) {
//                            startThresholdTest(i)
//                        } else {
//                            if (StorageChooser.onSelectListener != null) {
//                                StorageChooser.onSelectListener.onSelect(dirPath)
//                            }
//                        }
//                    }
//                }
//                this@OverviewDialogFragment.dismiss()
//            } else {
//                Toast.makeText(activity, R.string.toast_not_readable, Toast.LENGTH_SHORT)
//                        .show()
//            }
//        }

    }

    /**
     * initiate to take threshold test
     *
     * @param position list click index
     */

    private fun startThresholdTest(position: Int) {
//        val thresholdSuffix = mConfig!!.thresholdSuffix

        // if threshold suffix is null then memory threshold is also null
//        if (thresholdSuffix != null) {
//            val availableMem = memoryUtil.getAvailableMemorySize(evaluatePath(position))
//
//
//            if (doesPassThresholdTest(mConfig!!.memoryThreshold.toLong(), thresholdSuffix, availableMem)) {
//                val dirPath = evaluatePath(position)
//                DiskUtil.showSecondaryChooser(dirPath, mConfig!!)
//            } else {
//                val suffixedAvailableMem = memoryUtil.suffixedSize(availableMem, thresholdSuffix).toString() + " " + thresholdSuffix
//                Toast.makeText(activity!!.applicationContext, getString(R.string.toast_threshold_breached, suffixedAvailableMem), Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            // THROW: error in log
//            Log.e(TAG, "add .withThreshold(int size, String suffix) to your StorageChooser.Builder instance")
//        }
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
            "/storage/" + storagesList!![i].storageTitle
        }
    }

    /**
     * checks if available space in user's device is greater than the developer defined threshold
     *
     * @param threshold      defined by the developer using Config.withThreshold()
     * @param memorySuffix   also defined in Config.withThreshold() - check in GB, MB, KB ?
     * @param availableSpace statfs available mem in bytes (long)
     * @return if available memory is more than threshold
     */
    private fun doesPassThresholdTest(threshold: Long, memorySuffix: String, availableSpace: Long): Boolean {
        return memoryUtil.suffixedSize(availableSpace, memorySuffix) > threshold
    }

    /**
     * populate storageList with necessary storages with filter applied
     */
    private fun populateList() {
        storagesList = ArrayList()

        val storageDir = File("/storage")
        val internalStoragePath = Environment.getExternalStorageDirectory().absolutePath

        val volumeList = storageDir.listFiles()

        val storages = Storages()

        // just add the internal storage and avoid adding emulated henceforth
        storages.storageTitle = mContent!!.internalStorageText

        storages.storagePath = internalStoragePath
        storages.memoryTotalSize = memoryUtil.formatSize(memoryUtil.getTotalMemorySize(internalStoragePath))
        storages.memoryAvailableSize = memoryUtil.formatSize(memoryUtil.getAvailableMemorySize(internalStoragePath))
        storagesList!!.add(storages)


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
                storagesList!!.add(sharedStorage)
            }
        }

    }

//    override fun onCancel(dialog: DialogInterface) {
//        super.onCancel(dialog)
//        StorageChooser.onCancelListener.onCancel()
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.activity?.let {
            it.applicationContext
            val d = Dialog(it, R.style.DialogTheme)
            d.setContentView(getLayout(LayoutInflater.from(activity!!.applicationContext), mContainer))
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(d.window!!.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            d.window!!.attributes = lp
            return  d
        }
        return Dialog(this.activity!!.applicationContext, R.style.DialogTheme)
    }

    companion object {

        private val BUILD_DEBUG = true

        // Convinience methods

        fun getSCTypeface(context: Context, path: String, assets: Boolean): Typeface {
            return if (assets) {
                Typeface.createFromAsset(context.assets,
                        path)
            } else Typeface.createFromFile(path)
        }
    }
}
