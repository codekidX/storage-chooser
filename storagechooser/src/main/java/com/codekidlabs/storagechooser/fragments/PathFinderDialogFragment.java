package com.codekidlabs.storagechooser.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codekidlabs.storagechooser.ExternalStoragePathFinder;
import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.adapters.PathFinderListAdapter;
import com.codekidlabs.storagechooser.utils.DiskUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PathFinderDialogFragment extends DialogFragment{


    private View mLayout;
    private ViewGroup mContainer;


    private static List<String> storagesList;

    private static String mPath;

    private static PathFinderDialogFragment sPathFinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sPathFinder = this;
    }

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
        mLayout = inflater.inflate(R.layout.path_finder_list, container, false);
        initListView(getContext(), mLayout);
        return mLayout;
    }

    private static void initListView(Context context, View view) {
        ListView listView = (ListView) view.findViewById(R.id.storage_list_view);
        populateList();

        listView.setAdapter(new PathFinderListAdapter(context, storagesList));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPath = storagesList.get(i);
                sPathFinder.dismiss();
            }
        });

    }

    private static void populateList() {
        storagesList = new ArrayList<String>();
        File rootStorageDir = new File("/storage");

        File[] storageDirs = rootStorageDir.listFiles();

        for(File f: storageDirs) {
            storagesList.add(f.getName());
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = ExternalStoragePathFinder.dialog;
        d.setContentView(getLayout(LayoutInflater.from(getContext()), mContainer));
        return d;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        ExternalStoragePathFinder.STORAGE_EXTERNAL_PATH = mPath;
        if(ExternalStoragePathFinder.getUserSharedPreference() != null) {
            DiskUtil.saveFinderPathPreference(ExternalStoragePathFinder.getUserSharedPreference(), ExternalStoragePathFinder.EXTERNAL_STORAGE_PATH_KEY);
        }
        super.onDismiss(dialog);
    }
}
