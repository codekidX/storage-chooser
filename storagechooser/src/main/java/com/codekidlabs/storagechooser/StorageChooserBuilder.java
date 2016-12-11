package com.codekidlabs.storagechooser;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;

import com.codekidlabs.storagechooser.fragments.ChooserDialogFragment;
import com.codekidlabs.storagechooser.models.Config;
import com.codekidlabs.storagechooser.utils.MemoryUtil;


public class StorageChooserBuilder {

    public static Dialog dialog;
    public static String STORAGE_STATIC_PATH;

    public static Config sConfig;
    private Activity chooserActivity;

    /**
     * basic constructor of StorageChooserBuilder
     * @param config to use with dialog window addition
     */
    public StorageChooserBuilder(Activity activity, Config config) {
        setsConfig(config);
        setChooserActivity(activity);
    }

    /**
     * init creates and shows the storage chooser dialog
     */
    public void init() {
        dialog = getStorageChooserDialog(getChooserActivity());
        ChooserDialogFragment chooserDialogFragment = new ChooserDialogFragment();
        chooserDialogFragment.show(sConfig.getFragmentManager(),"storagechooser_dialog");
    }

    public static Config getsConfig() {
        return sConfig;
    }

    public static void setsConfig(Config sConfig) {
        StorageChooserBuilder.sConfig = sConfig;
    }

    private Activity getChooserActivity() {
        return chooserActivity;
    }

    private void setChooserActivity(Activity chooserActivity) {
        this.chooserActivity = chooserActivity;
    }

    private static Dialog getStorageChooserDialog(Activity activity) {
        return new Dialog(activity);
    }
    /**
     * @class Builder
     *  - as the name suggests it gets all the configurations provided by the developer and
     *    passes them to the StorageChooserBuilder class using the constructor.
     *
     *    NOTE: The dialog is still not yet show even though the builder instance is present.
     *    show() is called seperately on the builder because it does not return a builder but
     *    triggers init() inside the StorageChooserBuilder class.
     */
    public static class Builder {

        private Activity mActivity;
        private boolean mActionSave = false;
        private boolean mShowMemoryBar = false;

        Config devConfig;

        public Builder() {
            devConfig =  new Config();
        }

        public Builder withActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public Builder withFragmentManager(FragmentManager fragmentManager) {
            devConfig.setFragmentManager(fragmentManager);
            return this;
        }

        public Builder withMemoryBar(boolean memoryBarBoolean) {
            mShowMemoryBar = memoryBarBoolean;
            return this;
        }

        public Builder withPredefinedPath(String path) {
            devConfig.setPredefinedPath(path);
            return this;
        }

        public Builder withMemoryThreshold(int size, String suffix) {
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


        public Builder build() {
            return this;
        }

        public void show() {
            devConfig.setActionSave(mActionSave);
            devConfig.setShowMemoryBar(mShowMemoryBar);
            new StorageChooserBuilder(mActivity, devConfig).init();
        }
    }
}
