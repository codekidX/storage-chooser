package com.codekidlabs.storagechooser.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.models.Storages;

import java.util.List;

public class StorageChooserListAdapter extends BaseAdapter {

    List<Storages> storagesList;
    Context mContext;
    boolean shouldShowMemoryBar;


    private TextView storageName;
    private TextView memoryStatus;

    public StorageChooserListAdapter(List<Storages> storagesList, Context mContext, boolean shouldShowMemoryBar) {
        this.storagesList = storagesList;
        this.mContext = mContext;
        this.shouldShowMemoryBar = shouldShowMemoryBar;
    }

    @Override
    public int getCount() {
        return storagesList.size();
    }

    @Override
    public Object getItem(int i) {
        return storagesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rootView = inflater.inflate(R.layout.row_storage, viewGroup, false);

        storageName = (TextView) rootView.findViewById(R.id.storage_name);
        memoryStatus = (TextView) rootView.findViewById(R.id.memory_status);

        Storages storages = storagesList.get(i);

        storageName.setText(storages.getStorageTitle());
        memoryStatus.setText(storages.getMemoryAvailableSize() + "/" + storages.getMemoryTotalSize() + " free");


        return rootView;

    }
}
