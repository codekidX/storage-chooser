package com.codekidlabs.storagechooser.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.Animatable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.Content;
import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.adapters.SecondaryChooserAdapter;
import com.codekidlabs.storagechooser.filters.UniversalFileFilter;
import com.codekidlabs.storagechooser.models.Config;
import com.codekidlabs.storagechooser.utils.DiskUtil;
import com.codekidlabs.storagechooser.utils.FileUtil;
import com.codekidlabs.storagechooser.utils.ResourceUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import static com.codekidlabs.storagechooser.StorageChooser.Theme;


public class SecondaryChooserFragment extends android.app.DialogFragment {

    private static final String INTERNAL_STORAGE_TITLE = "Internal Storage";
    private static final String EXTERNAL_STORAGE_TITLE = "ExtSD";
    private static final int FLAG_DISSMISS_NORMAL = 0;
    private static final int FLAG_DISSMISS_INIT_DIALOG = 1;
    private static boolean MODE_MULTIPLE = false;
    private static String theSelectedPath = "";
    private static String mAddressClippedPath = "";
    private View mLayout;
    private View mInactiveGradient;
    private ViewGroup mContainer;
    private TextView mPathChosen;
    private ImageButton mBackButton;
    private Button mSelectButton;
    private Button mCreateButton;
    private ImageView mNewFolderImageView;
    private EditText mFolderNameEditText;
    private FloatingActionButton mMultipleOnSelectButton;
    private RelativeLayout mNewFolderView;
    private ProgressBar mFilesProgress;
    private String mBundlePath;
    private ListView listView;
    private boolean isOpen;
    private List<String> customStoragesList;
    private SecondaryChooserAdapter secondaryChooserAdapter;

    private FileUtil fileUtil;

    private int[] scheme;
    private Config mConfig;
    private Content mContent;
    private Context mContext;
    private Handler mHandler;
    private ResourceUtil mResourceUtil;

    // multiple mode stuffs
    private ArrayList<String> mMultipleModeList = new ArrayList<>();

    /**
     * THE HOLY PLACE OF CLICK LISTENERS
     */
    private View.OnClickListener mSelectButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mConfig.isActionSave()) {
                DiskUtil.saveChooserPathPreference(mConfig.getPreference(), theSelectedPath);
            } else {
                Log.d("StorageChooser", "Chosen path: " + theSelectedPath);
            }

            StorageChooser.onSelectListener.onSelect(theSelectedPath);
            dissmissDialog(FLAG_DISSMISS_NORMAL);
        }

    };
    private View.OnClickListener mNewFolderButtonCloseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideAddFolderView();
            hideKeyboard();
        }
    };
    private View.OnClickListener mNewFolderButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showAddFolderView();
        }
    };
    private boolean keyboardToggle;
    private final String TAG = "StorageChooser";
    private boolean isFilePicker;
    private View.OnClickListener mCreateButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (validateFolderName()) {
                boolean success = FileUtil.createDirectory(mFolderNameEditText.getText().toString().trim(), theSelectedPath);
                if (success) {
                    Toast.makeText(mContext, mContent.getFolderCreatedToastText(), Toast.LENGTH_SHORT).show();
                    trimPopulate(theSelectedPath);
                    hideKeyboard();
                    hideAddFolderView();
                } else {
                    Toast.makeText(mContext, mContent.getFolderErrorToastText(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    private AdapterView.OnItemClickListener mSingleModeClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String jointPath = theSelectedPath + "/" + customStoragesList.get(i);
                    if (FileUtil.isDir(jointPath)) {
                        populateList("/" + customStoragesList.get(i));
                    } else {
                        StorageChooser.onSelectListener.onSelect(jointPath);
                        dissmissDialog(FLAG_DISSMISS_NORMAL);
                    }
                }
            }, 300);
        }
    };
    private AdapterView.OnItemLongClickListener mLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

            String jointPath = theSelectedPath + "/" + customStoragesList.get(i);

            if (!FileUtil.isDir(jointPath)) {
                MODE_MULTIPLE = true;
                listView.setOnItemClickListener(mMultipleModeClickListener);
                handleListMultipleAction(i, view);
            } else {
                populateList("/" + customStoragesList.get(i));
            }

            return true;
        }
    };


    private View.OnClickListener mBackButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            performBackAction();
        }
    };
    private View.OnClickListener mMultipleModeDoneButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            StorageChooser.onMultipleSelectListener.onDone(mMultipleModeList);
            bringBackSingleMode();
            dissmissDialog(FLAG_DISSMISS_NORMAL);
        }
    };
    private AdapterView.OnItemClickListener mMultipleModeClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
            String jointPath = theSelectedPath + "/" + customStoragesList.get(i);

            if (!FileUtil.isDir(jointPath)) {
                handleListMultipleAction(i, view);
            } else {
                bringBackSingleMode();
                populateList("/" + customStoragesList.get(i));
            }

        }
    };

    // ================ CLICK LISTENER END ==================

    private void showAddFolderView() {
        mNewFolderView.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.anim_new_folder_view);
        mNewFolderView.startAnimation(anim);
        mInactiveGradient.startAnimation(anim);


        if (DiskUtil.isLollipopAndAbove()) {
            mNewFolderImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.drawable_plus_to_close));
            // image button animation
            Animatable animatable = (Animatable) mNewFolderImageView.getDrawable();
            animatable.start();
        }
        mNewFolderImageView.setOnClickListener(mNewFolderButtonCloseListener);

