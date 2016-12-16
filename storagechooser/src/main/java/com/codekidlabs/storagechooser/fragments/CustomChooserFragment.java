package com.codekidlabs.storagechooser.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.StorageChooserBuilder;
import com.codekidlabs.storagechooser.adapters.StorageChooserCustomListAdapter;
import com.codekidlabs.storagechooser.utils.DiskUtil;
import com.codekidlabs.storagechooser.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CustomChooserFragment extends DialogFragment {

    private View mLayout;
    private ViewGroup mContainer;

    private TextView mPathChosen;
    private ImageButton mBackButton;
    private Button mSelectButton;

    private String mBundlePath;


    private static final String INTERNAL_STORAGE_TITLE = "Internal Storage";
    private static final String EXTERNAL_STORAGE_TITLE = "ExtSD";

    private static String theSelectedPath = "";
    private static String mAddressClippedPath = "";

    private List<String> customStoragesList;
    private StorageChooserCustomListAdapter customListAdapter;


    private View.OnClickListener mSelectButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(StorageChooserBuilder.sConfig.isActionSave()) {
                DiskUtil.saveChooserPathPreference(StorageChooserBuilder.sConfig.getPreference(), theSelectedPath);
            } else {
                Log.d("StorageChooser", "Chosen path: " + theSelectedPath);
            }
            CustomChooserFragment.this.dismiss();
        }

    };

    private View.OnClickListener mBackButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            performBackAction();
        }
    };

    private void performBackAction() {
        int slashIndex = theSelectedPath.lastIndexOf("/");

        if(theSelectedPath.equals(mBundlePath)) {
            dissmissDialog();
        } else {
            theSelectedPath = theSelectedPath.substring(0, slashIndex);
            Log.e("SCLib", "Performing back action: " + theSelectedPath);
                populateList("");
        }
    }

    private void dissmissDialog() {
        theSelectedPath = "";
        mAddressClippedPath = "";
        this.dismiss();
        ChooserDialogFragment c = new ChooserDialogFragment();
        c.show(StorageChooserBuilder.sConfig.getFragmentManager(), "storagechooser_dialog");
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
        mLayout = inflater.inflate(R.layout.custom_storage_list, container, false);
        initListView(getContext(), mLayout, StorageChooserBuilder.sConfig.isShowMemoryBar());

        mBackButton = (ImageButton) mLayout.findViewById(R.id.back_button);
        mSelectButton = (Button) mLayout.findViewById(R.id.select_button);

        mBackButton.setOnClickListener(mBackButtonClickListener);
        mSelectButton.setOnClickListener(mSelectButtonClickListener);

        return mLayout;
    }


    /**
     * storage listView related code in this block
     */
    private void initListView(Context context, View view, boolean shouldShowMemoryBar) {
        ListView listView = (ListView) view.findViewById(R.id.storage_list_view);
        mPathChosen = (TextView) view.findViewById(R.id.path_chosen);
        mBundlePath = this.getArguments().getString(DiskUtil.SC_PREFERENCE_KEY);
        populateList(mBundlePath);
        customListAdapter =new StorageChooserCustomListAdapter(customStoragesList, context, shouldShowMemoryBar);
        listView.setAdapter(customListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                populateList("/" + customStoragesList.get(i));
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
    private void populateList(String path) {
        if(customStoragesList == null) {
            customStoragesList = new ArrayList<String>();
        } else {
            customStoragesList.clear();
        }

        FileUtil fileUtil = new FileUtil();
        theSelectedPath = theSelectedPath +  path;

        //if the path length is greater than that of the addressbar length
        // we need to clip the starting part so that it fits the length and makes some room
        int pathLength = theSelectedPath.length();
        if(pathLength >= 35) {
            // how many directories did user choose
            int slashCount = getSlashCount(theSelectedPath);
            if(slashCount > 2) {
                mAddressClippedPath = theSelectedPath.substring(theSelectedPath.indexOf("/", theSelectedPath.indexOf("/") + 2), pathLength);
            } else if(slashCount <= 2) {
                mAddressClippedPath = theSelectedPath.substring(theSelectedPath.indexOf("/", theSelectedPath.indexOf("/") + 2), pathLength);
            }
        } else {
            mAddressClippedPath = theSelectedPath;
        }

        File[] volumeList = fileUtil.listFilesForDir(theSelectedPath);

        Log.e("SCLib", theSelectedPath);
        if(volumeList != null) {
            for (File f : volumeList) {
                if(!f.getName().startsWith(".")) {
                    customStoragesList.add(f.getName());
                }
            }

            Collections.sort(customStoragesList, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });
        }else {
            customStoragesList.clear();
        }


        if(customListAdapter !=null) {
            customListAdapter.notifyDataSetChanged();
        }

        playTheAddressBarAnimation();
    }

    private void playTheAddressBarAnimation() {
        mPathChosen.setText(mAddressClippedPath);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_address_bar);
        mPathChosen.startAnimation(animation);
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

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        theSelectedPath = "";
        mAddressClippedPath = "";
    }

    private int getSlashCount(String path) {
        int count = 0;

        for(char s: path.toCharArray()) {
            if(s == '/') {
                count++;
            }
        }
        return count;
    }
}
