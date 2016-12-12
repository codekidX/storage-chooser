package com.codekidlabs.storagechooser.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.StorageChooserBuilder;
import com.codekidlabs.storagechooser.adapters.StorageChooserCustomListAdapter;
import com.codekidlabs.storagechooser.adapters.StorageChooserListAdapter;
import com.codekidlabs.storagechooser.models.Storages;
import com.codekidlabs.storagechooser.utils.DiskUtil;
import com.codekidlabs.storagechooser.utils.MemoryUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CustomChooserFragment extends DialogFragment {

    private View mLayout;
    private ViewGroup mContainer;

    private TextView mPathChosen;

    private static final String INTERNAL_STORAGE_TITLE = "Internal Storage";
    private static final String EXTERNAL_STORAGE_TITLE = "External Storage";

    private static final String EXTERNAL_STORAGE_PATH_KITKAT = "/storage/extSdCard";

    private List<String> customStoragesList;


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
        mLayout = inflater.inflate(R.layout.custom_storage_list, container, false);
        initListView(getContext(), mLayout, StorageChooserBuilder.sConfig.isShowMemoryBar());
        return mLayout;
    }


    /**
     * storage listView related code in this block
     */
    private void initListView(Context context, View view, boolean shouldShowMemoryBar) {
        ListView listView = (ListView) view.findViewById(R.id.storage_list_view);
        populateList();

        listView.setAdapter(new StorageChooserCustomListAdapter(customStoragesList, context, shouldShowMemoryBar));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });

    }


    /**
     * evaluates path with respect to the list click position
     * @param i position in list
     * @return String with the required path for developers
     */
    private String evaluatePath(int i) {
        String preDefPath = StorageChooserBuilder.sConfig.getPredefinedPath();
        if(preDefPath == null) {
            Log.e("StorageChooser", "Cannot return a path, set withPredefinedPath() in your builder.");
            return null;
        } else {
            if(i == 0) {
                return Environment.getExternalStorageDirectory().getAbsolutePath() + preDefPath;
            } else {
                return "/storage/" + customStoragesList.get(i);
            }
        }
    }

    private boolean doesPassMemoryThreshold(long threshold, String memorySuffix, long availableSpace) {
        return true;
    }

    /**
     * populate storageList with necessary storages with filter applied
     */
    private void populateList() {
        customStoragesList = new ArrayList<String>();

        File storageDir = new File("/storage");
        File internalStorageDir = Environment.getExternalStorageDirectory();

        File[] volumeList = internalStorageDir.listFiles();


        for(File f: volumeList) {
            customStoragesList.add(f.getName());
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = StorageChooserBuilder.dialog;
        d.setContentView(getLayout(LayoutInflater.from(getContext()), mContainer));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        d.getWindow().setAttributes(lp);
        return d;
    }
}
