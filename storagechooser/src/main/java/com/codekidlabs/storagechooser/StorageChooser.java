package com.codekidlabs.storagechooser;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.codekidlabs.storagechooser.fragments.ChooserDialogFragment;
import com.codekidlabs.storagechooser.models.Config;
import com.codekidlabs.storagechooser.utils.DiskUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;


public class StorageChooser {

    public static final String NONE = "none";
    public static final String DIRECTORY_CHOOSER = "dir";
    public static final String FILE_PICKER = "file";
    public static Dialog dialog;
    public static Config sConfig;
    public static OnSelectListener onSelectListener;
    public static OnCancelListener onCancelListener;
    public static OnMultipleSelectListener onMultipleSelectListener;
    public static String LAST_SESSION_PATH = null;
    private final String TAG = this.getClass().getName();
    private Activity chooserActivity;

    /**
     * basic constructor of StorageChooser
     *
     * @param config to use with dialog window addition
     */
    StorageChooser(Activity activity, Config config) {
        setsConfig(config);
        setChooserActivity(activity);
    }

    /**
     * blank constructor of StorageChooser
     * no params used only for internal library use
     */
    public StorageChooser() {
    }

    public static Config getsConfig() {
        return sConfig;
    }

    public static void setsConfig(Config sConfig) {
        StorageChooser.sConfig = sConfig;
    }

    private static Dialog getStorageChooserDialog(Activity activity) {
        return new Dialog(activity, R.style.DialogTheme);
    }

    /**
     * show() shows the storage chooser dialog
     */
    public void show() {
        init();
    }

    /**
     * init() creates the storage chooser dialog
     */
    private void init() {
        dialog = getStorageChooserDialog(getChooserActivity());

        // check if listeners are set to avoid crash
        if (onSelectListener == null) {
            onSelectListener = getDefaultOnSelectListener();
        }
        if (onCancelListener == null) {
            onCancelListener = getDefaultOnCancelListener();
        }
        if (onMultipleSelectListener == null) {
            onMultipleSelectListener = getDefaultMultipleSelectionListener();
        }

        if (sConfig.isResumeSession() && StorageChooser.LAST_SESSION_PATH != null) {
            DiskUtil.showSecondaryChooser(StorageChooser.LAST_SESSION_PATH, sConfig);
        } else {

            // if dev needs to skip overview and the primary path is not mentioned the directory
            // chooser or file picker will default to internal storage
            if (sConfig.isSkipOverview()) {
                if (sConfig.getPrimaryPath() == null) {

                    // internal storage is always the first element (I took care of it :wink:)
                    String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    DiskUtil.showSecondaryChooser(dirPath, sConfig);
                } else {

                    // path provided by dev is the goto path for chooser
                    DiskUtil.showSecondaryChooser(sConfig.getPrimaryPath(), sConfig);
                }

            } else {
                ChooserDialogFragment chooserDialogFragment = new ChooserDialogFragment();
                chooserDialogFragment.show(sConfig.getFragmentManager(), "storagechooser_dialog");
            }
        }
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        StorageChooser.onSelectListener = onSelectListener;
    }


    public void setOnCancelListener(OnCancelListener onCancelListener) {
        StorageChooser.onCancelListener = onCancelListener;
    }

    public void setOnMultipleSelectListener(OnMultipleSelectListener onMultipleSelectListener) {
        StorageChooser.onMultipleSelectListener = onMultipleSelectListener;
    }

    private Activity getChooserActivity() {
        return chooserActivity;
    }

    private void setChooserActivity(Activity chooserActivity) {
        this.chooserActivity = chooserActivity;
    }

    private OnSelectListener getDefaultOnSelectListener() {
        return new OnSelectListener() {
            @Override
            public void onSelect(String path) {
                Log.e(TAG, "You need to setup OnSelectListener from your side. OUTPUT: " + path);
            }
        };
    }

    private OnCancelListener getDefaultOnCancelListener() {
        return new OnCancelListener() {
            @Override
            public void onCancel() {
                Log.e(TAG, "You need to setup OnCancelListener from your side. This is default OnCancelListener fired.");
            }
        };
    }

