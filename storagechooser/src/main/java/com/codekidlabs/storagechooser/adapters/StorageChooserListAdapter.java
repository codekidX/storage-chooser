package com.codekidlabs.storagechooser.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.StorageChooserView;
import com.codekidlabs.storagechooser.animators.MemorybarAnimation;
import com.codekidlabs.storagechooser.models.Storages;
import com.codekidlabs.storagechooser.utils.MemoryUtil;

import java.io.File;
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
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rootView = inflater.inflate(R.layout.row_storage, viewGroup, false);

        //for animation set current position to provide animation delay


        TextView storageName = (TextView) rootView.findViewById(R.id.storage_name);
        TextView memoryStatus = (TextView) rootView.findViewById(R.id.memory_status);
        memoryBar = (ProgressBar) rootView.findViewById(R.id.memory_bar);

        Storages storages = storagesList.get(i);
        final SpannableStringBuilder str = new SpannableStringBuilder(storages.getStorageTitle() + " (" + storages.getMemoryTotalSize() + ")");

        str.setSpan(new StyleSpan(Typeface.ITALIC), getSpannableIndex(str), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        String availableText = mContext.getString(R.string.text_freespace, storages.getMemoryAvailableSize());
        storageName.setText(str);
        memoryStatus.setText(availableText);

        if(StorageChooser.sConfig.getMode() == StorageChooser.NIGHT_MODE) {
            if(StorageChooserView.nightColors != null) {
                memoryStatus.setTextColor(ContextCompat.getColor(mContext, StorageChooserView.nightColors[0]));
                DrawableCompat.setTint(memoryBar.getProgressDrawable(), StorageChooserView.nightColors[1]);

            } else {
                Log.i("StorageChooser", "Storage Chooser view colors not set. Set it using StorageChooserView.setNightColors(colors[]);");
            }
        }

        memoryPercentile = getPercentile(storages.getStoragePath());
        // THE ONE AND ONLY MEMORY BAR
        if(shouldShowMemoryBar) {
            memoryBar.setMax(100);
            memoryBar.setProgress(memoryPercentile);
        } else {
            memoryBar.setVisibility(View.GONE);
        }

        runMemorybarAnimation(i);
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
    private int getPercentile(String path) {
        MemoryUtil memoryUtil = new MemoryUtil();
        int percent = (int) ((memoryUtil.getAvailableMemorySize(path) * 100) / memoryUtil.getTotalMemorySize(path));
        Log.d("TAG", "percentage: " + percent);
        return 100 - percent;
    }

    /**
     * remove KiB,MiB,GiB text that we got from MemoryUtil.getAvailableMemorySize() &
     * MemoryUtil.getTotalMemorySize()
     * @param size String in the format of user readable string, with MB, GiB .. suffix
     * @return integer value of the percentage with amount of storage used
     */
    private long getMemoryFromString(String size) {
        long mem = 0;

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
