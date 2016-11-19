package com.codekidlabs.storagechooser.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.StorageChooserDialog;
import com.codekidlabs.storagechooser.adapters.StorageChooserListAdapter;
import com.codekidlabs.storagechooser.models.Storages;
import com.codekidlabs.storagechooser.utils.MemoryUtil;

import java.util.ArrayList;
import java.util.List;


public class ChooserDialogFragment extends DialogFragment {

    private View mLayout;
    private ViewGroup mContainer;

    private static final String INTERNAL_STORAGE = "Internal Storage";
    private static final String EXTERNAL_STORAGE = "External Storage";

    private static List<Storages> storagesList;

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
        initListView(getContext(), mLayout, StorageChooserDialog.isShowMemoryBar());
        return mLayout;
    }

    private static void initListView(Context context, View view, boolean shouldShowMemoryBar) {
        ListView listView = (ListView) view.findViewById(R.id.storage_list_view);
        populateList();

        listView.setAdapter(new StorageChooserListAdapter(storagesList, context, shouldShowMemoryBar));

    }

    private static void populateList() {
        storagesList = new ArrayList<Storages>();

        if(MemoryUtil.isExternalStorageAvailable()) {
            Storages storageInternal = new Storages();
            storageInternal.setStorageTitle(INTERNAL_STORAGE);
            storageInternal.setMemoryAvailableSize(MemoryUtil.getAvailableInternalMemorySize());
            storageInternal.setMemoryTotalSize(MemoryUtil.getTotalInternalMemorySize());

            Storages storageExternal = new Storages();
            storageInternal.setStorageTitle(EXTERNAL_STORAGE);
            storageInternal.setMemoryAvailableSize(MemoryUtil.getAvailableExternalMemorySize());
            storageInternal.setMemoryTotalSize(MemoryUtil.getTotalExternalMemorySize());

            storagesList.add(storageInternal);
            storagesList.add(storageExternal);
        } else {
            Storages storageInternal = new Storages();
            storageInternal.setStorageTitle(INTERNAL_STORAGE);
            storageInternal.setMemoryAvailableSize(MemoryUtil.getAvailableInternalMemorySize());
            storageInternal.setMemoryTotalSize(MemoryUtil.getTotalInternalMemorySize());

            storagesList.add(storageInternal);
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = StorageChooserDialog.dialog;
        d.setContentView(getLayout(LayoutInflater.from(getContext()), mContainer));
        return d;
    }
}
