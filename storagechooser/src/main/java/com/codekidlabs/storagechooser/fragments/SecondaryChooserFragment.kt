package com.codekidlabs.storagechooser.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.codekidlabs.storagechooser.Content
import com.codekidlabs.storagechooser.R
import com.codekidlabs.storagechooser.utils.FileUtil
import com.codekidlabs.storagechooser.utils.ResourceUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton

import java.io.File
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.codekidlabs.storagechooser.Config
import com.codekidlabs.storagechooser.adapters.SecondaryChooserAdapter

//import com.codekidlabs.storagechooser.StorageChooser.Theme


class SecondaryChooserFragment : DialogFragment() {
    private var mLayout: View? = null
    private var mInactiveGradient: View? = null
    private var mContainer: ViewGroup? = null
    private var mPathChosen: TextView? = null
    private var mBackButton: ImageButton? = null
    private var mSelectButton: Button? = null
    private var mCreateButton: Button? = null
    private var mNewFolderImageView: ImageView? = null
    private var mFolderNameEditText: EditText? = null
    private var mMultipleOnSelectButton: FloatingActionButton? = null
    private var mNewFolderView: RelativeLayout? = null
    private var mFilesProgress: ProgressBar? = null
    private var mBundlePath: String? = null
    private var listView: ListView? = null
    private val isOpen: Boolean = false
    private var customStoragesList: MutableList<String>? = null
    private var secondaryChooserAdapter: SecondaryChooserAdapter? = null

    private var fileUtil: FileUtil? = null

    private var scheme: IntArray? = null
    private var mConfig: Config? = null
    private var mContent: Content? = null
    private var mContext: Context? = null
    private var mHandler: Handler? = null
    private var mResourceUtil: ResourceUtil? = null

    // multiple mode stuffs
    private val mMultipleModeList = ArrayList<String>()

    /**
     * THE HOLY PLACE OF CLICK LISTENERS
     */
    private val mSelectButtonClickListener = View.OnClickListener {
        if (mConfig!!.saveSelection) {
//            DiskUtil.saveChooserPathPreference(mConfig!!.getPreference(), theSelectedPath)
        } else {
            Log.d("StorageChooser", "Chosen path: " + theSelectedPath!!)
        }

//        StorageChooser.onSelectListener.onSelect(theSelectedPath)
        dissmissDialog(FLAG_DISSMISS_NORMAL)
    }
    private val mNewFolderButtonCloseListener = View.OnClickListener {
        hideAddFolderView()
        hideKeyboard()
    }
    private val mNewFolderButtonClickListener = View.OnClickListener { showAddFolderView() }
    private val keyboardToggle: Boolean = false
    private val TAG = "StorageChooser"
    private var isFilePicker: Boolean = false
    private val mCreateButtonClickListener = View.OnClickListener {
        if (validateFolderName()) {
            val success = FileUtil.createDirectory(mFolderNameEditText!!.text.toString().trim { it <= ' ' }, theSelectedPath)
            if (success) {
                Toast.makeText(mContext, mContent!!.folderCreatedToastText, Toast.LENGTH_SHORT).show()
                trimPopulate(theSelectedPath)
                hideKeyboard()
                hideAddFolderView()
            } else {
                Toast.makeText(mContext, mContent!!.folderErrorToastText, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val mSingleModeClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
        mHandler!!.postDelayed({
            val jointPath = theSelectedPath + "/" + customStoragesList!![i]
            if (FileUtil.isDir(jointPath)) {
                populateList("/" + customStoragesList!![i])
            } else {
//                StorageChooser.onSelectListener.onSelect(jointPath)
                dissmissDialog(FLAG_DISSMISS_NORMAL)
            }
        }, 300)
    }
    private val mLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
        val jointPath = theSelectedPath + "/" + customStoragesList!![i]

        if (!FileUtil.isDir(jointPath)) {
            MODE_MULTIPLE = true
            listView!!.onItemClickListener = mMultipleModeClickListener
            handleListMultipleAction(i, view)
        } else {
            populateList("/" + customStoragesList!![i])
        }

        true
    }


    private val mBackButtonClickListener = View.OnClickListener { performBackAction() }
    private val mMultipleModeDoneButtonClickListener = View.OnClickListener {
//        StorageChooser.onMultipleSelectListener.onDone(mMultipleModeList)
        bringBackSingleMode()
        dissmissDialog(FLAG_DISSMISS_NORMAL)
    }
    private val mMultipleModeClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
        val jointPath = theSelectedPath + "/" + customStoragesList!![i]

        if (!FileUtil.isDir(jointPath)) {
            handleListMultipleAction(i, view)
        } else {
            bringBackSingleMode()
            populateList("/" + customStoragesList!![i])
        }
    }

    private val isFolderViewVisible: Boolean
        get() = mNewFolderView!!.visibility == View.VISIBLE

    // ================ CLICK LISTENER END ==================

    private fun showAddFolderView() {
        mNewFolderView!!.visibility = View.VISIBLE
        val anim = AnimationUtils.loadAnimation(mContext, R.anim.anim_new_folder_view)
        mNewFolderView!!.startAnimation(anim)
        mInactiveGradient!!.startAnimation(anim)


//        if (DiskUtil.isLollipopAndAbove()) {
//            mNewFolderImageView!!.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.drawable_plus_to_close))
//            // image button animation
//            val animatable = mNewFolderImageView!!.drawable as Animatable
//            animatable.start()
//        }
        mNewFolderImageView!!.setOnClickListener(mNewFolderButtonCloseListener)

        // OLD---       mNewFolderButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.window_close));

        //listview should not be clickable
//        SecondaryChooserAdapter.shouldEnable = false
    }

    private fun hideAddFolderView() {
        val anim = AnimationUtils.loadAnimation(mContext, R.anim.anim_close_folder_view)
        mNewFolderView!!.startAnimation(anim)
        mNewFolderView!!.visibility = View.INVISIBLE

//        if (DiskUtil.isLollipopAndAbove()) {
//            mNewFolderImageView!!.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.drawable_close_to_plus))
//            // image button animation
//            val animatable = mNewFolderImageView!!.drawable as Animatable
//            animatable.start()
//        }
        mNewFolderImageView!!.setOnClickListener(mNewFolderButtonClickListener)

        //listview should be clickable
//        SecondaryChooserAdapter.shouldEnable = true

        mInactiveGradient!!.startAnimation(anim)
        mInactiveGradient!!.visibility = View.INVISIBLE

        //   OLD --     mNewFolderButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.plus));
    }

    fun hideKeyboard() {
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mFolderNameEditText!!.windowToken, 0)

    }

