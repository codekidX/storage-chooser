package com.codekidlabs.storagechooser.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.models.Storages;

import java.util.List;

public class StorageChooserListAdapter extends BaseAdapter {

    private List<Storages> storagesList;
    private Context mContext;
    private boolean shouldShowMemoryBar;


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

        TextView storageName = (TextView) rootView.findViewById(R.id.storage_name);
        TextView memoryStatus = (TextView) rootView.findViewById(R.id.memory_status);
        ProgressBar memoryBar = (ProgressBar) rootView.findViewById(R.id.memory_bar);

        Storages storages = storagesList.get(i);

        storageName.setText(storages.getStorageTitle());
        memoryStatus.setText(storages.getMemoryAvailableSize() + "/" + storages.getMemoryTotalSize() + " free");

        // THE ONE AND ONLY MEMORY BAR
        if(shouldShowMemoryBar) {
            memoryBar.setMax(100);
            memoryBar.setProgress(getPercentile(storages.getMemoryAvailableSize(), storages.getMemoryTotalSize()));
        } else {
            memoryBar.setVisibility(View.GONE);
        }


        return rootView;

    }

    private int getPercentile(String memoryAvailableSize, String memoryTotalSize) {
        int percent = (getMemoryFromString(memoryAvailableSize) * 100) / getMemoryFromString(memoryTotalSize);
        Log.d("TAG", "percentage: " + percent);
        return percent;
    }

    private int getMemoryFromString(String size) {
        int mem = Integer.parseInt(size.replace(",","").replace("MB",""));
        Log.d("TAG", "Memory:"+ mem);
        return mem;
    }
}
