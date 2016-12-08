package com.codekidlabs.storagechooser.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.StorageChooserBuilder;
import com.codekidlabs.storagechooser.adapters.StorageChooserListAdapter;
import com.codekidlabs.storagechooser.models.Storages;
import com.codekidlabs.storagechooser.utils.DiskUtil;
import com.codekidlabs.storagechooser.utils.MemoryUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class ChooserDialogFragment extends DialogFragment {

    private View mLayout;
    private ViewGroup mContainer;

    private static final String INTERNAL_STORAGE_TITLE = "Internal Storage";
    private static final String EXTERNAL_STORAGE_TITLE = "External Storage";

    private static final String EXTERNAL_STORAGE_PATH_KITKAT = "/storage/extSdCard";

    private static List<Storages> storagesList;

    private static ChooserDialogFragment sChooserDialogFragment;

    private static String mPath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sChooserDialogFragment = this;
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
        mLayout = inflater.inflate(R.layout.storage_list, container, false);
        initListView(getContext(), mLayout, StorageChooserBuilder.isShowMemoryBar());
        return mLayout;
    }

    private static void initListView(Context context, View view, boolean shouldShowMemoryBar) {
        ListView listView = (ListView) view.findViewById(R.id.storage_list_view);
        populateList();

        listView.setAdapter(new StorageChooserListAdapter(storagesList, context, shouldShowMemoryBar));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });

    }

    private static void populateList() {
        storagesList = new ArrayList<Storages>();

        File storageDir = new File("/storage");
        File internalStorageDir = Environment.getExternalStorageDirectory();

        File[] volumeList = storageDir.listFiles();

            Storages storages = new Storages();

            // just add the internal storage and avoid adding emulated henceforth
            storages.setStorageTitle(INTERNAL_STORAGE_TITLE);
            storages.setMemoryTotalSize(MemoryUtil.getTotalMemorySize(internalStorageDir));
            storages.setMemoryAvailableSize(MemoryUtil.getAvailableMemorySize(internalStorageDir));
            storagesList.add(storages);


            for(File f: volumeList) {

                if(!f.getName().equals(MemoryUtil.SELF_DIR_NAME) && !f.getName().equals(MemoryUtil.EMULATED_DIR_NAME)) {
                    Storages sharedStorage = new Storages();
                    sharedStorage.setStorageTitle(f.getName());
                    sharedStorage.setMemoryTotalSize(MemoryUtil.getTotalMemorySize(f));
                    sharedStorage.setMemoryAvailableSize(MemoryUtil.getAvailableMemorySize(f));
                    storagesList.add(sharedStorage);
                }
            }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = StorageChooserBuilder.dialog;
        d.setContentView(getLayout(LayoutInflater.from(getContext()), mContainer));
        return d;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        StorageChooserBuilder.STORAGE_STATIC_PATH = mPath;
        if(StorageChooserBuilder.getUserSharedPreference() != null) {
            DiskUtil.saveChooserPathPreference(StorageChooserBuilder.getUserSharedPreference(), StorageChooserBuilder.getUserSharedPreferenceKey());
        }
        super.onDismiss(dialog);
    }
}