    private fun performBackAction() {
        val slashIndex = theSelectedPath!!.lastIndexOf("/")

        if (slashIndex != -1) {
            if (MODE_MULTIPLE) {
                bringBackSingleMode()
                secondaryChooserAdapter!!.notifyDataSetChanged()

            } else {
//                if (!mConfig!!.isSkipOverview()) {
//                    if (theSelectedPath == mBundlePath) {
//                        this@SecondaryChooserFragment.dismiss()
//
//                        //delay until close animation ends
//                        mHandler!!.postDelayed({ dissmissDialog(FLAG_DISSMISS_INIT_DIALOG) }, 200)
//                    } else {
//                        theSelectedPath = theSelectedPath!!.substring(0, slashIndex)
//                        StorageChooser.LAST_SESSION_PATH = theSelectedPath
//                        populateList("")
//                    }
//                } else {
//                    dissmissDialog(FLAG_DISSMISS_NORMAL)
//                }
            }
        } else {
            // let's just say that there is no / in the path at any given point
            // which is hard to imagine but.. what to do at that time ?
            // we set it to bundle path until the issue is totally investigated
            // TODO - dig deep about this condition !
            theSelectedPath = mBundlePath
//            StorageChooser.LAST_SESSION_PATH = theSelectedPath
            populateList("")
        }

    }

