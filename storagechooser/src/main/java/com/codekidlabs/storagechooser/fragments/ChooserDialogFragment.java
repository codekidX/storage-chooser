package com.codekidlabs.storagechooser.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.Content;
import com.codekidlabs.storagechooser.adapters.StorageChooserListAdapter;
import com.codekidlabs.storagechooser.models.Config;
import com.codekidlabs.storagechooser.models.Storages;
import com.codekidlabs.storagechooser.utils.DiskUtil;
import com.codekidlabs.storagechooser.utils.FileUtil;
import com.codekidlabs.storagechooser.utils.MemoryUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class ChooserDialogFragment extends android.app.DialogFragment {

    private View mLayout;
    private ViewGroup mContainer;

    private TextView mPathChosen;

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
        mHandler = new Handler();
        // init storage-chooser content [localization]
        if(mConfig.getContent() == null) {
            mContent = new Content();
        } else {
            mContent = mConfig.getContent();
        }
        mLayout = inflater.inflate(R.layout.storage_list, container, false);
        initListView(getActivity().getApplicationContext(), mLayout, mConfig.isShowMemoryBar());

        if(mContent.getOverviewHeading() !=null) {
            TextView dialogTitle = (TextView) mLayout.findViewById(R.id.dialog_title);
            dialogTitle.setText(mContent.getOverviewHeading());
        }

        return mLayout;
    }

    /**
     * storage listView related code in this block
     */
    private void initListView(Context context, View view, boolean shouldShowMemoryBar) {
        ListView listView = (ListView) view.findViewById(R.id.storage_list_view);

        // we need to populate before to get the internal storage path in list
        populateList();

        listView.setAdapter(new StorageChooserListAdapter(storagesList, context, shouldShowMemoryBar));


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // if allowCustomPath is called then directory chooser will be the default secondary dialog
                if(mConfig.isAllowCustomPath()) {
                    // if developer wants to apply threshold
                    if(mConfig.isApplyThreshold()) {
                        startThresholdTest(i);
                    } else {
                        final String dirPath = evaluatePath(i);

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                DiskUtil.showSecondaryChooser(dirPath, mConfig);
                            }
                        }, 250);
                    }
                } else {
                    String dirPath = evaluatePath(i);
                    if(mConfig.isActionSave()) {
                        String preDef = mConfig.getPredefinedPath();

                        if(preDef != null) {
                            // if dev forgot or did not add '/' at start add it to avoid errors
                            if(!preDef.startsWith("/")) {
                                preDef = "/" + preDef;
                            }
                            dirPath = dirPath + preDef;
                            DiskUtil.saveChooserPathPreference(mConfig.getPreference(), dirPath);
                        } else {
                            Log.w(TAG, "Predefined path is null set it by .withPredefinedPath() to builder. Saving root directory");
                            DiskUtil.saveChooserPathPreference(mConfig.getPreference(), dirPath);
                        }
                    } else {
                        //Log.d("StorageChooser", "Chosen path: " + dirPath);
                        if(mConfig.isApplyThreshold()) {
                            startThresholdTest(i);
                        } else {
                            if (StorageChooser.onSelectListener != null) {
                                StorageChooser.onSelectListener.onSelect(dirPath);
                            }
                        }
                    }
                }
                ChooserDialogFragment.this.dismiss();
            }
        });

    }

    /**
     * initiate to take threshold test
     * @param position list click index
     */

    private void startThresholdTest(int position) {
        String thresholdSuffix= mConfig.getThresholdSuffix();

        // if threshold suffix is null then memory threshold is also null
        if(thresholdSuffix != null) {
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
     * @param i position in list
     * @return String with the required path for developers
     */
    private String evaluatePath(int i) {
        if(i == 0) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return "/storage/" + storagesList.get(i).getStorageTitle();
        }
    }

    /**
     * checks if available space in user's device is greater than the developer defined threshold
     *
     * @param threshold defined by the developer using Config.withThreshold()
     * @param memorySuffix also defined in Config.withThreshold() - check in GB, MB, KB ?
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


        for(File f: volumeList) {
            if(!f.getName().equals(MemoryUtil.SELF_DIR_NAME)
                    && !f.getName().equals(MemoryUtil.EMULATED_DIR_KNOX)
                    && !f.getName().equals(MemoryUtil.EMULATED_DIR_NAME)
                    && !f.getName().equals(MemoryUtil.SDCARD0_DIR_NAME)) {
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
