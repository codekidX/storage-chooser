package com.codekidlabs.storagechooser.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.utils.FileUtil;
import com.codekidlabs.storagechooser.utils.ThumbnailUtil;

import java.util.List;

public class SecondaryChooserAdapter extends BaseAdapter {

    private List<String> storagesList;
    private Context mContext;
    private boolean shouldShowMemoryBar;
    public static boolean shouldEnable = true;

    public String prefixPath;
    private ThumbnailUtil thumbnailUtil;


    public SecondaryChooserAdapter(List<String> storagesList, Context mContext, boolean shouldShowMemoryBar) {
        this.storagesList = storagesList;
        this.mContext = mContext;
        this.shouldShowMemoryBar = shouldShowMemoryBar;

        // create instance once
        thumbnailUtil = new ThumbnailUtil(mContext);
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

        View rootView = inflater.inflate(R.layout.row_custom_paths, viewGroup, false);

        ImageView pathFolderIcon = (ImageView) rootView.findViewById(R.id.path_folder_icon);
        if(FileUtil.isDir(prefixPath + "/" + storagesList.get(i))) {
            applyFolderTint(pathFolderIcon);
        }

        thumbnailUtil.init(pathFolderIcon, storagesList.get(i));

        TextView storageName = (TextView) rootView.findViewById(R.id.storage_name);
        storageName.setText(storagesList.get(i));

        return rootView;

    }


    /**
     * return the spannable index of character '('
     * @param str SpannableStringBuilder to apply typeface changes
     * @return index of '('
     */
    private int getSpannableIndex(SpannableStringBuilder str) {
        return str.toString().indexOf("(") + 1;
    }

    public void setPrefixPath(String path) {
        this.prefixPath = path;
    }

    public String getPrefixPath() {
        return prefixPath;
    }

    private void applyFolderTint(ImageView im) {
        im.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary));
    }

    @Override
    public boolean isEnabled(int position) {
        return shouldEnable;
    }
}