//        mNewFolderButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.window_close));

        //listview should not be clickable
        SecondaryChooserAdapter.shouldEnable = false;
    }

    private void hideAddFolderView() {
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.anim_close_folder_view);
        mNewFolderView.startAnimation(anim);
        mNewFolderView.setVisibility(View.INVISIBLE);

        if (DiskUtil.isLollipopAndAbove()) {
            mNewFolderImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.drawable_close_to_plus));
            // image button animation
            Animatable animatable = (Animatable) mNewFolderImageView.getDrawable();
            animatable.start();
        }
        mNewFolderImageView.setOnClickListener(mNewFolderButtonClickListener);

        //listview should be clickable
        SecondaryChooserAdapter.shouldEnable = true;

        mInactiveGradient.startAnimation(anim);
        mInactiveGradient.setVisibility(View.INVISIBLE);

//        mNewFolderButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.plus));
    }

    private boolean isFolderViewVisible() {
        return mNewFolderView.getVisibility() == View.VISIBLE;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mFolderNameEditText.getWindowToken(), 0);

    }

    private void performBackAction() {
        int slashIndex = theSelectedPath.lastIndexOf("/");

        if(slashIndex != -1) {
            if (MODE_MULTIPLE) {
                bringBackSingleMode();
                secondaryChooserAdapter.notifyDataSetChanged();

            } else {
                if (!mConfig.isSkipOverview()) {
                    if (theSelectedPath.equals(mBundlePath)) {
                        SecondaryChooserFragment.this.dismiss();

                        //delay until close animation ends
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dissmissDialog(FLAG_DISSMISS_INIT_DIALOG);
                            }
                        }, 200);
                    } else {
                        theSelectedPath = theSelectedPath.substring(0, slashIndex);
                        StorageChooser.LAST_SESSION_PATH = theSelectedPath;
                        populateList("");
                    }
                } else {
                    dissmissDialog(FLAG_DISSMISS_NORMAL);
                }
            }
        } else {
            // let's just say that there is no / in the path at any given point
            // which is hard to imagine but.. what to do at that time ?
            // we set it to bundle path until the issue is totally investigated
            // TODO - dig deep about this condition !
            theSelectedPath = mBundlePath;
            StorageChooser.LAST_SESSION_PATH = theSelectedPath;
            populateList("");
        }

    }

    private void dissmissDialog(int flag) {

        switch (flag) {
            case FLAG_DISSMISS_INIT_DIALOG:
                ChooserDialogFragment c = new ChooserDialogFragment();
                c.show(mConfig.getFragmentManager(), "storagechooser_dialog");
                break;
            case FLAG_DISSMISS_NORMAL:
                StorageChooser.LAST_SESSION_PATH = theSelectedPath;
                this.dismiss();
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainer = container;
        if (getShowsDialog()) {
            // one could return null here, or be nice and call super()
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        return getLayout(inflater, container);
    }

    private View getLayout(LayoutInflater inflater, ViewGroup container) {
        mConfig = StorageChooser.sConfig;
        scheme = mConfig.getScheme();
        mHandler = new Handler();

        // init storage-chooser content [localization]
        if (mConfig.getContent() == null) {
            mContent = new Content();
        } else {
            mContent = mConfig.getContent();
        }

        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.DialogTheme);
        LayoutInflater li = inflater.cloneInContext(contextThemeWrapper);

        mContext = getActivity().getApplicationContext();
        mResourceUtil = new ResourceUtil(mContext);
        mLayout = li.inflate(R.layout.custom_storage_list, container, false);

        initListView(mContext, mLayout, mConfig.isShowMemoryBar());

        initUI();
        initNewFolderView();
        updateUI();

        return mLayout;
    }


    private void initUI() {

        mBackButton = mLayout.findViewById(R.id.back_button);
        mSelectButton = mLayout.findViewById(R.id.select_button);
        mMultipleOnSelectButton = mLayout.findViewById(R.id.multiple_selection_done_fab);

        mCreateButton = mLayout.findViewById(R.id.create_folder_button);

        mNewFolderView = mLayout.findViewById(R.id.new_folder_view);
        mNewFolderView.setBackgroundColor(scheme[Theme.SEC_FOLDER_CREATION_BG_INDEX]);
        mFolderNameEditText = mLayout.findViewById(R.id.et_folder_name);

        mInactiveGradient = mLayout.findViewById(R.id.inactive_gradient);

        (mLayout.findViewById(R.id.secondary_container)).setBackgroundColor(scheme[Theme.SEC_BG_INDEX]);

    }

    private void updateUI() {

        //at start dont show the new folder view unless user clicks on the add/plus button
        mNewFolderView.setVisibility(View.INVISIBLE);
        mInactiveGradient.setVisibility(View.INVISIBLE);


        mFolderNameEditText.setHint(mContent.getTextfieldHintText());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mFolderNameEditText.setHintTextColor(scheme[Theme.SEC_HINT_TINT_INDEX]);
        }


        // set label of buttons [localization]
        mSelectButton.setText(mContent.getSelectLabel());
        mCreateButton.setText(mContent.getCreateLabel());


        // set colors
        mSelectButton.setTextColor(scheme[Theme.SEC_SELECT_LABEL_INDEX]);
        mPathChosen.setTextColor(scheme[Theme.SEC_ADDRESS_TINT_INDEX]);

        // set addressbar typeface
        if (mConfig.getHeadingFont() != null) {
            mPathChosen.setTypeface(ChooserDialogFragment.getSCTypeface(mContext,
                    mConfig.getHeadingFont(), mConfig.isHeadingFromAssets()));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNewFolderImageView.setImageTintList(ColorStateList.valueOf(scheme[Theme.SEC_ADDRESS_TINT_INDEX]));
            mBackButton.setImageTintList(ColorStateList.valueOf(scheme[Theme.SEC_ADDRESS_TINT_INDEX]));
        }
        mMultipleOnSelectButton.setBackgroundTintList(ColorStateList.valueOf(scheme[Theme.SEC_DONE_FAB_INDEX]));
        mLayout.findViewById(R.id.custom_path_header).setBackgroundColor(scheme[Theme.SEC_ADDRESS_BAR_BG]);

        // ----

        mBackButton.setOnClickListener(mBackButtonClickListener);
        mSelectButton.setOnClickListener(mSelectButtonClickListener);
        mCreateButton.setOnClickListener(mCreateButtonClickListener);
        mMultipleOnSelectButton.setOnClickListener(mMultipleModeDoneButtonClickListener);

        if (mConfig.getSecondaryAction().equals(StorageChooser.FILE_PICKER)) {
            mSelectButton.setVisibility(View.GONE);
            setBottomNewFolderView();
        }

    }

    private void setBottomNewFolderView() {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mNewFolderView.setLayoutParams(lp);
    }

    private void initNewFolderView() {

        RelativeLayout mNewFolderButtonHolder = mLayout.findViewById(R.id.new_folder_button_holder);

        mNewFolderImageView = mLayout.findViewById(R.id.new_folder_iv);
        mNewFolderImageView.setOnClickListener(mNewFolderButtonClickListener);

        if (!mConfig.isAllowAddFolder()) {
            mNewFolderButtonHolder.setVisibility(View.GONE);
        }

    }


    /**
     * storage listView related code in this block
     */
    private void initListView(Context context, View view, boolean shouldShowMemoryBar) {
        listView = view.findViewById(R.id.storage_list_view);
        mPathChosen = view.findViewById(R.id.path_chosen);
        mFilesProgress = mLayout.findViewById(R.id.files_loader);
        mFilesProgress.setIndeterminate(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mFilesProgress.setIndeterminateTintList(ColorStateList.valueOf(scheme[Theme.OVERVIEW_MEMORYBAR_INDEX]));
        }

        mBundlePath = this.getArguments().getString(DiskUtil.SC_PREFERENCE_KEY);
        isFilePicker = this.getArguments().getBoolean(DiskUtil.SC_CHOOSER_FLAG, false);
        populateList(mBundlePath);
        secondaryChooserAdapter = new SecondaryChooserAdapter(customStoragesList, context, scheme,
                mConfig.getListFont(), mConfig.isListFromAssets());
        secondaryChooserAdapter.setPrefixPath(theSelectedPath);

        listView.setAdapter(secondaryChooserAdapter);
        //listview should be clickable at first
        SecondaryChooserAdapter.shouldEnable = true;
        listView.setOnItemClickListener(mSingleModeClickListener);

        if (isFilePicker && mConfig.isMultiSelect()) {
            listView.setOnItemLongClickListener(mLongClickListener);
        }

    }

    /**
     * handles actions in multiple mode
     * like adding to list and setting background color
     *
     * @param i is position of list clicked
     */
    private void handleListMultipleAction(int i, View view) {
        String jointPath = theSelectedPath + "/" + customStoragesList.get(i);

        // if this list path item is not selected before
        if (!secondaryChooserAdapter.selectedPaths.contains(i)) {
            view.setBackgroundColor(mResourceUtil.getPrimaryColorWithAlpha());

            secondaryChooserAdapter.selectedPaths.add(i);
            mMultipleModeList.add(jointPath);

        } else {
            //this item is selected before
            secondaryChooserAdapter.selectedPaths.remove(secondaryChooserAdapter.selectedPaths.indexOf(i));
            // reset bg to white
            view.setBackgroundColor(scheme[Theme.SEC_BG_INDEX]);
            mMultipleModeList.remove(mMultipleModeList.indexOf(jointPath));
        }

        if (mMultipleOnSelectButton.getVisibility() != View.VISIBLE && MODE_MULTIPLE)
            playTheMultipleButtonAnimation();

        if (listView.getOnItemLongClickListener() != null && MODE_MULTIPLE)
            // long click listener in multiple mode ? haha nice joke
            listView.setOnItemLongClickListener(null);

        if (mMultipleModeList.size() == 0)
            bringBackSingleMode();
    }

