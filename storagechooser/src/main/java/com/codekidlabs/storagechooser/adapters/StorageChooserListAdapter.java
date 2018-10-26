package com.codekidlabs.storagechooser.adapters;

import android.content.Context;
import android.graphics.Typeface;
import androidx.core.graphics.drawable.DrawableCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codekidlabs.storagechooser.Content;
import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.animators.MemorybarAnimation;
import com.codekidlabs.storagechooser.exceptions.MemoryNotAccessibleException;
import com.codekidlabs.storagechooser.fragments.ChooserDialogFragment;
import com.codekidlabs.storagechooser.models.Storages;
import com.codekidlabs.storagechooser.utils.MemoryUtil;

import java.util.List;

import static com.codekidlabs.storagechooser.StorageChooser.Theme.OVERVIEW_INDICATOR_INDEX;
import static com.codekidlabs.storagechooser.StorageChooser.Theme.OVERVIEW_MEMORYBAR_INDEX;
import static com.codekidlabs.storagechooser.StorageChooser.Theme.OVERVIEW_STORAGE_TEXT_INDEX;

public class StorageChooserListAdapter extends BaseAdapter {

    private static int memoryPercentile;
    private List<Storages> storagesList;
    private Context mContext;
    private boolean shouldShowMemoryBar;
    private boolean hideFreeSpaceLabel;
    private ProgressBar memoryBar;
    private int[] scheme;
    private float memorybarHeight;
    private String listTypeface;
    private boolean fromAssets;
    private Content mContent;


    public StorageChooserListAdapter(List<Storages> storagesList, Context mContext,
                                     boolean shouldShowMemoryBar, boolean hideFreeSpaceLabel,
                                     int[] scheme, float memorybarHeight, String listTypeface,
                                     boolean fromAssets, Content content) {
        this.storagesList = storagesList;
        this.mContext = mContext;
        this.shouldShowMemoryBar = shouldShowMemoryBar;
        this.hideFreeSpaceLabel = hideFreeSpaceLabel;
        this.scheme = scheme;
        this.memorybarHeight = memorybarHeight;
        this.listTypeface = listTypeface;
        this.fromAssets = fromAssets;
        this.mContent = content;
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
        TextView storageName = rootView.findViewById(R.id.storage_name);
        TextView memoryStatus = rootView.findViewById(R.id.memory_status);
        memoryBar = rootView.findViewById(R.id.memory_bar);

        // new scaled memorybar - following the new google play update!
        memoryBar.setScaleY(memorybarHeight);

        Storages storages = storagesList.get(i);
        final SpannableStringBuilder str = new SpannableStringBuilder(storages.getStorageTitle() + " (" + storages.getMemoryTotalSize() + ")");

        str.setSpan(new StyleSpan(Typeface.ITALIC), getSpannableIndex(str), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        String availableText = String.format(mContent.getFreeSpaceText(), storages.getMemoryAvailableSize());
        storageName.setText(str);

        storageName.setTextColor(scheme[OVERVIEW_STORAGE_TEXT_INDEX]);
        memoryStatus.setText(availableText);

        if (listTypeface != null) {
            storageName.setTypeface(ChooserDialogFragment.getSCTypeface(mContext, listTypeface,
                    fromAssets));
            memoryStatus.setTypeface(ChooserDialogFragment.getSCTypeface(mContext, listTypeface,
                    fromAssets));
        }

        memoryStatus.setTextColor(scheme[OVERVIEW_INDICATOR_INDEX]);
        DrawableCompat.setTint(memoryBar.getProgressDrawable(), scheme[OVERVIEW_MEMORYBAR_INDEX]);

        try {
            memoryPercentile = getPercentile(storages.getStoragePath());
        } catch (MemoryNotAccessibleException e) {
            e.printStackTrace();
        }
        // THE ONE AND ONLY MEMORY BAR
        if (shouldShowMemoryBar && memoryPercentile != -1) {
            memoryBar.setMax(100);
            memoryBar.setProgress(memoryPercentile);
            runMemorybarAnimation(i);
        } else {
            memoryBar.setVisibility(View.GONE);
        }

        if (hideFreeSpaceLabel) {
            memoryStatus.setVisibility(View.GONE);
            storageName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }

        return rootView;

    }

    private void runMemorybarAnimation(int pos) {
        MemorybarAnimation animation = new MemorybarAnimation(memoryBar, 0, memoryPercentile);
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());

        if (pos > 0) {
            animation.setStartOffset(300);
        }

        memoryBar.startAnimation(animation);
    }

    /**
     * return the spannable index of character '('
     *
     * @param str SpannableStringBuilder to apply typeface changes
     * @return index of '('
     */
    private int getSpannableIndex(SpannableStringBuilder str) {
        return str.toString().indexOf("(") + 1;
    }

    /**
     * calculate percentage of memory left for memorybar
     *
     * @param path use same statfs
     * @return integer value of the percentage with amount of storage used
     */
    private int getPercentile(String path) throws MemoryNotAccessibleException {
        MemoryUtil memoryUtil = new MemoryUtil();
        int percent;

        long availableMem = memoryUtil.getAvailableMemorySize(path);
        long totalMem = memoryUtil.getTotalMemorySize(path);

        if (totalMem > 0) {
            percent = (int) (100 - ((availableMem * 100) / totalMem));
        } else {
            throw new MemoryNotAccessibleException("Cannot compute memory for " + path);
        }

        return percent;
    }

    /**
     * remove KiB,MiB,GiB text that we got from MemoryUtil.getAvailableMemorySize() &
     * MemoryUtil.getTotalMemorySize()
     *
     * @param size String in the format of user readable string, with MB, GiB .. suffix
     * @return integer value of the percentage with amount of storage used
     */
    private long getMemoryFromString(String size) {
        long mem;

        if (size.contains("MiB")) {
            mem = Integer.parseInt(size.replace(",", "").replace("MiB", ""));
        } else if (size.contains("GiB")) {
            mem = Integer.parseInt(size.replace(",", "").replace("GiB", ""));
        } else {
            mem = Integer.parseInt(size.replace(",", "").replace("KiB", ""));
        }

        Log.d("TAG", "Memory:" + mem);
        return mem;
    }
}