    private OnMultipleSelectListener getDefaultMultipleSelectionListener() {
        return new OnMultipleSelectListener() {
            @Override
            public void onDone(ArrayList<String> selectedFilePaths) {
                Log.e(TAG, "You need to setup OnMultipleSelectListener from your side. This is default OnMultipleSelectListener fired.");
            }
        };
    }

    public enum FileType {
        VIDEO, AUDIO, DOCS, IMAGES, ARCHIVE
    }

    public interface OnSelectListener {

        void onSelect(String path);
    }

    public interface OnCancelListener {

        void onCancel();
    }

    public interface OnMultipleSelectListener {
        void onDone(ArrayList<String> selectedFilePaths);
    }

    /**
     * @class Builder
     * - as the name suggests it gets all the configurations provided by the developer and
     * passes them to the StorageChooser class using the constructor.
     * <p>
     * NOTE: The dialog is still not yet show even though the builder instance is present.
     * show() is called seperately on the builder because it does not return a builder but
     * triggers init() inside the StorageChooser class.
     */
    public static class Builder {

        Config devConfig;
        private Activity mActivity;
        private boolean mActionSave = false;
        private boolean mShowMemoryBar = false;
        private boolean mHideFreeSpaceLabel = false;
        private boolean mAllowCustomPath = false;
        private boolean mAllowAddFolder = false;
        private boolean mShowHidden = false;
        private boolean mSkipOverview = false;
        private boolean mApplyMemoryThreshold = false;
        private boolean mShowInGrid = false;
        private boolean mResumeSession = false;
        private boolean mHeadingFromAssets = false;
        private boolean mListFromAssets = false;
        private float mMemorybarHeight = 2f;
        private String type;
        private Content content;
        private StorageChooser.Theme theme;
        private StorageChooser.FileType filter;
        private ArrayList<StorageChooser.FileType> multipleFilter;

        public Builder() {
            devConfig = new Config();
        }

        public Builder withActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public Builder withFragmentManager(android.app.FragmentManager fragmentManager) {
            devConfig.setFragmentManager(fragmentManager);
            return this;
        }

        public Builder withMemoryBar(boolean memoryBarBoolean) {
            mShowMemoryBar = memoryBarBoolean;
            return this;
        }

        public Builder hideFreeSpaceLabel(boolean hideFreeSpaceLabel) {
            mHideFreeSpaceLabel = hideFreeSpaceLabel;
            return this;
        }

        public Builder setMemoryBarHeight(float height) {
            this.mMemorybarHeight = height;
            return this;
        }

        public Builder withPredefinedPath(String path) {
            devConfig.setPredefinedPath(path);
            return this;
        }

        public Builder applyMemoryThreshold(boolean applyThreshold) {
            mApplyMemoryThreshold = applyThreshold;
            return this;
        }

        public Builder withThreshold(int size, String suffix) {
            devConfig.setMemoryThreshold(size);
            devConfig.setThresholdSuffix(suffix);
            return this;
        }

        public Builder withPreference(SharedPreferences sharedPreferences) {
            devConfig.setPreference(sharedPreferences);
            return this;
        }

        public Builder actionSave(boolean save) {
            mActionSave = save;
            return this;
        }

        public Builder setDialogTitle(String title) {
            devConfig.setDialogTitle(title);
            return this;
        }

        public Builder setInternalStorageText(String storageNameText) {
            devConfig.setInternalStorageText(storageNameText);
            return this;
        }

        public Builder allowCustomPath(boolean allowCustomPath) {
            mAllowCustomPath = allowCustomPath;
            return this;
        }

        public Builder allowAddFolder(boolean addFolder) {
            mAllowAddFolder = addFolder;
            return this;
        }

        public Builder showHidden(boolean showHiddenFolders) {
            mShowHidden = showHiddenFolders;
            return this;
        }

        public Builder setType(String action) {
            type = action;
            return this;
        }

        public Builder setTheme(StorageChooser.Theme theme) {
            this.theme = theme;
            return this;
        }

        public Builder skipOverview(boolean skip, String primaryPath) {
            mSkipOverview = skip;
            devConfig.setPrimaryPath(primaryPath);
            return this;
        }

