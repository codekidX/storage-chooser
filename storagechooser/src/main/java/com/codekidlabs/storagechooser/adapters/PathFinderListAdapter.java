package com.codekidlabs.storagechooser.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codekidlabs.storagechooser.R;

import java.util.List;

public class PathFinderListAdapter extends BaseAdapter {

    private List<String> storagesList;
    private Context mContext;


    public PathFinderListAdapter(Context mContext, List<String> storagesList) {
        this.storagesList = storagesList;
        this.mContext = mContext;

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

        View rootView = inflater.inflate(R.layout.row_paths, viewGroup, false);

        TextView storageName = (TextView) rootView.findViewById(R.id.storage_name);

        storageName.setText(storagesList.get(i));

        return rootView;

    }
}