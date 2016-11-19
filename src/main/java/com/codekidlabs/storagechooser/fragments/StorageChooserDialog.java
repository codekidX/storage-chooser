package com.codekidlabs.storagechooser.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;


import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.adapters.StorageChooserListAdapter;
import com.codekidlabs.storagechooser.models.Storages;
import com.codekidlabs.storagechooser.utils.MemoryUtil;

import java.util.ArrayList;
import java.util.List;

public class StorageChooserDialog {

    private static DialogFragment sStorageChooserDialog;

    private static final String INTERNAL_STORAGE = "Internal Storage";
    private static final String EXTERNAL_STORAGE = "External Storage";

    private static List<Storages> storagesList;

    private static Dialog getStorageChooserDialog(Context context, boolean shouldShowMemoryBar) {
        Dialog dialog = new Dialog(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View storageChooserView = layoutInflater.inflate(R.layout.storage_list,null);;
        dialog.setContentView(storageChooserView);
        initListView(context, storageChooserView, shouldShowMemoryBar);
        return dialog;
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

    public static void showDialog(Context mContext, FragmentManager mFragmentManager, boolean mShowMemoryBar, int mMemoryTextColor, String mPath) {
        sStorageChooserDialog = new DialogFragment();

        sStorageChooserDialog.setupDialog(getStorageChooserDialog(mContext, mShowMemoryBar),DialogFragment.STYLE_NO_TITLE);

        sStorageChooserDialog.onDismiss(new DialogInterface() {
            @Override
            public void cancel() {

            }

            @Override
            public void dismiss() {

            }
        });

        sStorageChooserDialog.show(mFragmentManager, "storagechooser_dialog");



    }


    public class Builder {

        private Context mContext;
        private FragmentManager mFragmentManager;
        private boolean mShowMemoryBar;
        private int mMemoryTextColor = 0;
        private String mPath;

        public Builder() {
        }

        public Builder withContext(Context context) {
            mContext = context;
            return this;
        }

        public Builder withFragmentManager(FragmentManager fragmentManager) {
            mFragmentManager = fragmentManager;
            return this;
        }

        public Builder withMemoryBar(boolean memoryBarBoolean) {
            mShowMemoryBar = memoryBarBoolean;
            return this;
        }

        public Builder withPrimaryColor(int color) {
            if(color == 0) {
                mMemoryTextColor = Color.BLACK;
            } else {
                mMemoryTextColor = color;
            }
            return this;
        }

        public Builder withPredefinedPath(String path) {
            mPath = path;
            return this;
        }


        public void show() {
            StorageChooserDialog.showDialog(mContext, mFragmentManager, mShowMemoryBar, mMemoryTextColor, mPath);
        }
    }


}