        public Builder skipOverview(boolean skip) {
            mSkipOverview = skip;
            return this;
        }

        public Builder withContent(Content content) {
            this.content = content;
            return this;
        }

        public Builder filter(@Nullable StorageChooser.FileType filter) {
            this.filter = filter;
            return this;
        }

        public Builder crunch() {
            devConfig.setCustomFilter(false);
            return this;
        }

        public Builder customFilter(List<String> formats) {
            devConfig.setCustomFilter(true);
            devConfig.setCustomEnum(formats);
            return this;
        }

        public Builder showFoldersInGrid(boolean showInGrid) {
            devConfig.setGridView(showInGrid);
            return this;
        }

        public Builder shouldResumeSession(boolean resumeSession) {
            this.mResumeSession = resumeSession;
            return this;
        }

        // typefaces
        public Builder setHeadingTypeface(String path, boolean fromAssets) {
            devConfig.setHeadingFont(path);
            mHeadingFromAssets = fromAssets;
            return this;
        }

        public Builder setListTypeface(String path, boolean fromAssets) {
            devConfig.setListFont(path);
            mListFromAssets = fromAssets;
            return this;
        }

        public Builder disableMultiSelect() {
            devConfig.setMultiSelect(false);
            return this;
        }


        public StorageChooser build() {
            devConfig.setActionSave(mActionSave);
            devConfig.setShowMemoryBar(mShowMemoryBar);
            devConfig.setHideFreeSpaceLabel(mHideFreeSpaceLabel);
            devConfig.setAllowCustomPath(mAllowCustomPath);
            devConfig.setAllowAddFolder(mAllowAddFolder);
            devConfig.setShowHidden(mShowHidden);
            devConfig.setSkipOverview(mSkipOverview);
            devConfig.setApplyThreshold(mApplyMemoryThreshold);
            devConfig.setResumeSession(mResumeSession);
            devConfig.setGridView(mShowInGrid);
            devConfig.setContent(content);
            devConfig.setSingleFilter(filter);
            devConfig.setMemorybarHeight(mMemorybarHeight);
            devConfig.setHeadingFromAssets(mHeadingFromAssets);
            devConfig.setListFromAssets(mListFromAssets);

            type = (type == null) ? StorageChooser.NONE : type;
            devConfig.setSecondaryAction(type);

            if (theme == null || theme.getScheme() == null) {
                theme = new Theme(mActivity);
                devConfig.setScheme(theme.getDefaultScheme());
            } else {
                devConfig.setScheme(theme.getScheme());
            }

            return new StorageChooser(mActivity, devConfig);
        }

    }

    public static class Theme {

        // Overview dialog colors
        public static final int OVERVIEW_HEADER_INDEX = 0;
        public static final int OVERVIEW_TEXT_INDEX = 1;
        public static final int OVERVIEW_BG_INDEX = 2;
        public static final int OVERVIEW_STORAGE_TEXT_INDEX = 3;
        public static final int OVERVIEW_INDICATOR_INDEX = 4;
        public static final int OVERVIEW_MEMORYBAR_INDEX = 5;
        // Secondary dialog colors
        public static final int SEC_FOLDER_TINT_INDEX = 6;
        public static final int SEC_BG_INDEX = 7;
        public static final int SEC_TEXT_INDEX = 8;
        public static final int SEC_ADDRESS_TINT_INDEX = 9;
        public static final int SEC_HINT_TINT_INDEX = 10;
        public static final int SEC_SELECT_LABEL_INDEX = 11;
        public static final int SEC_FOLDER_CREATION_BG_INDEX = 12;
        public static final int SEC_DONE_FAB_INDEX = 13;
        public static final int SEC_ADDRESS_BAR_BG = 14;
        Context context;
        int[] scheme;

        public Theme(Context context) {
            this.context = context;
        }

        public int[] getDefaultScheme() {
            return context.getResources().getIntArray(R.array.default_light);
        }

        public int[] getDefaultDarkScheme() {
            return context.getResources().getIntArray(R.array.default_dark);
        }


        public int[] getScheme() {
            return scheme;
        }

        public void setScheme(int[] scheme) {
            this.scheme = scheme;
        }
    }
}