    private fun dissmissDialog(flag: Int) {

        when (flag) {
            FLAG_DISSMISS_INIT_DIALOG -> {
                val c = OverviewDialogFragment()
                c.show(activity!!.supportFragmentManager, "storagechooser_dialog")
            }
            FLAG_DISSMISS_NORMAL -> {
//                StorageChooser.LAST_SESSION_PATH = theSelectedPath
                this.dismiss()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContainer = container
        return if (showsDialog) {
            // one could return null here, or be nice and call super()
            super.onCreateView(inflater, container, savedInstanceState)
        } else getLayout(inflater, container)
    }

    private fun getLayout(inflater: LayoutInflater, container: ViewGroup?): View {
        mConfig = arguments!!.getParcelable("config") as Config
//        scheme = mConfig!!.getScheme()
        mHandler = Handler()

        // init storage-chooser content [localization]
        mContent = mConfig!!.content


        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.DialogTheme)
        val li = inflater.cloneInContext(contextThemeWrapper)

        mContext = activity!!.applicationContext
        mResourceUtil = ResourceUtil(mContext)
        mLayout = li.inflate(R.layout.custom_storage_list, container, false)

        initListView(mContext, mLayout!!, mConfig!!.showMemoryBar)

        initUI()
        initNewFolderView()
        updateUI()

        return mLayout!!
    }


    private fun initUI() {

//        mBackButton = mLayout!!.findViewById(R.id.back_button)
//        mSelectButton = mLayout!!.findViewById(R.id.select_button)
        mMultipleOnSelectButton = mLayout!!.findViewById(R.id.multiple_selection_done_fab)

//        mCreateButton = mLayout!!.findViewById(R.id.create_folder_button)

        mNewFolderView = mLayout!!.findViewById(R.id.new_folder_view)
//        mNewFolderView!!.setBackgroundColor(scheme!![Theme.SEC_FOLDER_CREATION_BG_INDEX])
        mFolderNameEditText = mLayout!!.findViewById(R.id.et_folder_name)

        mInactiveGradient = mLayout!!.findViewById(R.id.inactive_gradient)

//        mLayout!!.findViewById<View>(R.id.secondary_container).setBackgroundColor(scheme!![Theme.SEC_BG_INDEX])

    }

    private fun updateUI() {

        //at start dont show the new folder view unless user clicks on the add/plus button
        mNewFolderView!!.visibility = View.INVISIBLE
        mInactiveGradient!!.visibility = View.INVISIBLE


//        mFolderNameEditText!!.setHint(mContent!!.getTextfieldHintText())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mFolderNameEditText!!.setHintTextColor(scheme!![Theme.SEC_HINT_TINT_INDEX])
        }


        // set label of buttons [localization]
//        mSelectButton!!.text = mContent!!.selectLabel
//        mCreateButton!!.text = mContent!!.createLabel


        // set colors
//        mSelectButton!!.setTextColor(scheme!![Theme.SEC_SELECT_LABEL_INDEX])
//        mPathChosen!!.setTextColor(scheme!![Theme.SEC_ADDRESS_TINT_INDEX])

        // set addressbar typeface
//        if (mConfig!!.getHeadingFont() != null) {
//            mPathChosen!!.typeface = OverviewDialogFragment.getSCTypeface(mContext!!,
//                    mConfig!!.getHeadingFont(), mConfig!!.isHeadingFromAssets())
//        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mNewFolderImageView!!.imageTintList = ColorStateList.valueOf(scheme!![Theme.SEC_ADDRESS_TINT_INDEX])
//            mBackButton!!.imageTintList = ColorStateList.valueOf(scheme!![Theme.SEC_ADDRESS_TINT_INDEX])
////        }
//        mMultipleOnSelectButton!!.backgroundTintList = ColorStateList.valueOf(scheme!![Theme.SEC_DONE_FAB_INDEX])
//        mLayout!!.findViewById<View>(R.id.custom_path_header).setBackgroundColor(scheme!![Theme.SEC_ADDRESS_BAR_BG])

        // ----

//        mBackButton!!.setOnClickListener(mBackButtonClickListener)
//        mSelectButton!!.setOnClickListener(mSelectButtonClickListener)
//        mCreateButton!!.setOnClickListener(mCreateButtonClickListener)
        mMultipleOnSelectButton!!.setOnClickListener(mMultipleModeDoneButtonClickListener)
//
//        if (mConfig!!.getSecondaryAction().equals(StorageChooser.FILE_PICKER)) {
//            mSelectButton!!.visibility = View.GONE
//            setBottomNewFolderView()
//        }

    }

