package com.codekidlabs.storagechooser.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
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

import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.StorageChooserView;
import com.codekidlabs.storagechooser.adapters.StorageChooserListAdapter;
import com.codekidlabs.storagechooser.models.Config;
import com.codekidlabs.storagechooser.models.Storages;
import com.codekidlabs.storagechooser.utils.DiskUtil;
import com.codekidlabs.storagechooser.utils.FileUtil;
import com.codekidlabs.storagechooser.utils.MemoryUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class ChooserDialogFragment extends DialogFragment {

    private View mLayout;
    private ViewGroup mContainer;

    private TextView mPathChosen;

    private static final String INTERNAL_STORAGE_TITLE = "Internal Storage";
    private static final String EXTERNAL_STORAGE_TITLE = "External Storage";

    private static final String EXTERNAL_STORAGE_PATH_KITKAT = "/storage/extSdCard";

    private List<Storages> storagesList;
    private List<String> customStoragesList;
    private String TAG = "StorageChooser";
    private MemoryUtil memoryUtil = new MemoryUtil();
    private FileUtil fileUtil = new FileUtil();

    private Config mConfig;

    // day night flag
    private int mChooserMode;


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
        mLayout = inflater.inflate(R.layout.storage_list, container, false);
        initListView(getContext(), mLayout, mConfig.isShowMemoryBar());

        if(StorageChooserView.CHOOSER_HEADING !=null) {
            TextView dialogTitle = (TextView) mLayout.findViewById(R.id.dialog_title);
            dialogTitle.setText(StorageChooserView.CHOOSER_HEADING);
        }

        return mLayout;
    }

    /**
     * storage listView related code in this block
     */
    private void initListView(Context context, View view, boolean shouldShowMemoryBar) {
        ListView listView = (ListView) view.findViewById(R.id.storage_list_view);
        populateList();

        listView.setAdapter(new StorageChooserListAdapter(storagesList, context, shouldShowMemoryBar));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(mConfig.isAllowCustomPath()) {
                    String dirPath = evaluatePath(i);
                    showSecondaryChooser(dirPath);
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
                        Log.d("StorageChooser", "Chosen path: " + dirPath);
                    }
                }
                ChooserDialogFragment.this.dismiss();
            }
        });

    }

    private void showSecondaryChooser(String dirPath) {

        Bundle bundle = new Bundle();
        bundle.putString(DiskUtil.SC_PREFERENCE_KEY, dirPath);

        switch (mConfig.getSecondaryAction()) {
            case StorageChooser.NONE:
                break;
            case StorageChooser.DIRECTORY_CHOOSER:
                CustomChooserFragment c = new CustomChooserFragment();
                c.setArguments(bundle);
                c.show(mConfig.getFragmentManager(), "custom_chooser");
                break;
            case StorageChooser.FILE_PICKER:
                FilePickerFragment f = new FilePickerFragment();
                f.setArguments(bundle);
                f.show(mConfig.getFragmentManager(), "file_picker");
                break;
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

    private boolean doesPassMemoryThreshold(long threshold, String memorySuffix, long availableSpace) {
        return true;
    }

    /**
     * populate storageList with necessary storages with filter applied
     */
    private void populateList() {
        storagesList = new ArrayList<Storages>();

        File storageDir = new File("/storage");
        String internalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File[] volumeList = storageDir.listFiles();
        fileUtil.removeNonOperational(Arrays.asList(volumeList));

        Storages storages = new Storages();

        // just add the internal storage and avoid adding emulated henceforth
        if(StorageChooserView.INTERNAL_STORAGE_TEXT !=null) {
            storages.setStorageTitle(StorageChooserView.INTERNAL_STORAGE_TEXT);
        } else {
            storages.setStorageTitle(INTERNAL_STORAGE_TITLE);
        }
        storages.setStoragePath(internalStoragePath);
        storages.setMemoryTotalSize(memoryUtil.formatSize(memoryUtil.getTotalMemorySize(internalStoragePath)));
        storages.setMemoryAvailableSize(memoryUtil.formatSize(memoryUtil.getAvailableMemorySize(internalStoragePath)));
        storagesList.add(storages);


        for(File f: volumeList) {
            Storages sharedStorage = new Storages();
            String fPath = f.getAbsolutePath();
            sharedStorage.setStorageTitle(f.getName());
            sharedStorage.setMemoryTotalSize(memoryUtil.formatSize(memoryUtil.getTotalMemorySize(fPath)));
            sharedStorage.setMemoryAvailableSize(memoryUtil.formatSize(memoryUtil.getAvailableMemorySize(fPath)));
            sharedStorage.setStoragePath(fPath);
            storagesList.add(sharedStorage);
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = StorageChooser.dialog;
        d.setContentView(getLayout(LayoutInflater.from(getContext()), mContainer));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        d.getWindow().setAttributes(lp);
        return d;
    }
}
