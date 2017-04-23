package com.codekidlabs.storagechooser.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.animators.MemorybarAnimation;
import com.codekidlabs.storagechooser.exceptions.MemoryNotAccessibleException;
import com.codekidlabs.storagechooser.models.Storages;
import com.codekidlabs.storagechooser.utils.MemoryUtil;

import java.util.List;

public class StorageChooserListAdapter extends BaseAdapter {

    private List<Storages> storagesList;
    private Context mContext;
    private boolean shouldShowMemoryBar;

    private ProgressBar memoryBar;
    private static int memoryPercentile;


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
        memoryPercentile = -1;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.row_storage, viewGroup, false);

        //for animation set current position to provide animation delay
        TextView storageName = (TextView) rootView.findViewById(R.id.storage_name);
        TextView memoryStatus = (TextView) rootView.findViewById(R.id.memory_status);
        memoryBar = (ProgressBar) rootView.findViewById(R.id.memory_bar);

        // new scaled memorybar - following the new google play update!
        memoryBar.setScaleY(2f);

        Storages storages = storagesList.get(i);
        final SpannableStringBuilder str = new SpannableStringBuilder(storages.getStorageTitle() + " (" + storages.getMemoryTotalSize() + ")");

        str.setSpan(new StyleSpan(Typeface.ITALIC), getSpannableIndex(str), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        String availableText = mContext.getString(R.string.text_freespace, storages.getMemoryAvailableSize());
        storageName.setText(str);
        memoryStatus.setText(availableText);

        memoryStatus.setTextColor(ContextCompat.getColor(mContext, R.color.memory_status_color));
        DrawableCompat.setTint(memoryBar.getProgressDrawable(), ContextCompat.getColor(mContext, R.color.memory_bar_color));

        try {
            memoryPercentile = getPercentile(storages.getStoragePath());
        } catch (MemoryNotAccessibleException e) {
            e.printStackTrace();
        }
        // THE ONE AND ONLY MEMORY BAR
        if(shouldShowMemoryBar && memoryPercentile != -1) {
            memoryBar.setMax(100);
            memoryBar.setProgress(memoryPercentile);
            runMemorybarAnimation(i);
        } else {
            memoryBar.setVisibility(View.GONE);
        }
        
        return rootView;

    }

    private void runMemorybarAnimation(int pos) {
        MemorybarAnimation animation = new MemorybarAnimation(memoryBar,0, memoryPercentile);
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());

        if(pos > 0) {
            animation.setStartOffset(300);
        }

        memoryBar.startAnimation(animation);
    }

    /**
     * return the spannable index of character '('
     * @param str SpannableStringBuilder to apply typeface changes
     * @return index of '('
     */
    private int getSpannableIndex(SpannableStringBuilder str) {
            return str.toString().indexOf("(") + 1;
    }

    /**
     * calculate percentage of memory left for memorybar
     * @param path use same statfs
     * @return integer value of the percentage with amount of storage used
     */
    private int getPercentile(String path) throws MemoryNotAccessibleException {
        MemoryUtil memoryUtil = new MemoryUtil();
        int percent;

        long availableMem =  memoryUtil.getAvailableMemorySize(path);
        long totalMem =  memoryUtil.getTotalMemorySize(path);

        if(totalMem > 0) {
            percent = (int) (100 - ((availableMem * 100) / totalMem));
        } else {
            throw new MemoryNotAccessibleException("Cannot compute memory for " + path);
        }

        return percent;
    }

    /**
     * remove KiB,MiB,GiB text that we got from MemoryUtil.getAvailableMemorySize() &
     * MemoryUtil.getTotalMemorySize()
     * @param size String in the format of user readable string, with MB, GiB .. suffix
     * @return integer value of the percentage with amount of storage used
     */
    private long getMemoryFromString(String size) {
        long mem;

        if(size.contains("MiB")) {
            mem = Integer.parseInt(size.replace(",","").replace("MiB",""));
        } else if (size.contains("GiB")){
            mem = Integer.parseInt(size.replace(",","").replace("GiB",""));
        } else {
            mem = Integer.parseInt(size.replace(",","").replace("KiB",""));
        }

        Log.d("TAG", "Memory:"+ mem);
        return mem;
    }
}
