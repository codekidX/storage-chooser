package com.codekidlabs.storagechooser.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.Content;
import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.adapters.StorageChooserListAdapter;
import com.codekidlabs.storagechooser.models.Config;
import com.codekidlabs.storagechooser.models.Storages;
import com.codekidlabs.storagechooser.utils.DiskUtil;
import com.codekidlabs.storagechooser.utils.FileUtil;
import com.codekidlabs.storagechooser.utils.MemoryUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.codekidlabs.storagechooser.StorageChooser.Theme.OVERVIEW_BG_INDEX;
import static com.codekidlabs.storagechooser.StorageChooser.Theme.OVERVIEW_HEADER_INDEX;
import static com.codekidlabs.storagechooser.StorageChooser.Theme.OVERVIEW_TEXT_INDEX;


public class ChooserDialogFragment extends android.app.DialogFragment {

    private static final boolean BUILD_DEBUG = true;
    private View mLayout;
    private ViewGroup mContainer;

    private List<Storages> storagesList;
    private List<String> customStoragesList;
    private String TAG = "StorageChooser";
    private MemoryUtil memoryUtil = new MemoryUtil();
    private FileUtil fileUtil = new FileUtil();

    private Config mConfig;
    private Content mContent;

    // day night flag
    private int mChooserMode;

    // delaying secondary chooser
    private Handler mHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContainer = container;
        if (getShowsDialog()) {
            // one could return null here, or be nice and call super()
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        return getLayout(inflater, container);
    }

    private View getLayout(LayoutInflater inflater, ViewGroup container) {
        mConfig = StorageChooser.sConfig;
        mHandler = new Handler();
        // init storage-chooser content [localization]
        if (mConfig.getContent() == null) {
            mContent = new Content();
        } else {
            mContent = mConfig.getContent();
        }
        mLayout = inflater.inflate(R.layout.storage_list, container, false);
        initListView(getActivity().getApplicationContext(), mLayout, mConfig.isShowMemoryBar());

        if (mContent.getOverviewHeading() != null) {
            TextView dialogTitle = mLayout.findViewById(R.id.dialog_title);
            dialogTitle.setTextColor(mConfig.getScheme()[OVERVIEW_TEXT_INDEX]);
            dialogTitle.setText(mContent.getOverviewHeading());

            // set heading typeface
            if(mConfig.getHeadingFont() != null) {
                dialogTitle.setTypeface(getSCTypeface(getActivity().getApplicationContext(),
                        mConfig.getHeadingFont(),
                        mConfig.isHeadingFromAssets()));
            }
        }

        mLayout.findViewById(R.id.header_container).setBackgroundColor(
                mConfig.getScheme()[OVERVIEW_HEADER_INDEX]);
        mLayout.findViewById(R.id.overview_container).setBackgroundColor(
                mConfig.getScheme()[OVERVIEW_BG_INDEX]);

        return mLayout;
    }