    private fun setBottomNewFolderView() {
        val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80f, resources.displayMetrics).toInt()
        val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height)
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        mNewFolderView!!.layoutParams = lp
    }

    private fun initNewFolderView() {

//        val mNewFolderButtonHolder = mLayout!!.findViewById<RelativeLayout>(R.id.new_folder_button_holder)
//
//        mNewFolderImageView = mLayout!!.findViewById(R.id.new_folder_iv)
//        mNewFolderImageView!!.setOnClickListener(mNewFolderButtonClickListener)

//        if (!mConfig!!.isAllowAddFolder()) {
//            mNewFolderButtonHolder.visibility = View.GONE
//        }

    }


    /**
     * storage listView related code in this block
     */
    private fun initListView(context: Context?, view: View, shouldShowMemoryBar: Boolean) {
        listView = view.findViewById(R.id.storage_list_view)
        mPathChosen = view.findViewById(R.id.path_chosen)
        mFilesProgress = mLayout!!.findViewById(R.id.files_loader)
        mFilesProgress!!.isIndeterminate = true
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mFilesProgress!!.indeterminateTintList = ColorStateList.valueOf(scheme!![Theme.OVERVIEW_MEMORYBAR_INDEX])
//        }
//
//        mBundlePath = this.arguments!!.getString(DiskUtil.SC_PREFERENCE_KEY)
//        isFilePicker = this.arguments!!.getBoolean(DiskUtil.SC_CHOOSER_FLAG, false)
//        populateList(mBundlePath)
//        secondaryChooserAdapter = SecondaryChooserAdapter(customStoragesList, context, scheme,
//                mConfig!!.getListFont(), mConfig!!.isListFromAssets())
//        secondaryChooserAdapter!!.setPrefixPath(theSelectedPath)

        listView!!.adapter = secondaryChooserAdapter
        //listview should be clickable at first
//        SecondaryChooserAdapter.shouldEnable = true
        listView!!.onItemClickListener = mSingleModeClickListener

//        if (isFilePicker && mConfig!!.isMultiSelect()) {
//            listView!!.onItemLongClickListener = mLongClickListener
//        }

    }

    /**
     * handles actions in multiple mode
     * like adding to list and setting background color
     *
     * @param i is position of list clicked
     */
    private fun handleListMultipleAction(i: Int, view: View) {
        val jointPath = theSelectedPath + "/" + customStoragesList!![i]

        // if this list path item is not selected before
        if (!secondaryChooserAdapter!!.selectedPaths.contains(i)) {
            view.setBackgroundColor(mResourceUtil!!.primaryColorWithAlpha)

            secondaryChooserAdapter!!.selectedPaths.add(i)
            mMultipleModeList.add(jointPath)

        } else {
            //this item is selected before
            secondaryChooserAdapter!!.selectedPaths.remove(secondaryChooserAdapter!!.selectedPaths.indexOf(i))
            // reset bg to white
//            view.setBackgroundColor(scheme!![Theme.SEC_BG_INDEX])
            mMultipleModeList.removeAt(mMultipleModeList.indexOf(jointPath))
        }

        if (mMultipleOnSelectButton!!.visibility != View.VISIBLE && MODE_MULTIPLE)
            playTheMultipleButtonAnimation()

        if (listView!!.onItemLongClickListener != null && MODE_MULTIPLE)
        // long click listener in multiple mode ? haha nice joke
            listView!!.onItemLongClickListener = null

        if (mMultipleModeList.size == 0)
            bringBackSingleMode()
    }

    //    private int getListIndex(int i) {
    //        for(int j=0; j<secondaryChooserAdapter.selectedPaths.size(); j++) {
    //            if()
    //        }
    //    }

    /**
     * brings back the default state of storage-chooser
     */
    private fun bringBackSingleMode() {
        // if selected new directory end the multiple mode
        MODE_MULTIPLE = false
        // set listview to single mode click
        listView!!.onItemClickListener = mSingleModeClickListener
        // clear both path list and adapter item list
        mMultipleModeList.clear()
        secondaryChooserAdapter!!.selectedPaths.clear()
        // remove access to done button
        playTheMultipleButtonEndAnimation()
        // aaaaaaaaand bring back long click listener
        listView!!.onItemLongClickListener = mLongClickListener
    }


    /**
     * evaluates path with respect to the list click position
     *
     * @param i position in list
     */
    private fun evaluateAction(i: Int) {
//        val preDefPath = mConfig!!.getPredefinedPath()
//        val isCustom = mConfig!!.isAllowCustomPath()
//        if (preDefPath == null) {
//            Log.w(TAG, "No predefined path set")
//        } else if (isCustom) {
//            populateList("/" + customStoragesList!![i])
//        }
    }

    private fun doesPassMemoryThreshold(threshold: Long, memorySuffix: String, availableSpace: Long): Boolean {
        return true
    }

    /**
     * populate storageList with necessary storages with filter applied
     *
     * @param path defines the path for which list of folder is requested
     */
    private fun populateList(path: String?) {
        if (customStoragesList == null) {
            customStoragesList = ArrayList()
        } else {
            customStoragesList!!.clear()
        }

        fileUtil = FileUtil()
        theSelectedPath = theSelectedPath!! + path!!
//        if (secondaryChooserAdapter != null && secondaryChooserAdapter!!.getPrefixPath() != null) {
//            secondaryChooserAdapter!!.setPrefixPath(theSelectedPath)
//        }

        //if the path length is greater than that of the addressbar length
        // we need to clip the starting part so that it fits the length and makes some room
        val pathLength = theSelectedPath!!.length
        if (pathLength >= 25) {
            // how many directories did user choose
            val slashCount = getSlashCount(theSelectedPath!!)
            if (slashCount > 2) {
                mAddressClippedPath = theSelectedPath!!.substring(theSelectedPath!!.indexOf("/", theSelectedPath!!.indexOf("/") + 2), pathLength)
            } else if (slashCount <= 2) {
                mAddressClippedPath = theSelectedPath!!.substring(theSelectedPath!!.indexOf("/", theSelectedPath!!.indexOf("/") + 2), pathLength)
            }
        } else {
//            mAddressClippedPath = theSelectedPath
        }

        val volumeList: Array<File>

        if (isFilePicker) {
//            if (mConfig!!.isCustomFilter()) {
//                // we need to start a async task here because the filter loops through all files
//                // and if the files are more it'll take ages and it'll cause ANR as this is
//                // running on main thread
//                FileFilterTask(mConfig, true).execute()
//            } else {
//                if (mConfig!!.getSingleFilter() != null) {
//                    FileFilterTask(mConfig, false).execute()
//                } else {
//                    volumeList = fileUtil!!.listFilesInDir(theSelectedPath)
//                    setAdapterList(volumeList)
//                    refreshList()
//                    setBundlePathOnUpdate()
//                }
//            }
        } else {
            volumeList = fileUtil!!.listFilesAsDir(theSelectedPath)
            setAdapterList(volumeList)
            refreshList()
            setBundlePathOnUpdate()
        }

        playTheAddressBarAnimation()
    }

    /**
     * setBundlePathOnUpdate sets the mBundlePath on each list element change. Bundle path is used
     * to change the main working directory of chooser.
     */
    fun setBundlePathOnUpdate() {
//        if (mConfig!!.isResumeSession() && StorageChooser.LAST_SESSION_PATH != null) {
//            if (StorageChooser.LAST_SESSION_PATH.startsWith(Environment.getExternalStorageDirectory().absolutePath)) {
//                mBundlePath = Environment.getExternalStorageDirectory().absolutePath
//            } else {
//                mBundlePath = StorageChooser.LAST_SESSION_PATH.substring(StorageChooser.LAST_SESSION_PATH.indexOf("/", 16), StorageChooser.LAST_SESSION_PATH.length())
//            }
//        }
    }

    /**
     * Refreshes the list LOL. It checks if adapter is null and notifies change of list on each
     * file list update.
     */
    fun refreshList() {
        if (secondaryChooserAdapter != null) {
            secondaryChooserAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * Accepts a file list and sets it for use of storage chooser. Called before all refreshList()
     *
     * @param volumeList File list to be shown in storage chooser
     */
    fun setAdapterList(volumeList: Array<File>?) {
        if (volumeList != null) {
            for (f in volumeList) {
//                if (mConfig!!.isShowHidden()) {
//                    customStoragesList!!.add(f.name)
//                } else {
//                    if (!f.name.startsWith(".")) {
//                        customStoragesList!!.add(f.name)
//                    }
//                }
            }

            Collections.sort(customStoragesList!!) { s1, s2 -> s1.compareTo(s2, ignoreCase = true) }
        } else {
            customStoragesList!!.clear()
        }
    }

    /**
     * Unlike populate list trim populate only updates the list not the addressbar.
     * This is used when creating new folder and update to list is required
     *
     * @param s is the path to be refreshed
     */
    private fun trimPopulate(s: String?) {
        if (customStoragesList == null) {
            customStoragesList = ArrayList()
        } else {
            customStoragesList!!.clear()
        }
        val volumeList: Array<File>?

        if (isFilePicker) {
            volumeList = fileUtil!!.listFilesInDir(theSelectedPath)
        } else {
            volumeList = fileUtil!!.listFilesAsDir(theSelectedPath)
        }

        Log.e("SCLib", theSelectedPath)
        if (volumeList != null) {
            for (f in volumeList) {
                if (!f.name.startsWith(".")) {
                    customStoragesList!!.add(f.name)
                }
            }

            Collections.sort(customStoragesList!!) { s1, s2 -> s1.compareTo(s2, ignoreCase = true) }
        } else {
            customStoragesList!!.clear()
        }


        if (secondaryChooserAdapter != null) {
//            secondaryChooserAdapter!!.setPrefixPath(s)
            secondaryChooserAdapter!!.notifyDataSetChanged()
        }
    }

    // ======================= ANIMATIONS =========================
    private fun playTheAddressBarAnimation() {
        mPathChosen!!.text = mAddressClippedPath
        val animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_address_bar)
        mPathChosen!!.startAnimation(animation)
    }


    private fun playTheMultipleButtonAnimation() {
        val animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_multiple_button)
        mMultipleOnSelectButton!!.show()
        mMultipleOnSelectButton!!.startAnimation(animation)
    }


    private fun playTheMultipleButtonEndAnimation() {
        val animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_multiple_button_end)
        mMultipleOnSelectButton!!.startAnimation(animation)
        mMultipleOnSelectButton!!.hide()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.activity?.let {
            it.applicationContext
            val d = Dialog(it, R.style.DialogTheme)
//            d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            d.setContentView(getLayout(LayoutInflater.from(activity!!.applicationContext), mContainer))
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(d.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            d.window!!.attributes = lp
            return  d
        }
        return Dialog(this.activity!!.applicationContext, R.style.DialogTheme)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        theSelectedPath = ""
        mAddressClippedPath = ""
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
//        StorageChooser.LAST_SESSION_PATH = theSelectedPath
        theSelectedPath = ""
        mAddressClippedPath = ""

//        StorageChooser.onCancelListener.onCancel()
    }

    private fun getSlashCount(path: String): Int {
        var count = 0

        for (s in path.toCharArray()) {
            if (s == '/') {
                count++
            }
        }
        return count
    }

    /**
     * Checks if edit text field is empty or not. Since there is only one edit text here no
     * param is required for now.
     */
    private fun validateFolderName(): Boolean {
        if (mFolderNameEditText!!.text.toString().trim { it <= ' ' }.isEmpty()) {
            mFolderNameEditText!!.error = mContent!!.textFieldErrorText
            return false
        }
        return true
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class FileFilterTask(private val mConfig: Config, private val isMultiple: Boolean) : AsyncTask<Void, String, Boolean>() {
        private var fileList: Array<File>? = null

        override fun onPreExecute() {
            super.onPreExecute()
            mFilesProgress!!.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg voids: Void): Boolean? {
//            if (isMultiple) {
//                val universalFileFilter = UniversalFileFilter(mConfig.isCustomFilter(), mConfig.getCustomEnum())
//                fileList = File(theSelectedPath!!)
//                        .listFiles(universalFileFilter)
//            } else {
//                fileList = File(theSelectedPath!!).listFiles(UniversalFileFilter(mConfig.getSingleFilter()))
//            }
            return true
        }

        override fun onPostExecute(aBoolean: Boolean?) {
            super.onPostExecute(aBoolean)
            mFilesProgress!!.visibility = View.INVISIBLE
            setAdapterList(this.fileList)
            refreshList()
            setBundlePathOnUpdate()
        }
    }

    companion object {

        private val INTERNAL_STORAGE_TITLE = "Internal Storage"
        private val EXTERNAL_STORAGE_TITLE = "ExtSD"
        private val FLAG_DISSMISS_NORMAL = 0
        private val FLAG_DISSMISS_INIT_DIALOG = 1
        private var MODE_MULTIPLE = false
        private var theSelectedPath: String? = ""
        private var mAddressClippedPath = ""
    }
}
