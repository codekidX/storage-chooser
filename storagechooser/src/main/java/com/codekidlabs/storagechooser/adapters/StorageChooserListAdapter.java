package com.codekidlabs.storagechooser.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
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
        final SpannableStringBuilder str = new SpannableStringBuilder(storages.getStorageTitle() + " (" + storages.getMemoryTotalSize() + ")");

        str.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), getSpannableIndex(str), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        String availableText = storages.getMemoryAvailableSize() + " free";

        storageName.setText(str);
        memoryStatus.setText(availableText);

        // THE ONE AND ONLY MEMORY BAR
        if(shouldShowMemoryBar) {
            memoryBar.setMax(100);
            memoryBar.setProgress(getPercentile(storages.getMemoryAvailableSize(), storages.getMemoryTotalSize()));
        } else {
            memoryBar.setVisibility(View.GONE);
        }


        return rootView;

    }

    private int getSpannableIndex(SpannableStringBuilder str) {
            return str.toString().indexOf("(") + 1;
    }

    private int getPercentile(String memoryAvailableSize, String memoryTotalSize) {
        int percent = (getMemoryFromString(memoryAvailableSize) * 100) / getMemoryFromString(memoryTotalSize);
        Log.d("TAG", "percentage: " + percent);
        return 100 - percent;
    }

    private int getMemoryFromString(String size) {
        int mem = 0;

        if(size.contains("MB")) {
            mem = Integer.parseInt(size.replace(",","").replace("MB",""));
        } else if (size.contains("GiB")){
            mem = Integer.parseInt(size.replace(",","").replace("GiB",""));
        }


        Log.d("TAG", "Memory:"+ mem);
        return mem;
    }
}