    /**
     * storage listView related code in this block
     */
    private void initListView(Context context, View view, boolean shouldShowMemoryBar) {
        ListView listView = view.findViewById(R.id.storage_list_view);

        // we need to populate before to get the internal storage path in list
        populateList();

        listView.setAdapter(new StorageChooserListAdapter(storagesList, context,
                shouldShowMemoryBar, mConfig.isHideFreeSpaceLabel(), mConfig.getScheme(),
                mConfig.getMemorybarHeight(), mConfig.getListFont(), mConfig.isListFromAssets(),
                mContent));


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String dirPath = evaluatePath(i);

                if (new File(dirPath).canRead()) {
                    // if allowCustomPath is called then directory chooser will be the default secondary dialog
                    if (mConfig.isAllowCustomPath()) {
                        // if developer wants to apply threshold
                        if (mConfig.isApplyThreshold()) {
                            startThresholdTest(i);
                        } else {

                            if (BUILD_DEBUG) {
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        DiskUtil.showSecondaryChooser(dirPath, mConfig);
                                    }
                                }, 250);
                            } else {
                                DiskUtil.showSecondaryChooser(dirPath, mConfig);
                            }


                        }
                    } else {
                        if (mConfig.isActionSave()) {
                            String preDef = mConfig.getPredefinedPath();
                            // if dev forgot or did not add '/' at start add it to avoid errors
                            String preDirPath = null;

                            if (preDef != null) {
                                if (!preDef.startsWith("/")) {
                                    preDef = "/" + preDef;
                                }
                                preDirPath = dirPath + preDef;
                                DiskUtil.saveChooserPathPreference(mConfig.getPreference(), preDirPath);
                            } else {
                                Log.w(TAG, "Predefined path is null set it by .withPredefinedPath() to builder. Saving root directory");
                                DiskUtil.saveChooserPathPreference(mConfig.getPreference(), preDirPath);
                            }
                        } else {
                            //Log.d("StorageChooser", "Chosen path: " + dirPath);
                            if (mConfig.isApplyThreshold()) {
                                startThresholdTest(i);
                            } else {
                                if (StorageChooser.onSelectListener != null) {
                                    StorageChooser.onSelectListener.onSelect(dirPath);
                                }
                            }
                        }
                    }
                    ChooserDialogFragment.this.dismiss();
                } else {
                    Toast.makeText(getActivity(), R.string.toast_not_readable, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    /**
     * initiate to take threshold test
     *
     * @param position list click index
     */

    private void startThresholdTest(int position) {
        String thresholdSuffix = mConfig.getThresholdSuffix();

        // if threshold suffix is null then memory threshold is also null
        if (thresholdSuffix != null) {
            long availableMem = memoryUtil.getAvailableMemorySize(evaluatePath(position));


            if (doesPassThresholdTest((long) mConfig.getMemoryThreshold(), thresholdSuffix, availableMem)) {
                String dirPath = evaluatePath(position);
                DiskUtil.showSecondaryChooser(dirPath, mConfig);
            } else {
                String suffixedAvailableMem = String.valueOf(memoryUtil.suffixedSize(availableMem, thresholdSuffix)) + " " + thresholdSuffix;
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_threshold_breached, suffixedAvailableMem), Toast.LENGTH_SHORT).show();
            }
        } else {
            // THROW: error in log
            Log.e(TAG, "add .withThreshold(int size, String suffix) to your StorageChooser.Builder instance");
        }
    }


    /**
     * evaluates path with respect to the list click position
     *
     * @param i position in list
     * @return String with the required path for developers
     */
    private String evaluatePath(int i) {
        if (i == 0) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return "/storage/" + storagesList.get(i).getStorageTitle();
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
    private boolean doesPassThresholdTest(long threshold, String memorySuffix, long availableSpace) {
        return memoryUtil.suffixedSize(availableSpace, memorySuffix) > threshold;
    }

    /**
     * populate storageList with necessary storages with filter applied
     */
    private void populateList() {
        storagesList = new ArrayList<>();

        File storageDir = new File("/storage");
        String internalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File[] volumeList = storageDir.listFiles();

        Storages storages = new Storages();

        // just add the internal storage and avoid adding emulated henceforth
        storages.setStorageTitle(mContent.getInternalStorageText());

        storages.setStoragePath(internalStoragePath);
        storages.setMemoryTotalSize(memoryUtil.formatSize(memoryUtil.getTotalMemorySize(internalStoragePath)));
        storages.setMemoryAvailableSize(memoryUtil.formatSize(memoryUtil.getAvailableMemorySize(internalStoragePath)));
        storagesList.add(storages);


        for (File f : volumeList) {
            if (!f.getName().equals(MemoryUtil.SELF_DIR_NAME)
                    && !f.getName().equals(MemoryUtil.EMULATED_DIR_KNOX)
                    && !f.getName().equals(MemoryUtil.EMULATED_DIR_NAME)
                    && !f.getName().equals(MemoryUtil.SDCARD0_DIR_NAME)
                    && !f.getName().equals(MemoryUtil.CONTAINER)) {
                Storages sharedStorage = new Storages();
                String fPath = f.getAbsolutePath();
                sharedStorage.setStorageTitle(f.getName());
                sharedStorage.setMemoryTotalSize(memoryUtil.formatSize(memoryUtil.getTotalMemorySize(fPath)));
                sharedStorage.setMemoryAvailableSize(memoryUtil.formatSize(memoryUtil.getAvailableMemorySize(fPath)));
                sharedStorage.setStoragePath(fPath);
                storagesList.add(sharedStorage);
            }
        }

    }

    // Convinience methods

    public static Typeface getSCTypeface(Context context, String path, boolean assets) {
        if (assets) {
            return Typeface.createFromAsset(context.getAssets(),
                    path);
        }
        return Typeface.createFromFile(path);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        StorageChooser.onCancelListener.onCancel();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = StorageChooser.dialog;
        d.setContentView(getLayout(LayoutInflater.from(getActivity().getApplicationContext()), mContainer));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        d.getWindow().setAttributes(lp);
        return d;
    }
}