//    private int getListIndex(int i) {
//        for(int j=0; j<secondaryChooserAdapter.selectedPaths.size(); j++) {
//            if()
//        }
//    }

    /**
     * brings back the default state of storage-chooser
     */
    private void bringBackSingleMode() {
        // if selected new directory end the multiple mode
        MODE_MULTIPLE = false;
        // set listview to single mode click
        listView.setOnItemClickListener(mSingleModeClickListener);
        // clear both path list and adapter item list
        mMultipleModeList.clear();
        secondaryChooserAdapter.selectedPaths.clear();
        // remove access to done button
        playTheMultipleButtonEndAnimation();
        // aaaaaaaaand bring back long click listener
        listView.setOnItemLongClickListener(mLongClickListener);
    }


    /**
     * evaluates path with respect to the list click position
     *
     * @param i position in list
     */
    private void evaluateAction(int i) {
        String preDefPath = mConfig.getPredefinedPath();
        boolean isCustom = mConfig.isAllowCustomPath();
        if (preDefPath == null) {
            Log.w(TAG, "No predefined path set");
        } else if (isCustom) {
            populateList("/" + customStoragesList.get(i));
        }
    }

    private boolean doesPassMemoryThreshold(long threshold, String memorySuffix, long availableSpace) {
        return true;
    }

    /**
     * populate storageList with necessary storages with filter applied
     *
     * @param path defines the path for which list of folder is requested
     */
    private void populateList(String path) {
        if (customStoragesList == null) {
            customStoragesList = new ArrayList<>();
        } else {
            customStoragesList.clear();
        }

        fileUtil = new FileUtil();
        theSelectedPath = theSelectedPath + path;
        if (secondaryChooserAdapter != null && secondaryChooserAdapter.getPrefixPath() != null) {
            secondaryChooserAdapter.setPrefixPath(theSelectedPath);
        }

        //if the path length is greater than that of the addressbar length
        // we need to clip the starting part so that it fits the length and makes some room
        int pathLength = theSelectedPath.length();
        if (pathLength >= 25) {
            // how many directories did user choose
            int slashCount = getSlashCount(theSelectedPath);
            if (slashCount > 2) {
                mAddressClippedPath = theSelectedPath.substring(theSelectedPath.indexOf("/", theSelectedPath.indexOf("/") + 2), pathLength);
            } else if (slashCount <= 2) {
                mAddressClippedPath = theSelectedPath.substring(theSelectedPath.indexOf("/", theSelectedPath.indexOf("/") + 2), pathLength);
            }
        } else {
            mAddressClippedPath = theSelectedPath;
        }

        File[] volumeList;

        if (isFilePicker) {
            if (mConfig.isCustomFilter()) {
                // we need to start a async task here because the filter loops through all files
                // and if the files are more it'll take ages and it'll cause ANR as this is
                // running on main thread
                new FileFilterTask(mConfig, true).execute();
            } else {
                if (mConfig.getSingleFilter() != null) {
                    new FileFilterTask(mConfig, false).execute();
                } else {
                    volumeList = fileUtil.listFilesInDir(theSelectedPath);
                    setAdapterList(volumeList);
                    refreshList();
                    setBundlePathOnUpdate();
                }
            }
        } else {
            volumeList = fileUtil.listFilesAsDir(theSelectedPath);
            setAdapterList(volumeList);
            refreshList();
            setBundlePathOnUpdate();
        }

        playTheAddressBarAnimation();
    }

    /**
     * setBundlePathOnUpdate sets the mBundlePath on each list element change. Bundle path is used
     * to change the main working directory of chooser.
     */
    public void setBundlePathOnUpdate() {
        if (mConfig.isResumeSession() && StorageChooser.LAST_SESSION_PATH != null) {
            if (StorageChooser.LAST_SESSION_PATH.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                mBundlePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            } else {
                mBundlePath = StorageChooser.LAST_SESSION_PATH.substring(StorageChooser.LAST_SESSION_PATH.indexOf("/", 16), StorageChooser.LAST_SESSION_PATH.length());
            }
        }
    }

    /**
     * Refreshes the list LOL. It checks if adapter is null and notifies change of list on each
     * file list update.
     */
    public void refreshList() {
        if (secondaryChooserAdapter != null) {
            secondaryChooserAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Accepts a file list and sets it for use of storage chooser. Called before all refreshList()
     *
     * @param volumeList File list to be shown in storage chooser
     */
    public void setAdapterList(File[] volumeList) {
        if (volumeList != null) {
            for (File f : volumeList) {
                if (mConfig.isShowHidden()) {
                    customStoragesList.add(f.getName());
                } else {
                    if (!f.getName().startsWith(".")) {
                        customStoragesList.add(f.getName());
                    }
                }
            }

            Collections.sort(customStoragesList, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });
        } else {
            customStoragesList.clear();
        }
    }

    /**
     * Unlike populate list trim populate only updates the list not the addressbar.
     * This is used when creating new folder and update to list is required
     *
     * @param s is the path to be refreshed
     */
    private void trimPopulate(String s) {
        if (customStoragesList == null) {
            customStoragesList = new ArrayList<>();
        } else {
            customStoragesList.clear();
        }
        File[] volumeList;

        if (isFilePicker) {
            volumeList = fileUtil.listFilesInDir(theSelectedPath);
        } else {
            volumeList = fileUtil.listFilesAsDir(theSelectedPath);
        }

        Log.e("SCLib", theSelectedPath);
        if (volumeList != null) {
            for (File f : volumeList) {
                if (!f.getName().startsWith(".")) {
                    customStoragesList.add(f.getName());
                }
            }

            Collections.sort(customStoragesList, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });
        } else {
            customStoragesList.clear();
        }


        if (secondaryChooserAdapter != null) {
            secondaryChooserAdapter.setPrefixPath(s);
            secondaryChooserAdapter.notifyDataSetChanged();
        }
    }

    // ======================= ANIMATIONS =========================
    private void playTheAddressBarAnimation() {
        mPathChosen.setText(mAddressClippedPath);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_address_bar);
        mPathChosen.startAnimation(animation);
    }


    private void playTheMultipleButtonAnimation() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_multiple_button);
        mMultipleOnSelectButton.show();
        mMultipleOnSelectButton.startAnimation(animation);
    }


    private void playTheMultipleButtonEndAnimation() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_multiple_button_end);
        mMultipleOnSelectButton.startAnimation(animation);
        mMultipleOnSelectButton.hide();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = StorageChooser.dialog;
        if(getActivity() != null) {
            d.setContentView(getLayout(LayoutInflater.from(getActivity().getApplicationContext()), mContainer));
        }

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        d.getWindow().setAttributes(lp);
        return d;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        theSelectedPath = "";
        mAddressClippedPath = "";
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        StorageChooser.LAST_SESSION_PATH = theSelectedPath;
        theSelectedPath = "";
        mAddressClippedPath = "";

        StorageChooser.onCancelListener.onCancel();
    }

    private int getSlashCount(String path) {
        int count = 0;

        for (char s : path.toCharArray()) {
            if (s == '/') {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if edit text field is empty or not. Since there is only one edit text here no
     * param is required for now.
     */
    private boolean validateFolderName() {
        if (mFolderNameEditText.getText().toString().trim().isEmpty()) {
            mFolderNameEditText.setError(mContent.getTextfieldErrorText());
            return false;
        }
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    class FileFilterTask extends AsyncTask<Void, String, Boolean> {

        private Config mConfig;
        private boolean isMultiple;
        private File[] fileList;

        FileFilterTask(Config mConfig, boolean isMultiple) {
            this.mConfig = mConfig;
            this.isMultiple = isMultiple;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mFilesProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (isMultiple) {
                UniversalFileFilter universalFileFilter =
                        new UniversalFileFilter(mConfig.isCustomFilter(), mConfig.getCustomEnum());
                fileList = new File(theSelectedPath)
                        .listFiles(universalFileFilter);
            } else {
                fileList = new File(theSelectedPath).listFiles(new UniversalFileFilter(mConfig.getSingleFilter()));
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mFilesProgress.setVisibility(View.INVISIBLE);
            setAdapterList(this.fileList);
            refreshList();
            setBundlePathOnUpdate();
        }
    }
}
