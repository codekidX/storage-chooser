package com.codekidlabs.storagechooser.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.R;
import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.Content;
import com.codekidlabs.storagechooser.adapters.SecondaryChooserAdapter;
import com.codekidlabs.storagechooser.models.Config;
import com.codekidlabs.storagechooser.utils.DiskUtil;
import com.codekidlabs.storagechooser.utils.FileUtil;
import com.codekidlabs.storagechooser.utils.ResourceUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SecondaryChooserFragment extends android.app.DialogFragment {

    private View mLayout;
    private View mInactiveGradient;
    private ViewGroup mContainer;

    private TextView mPathChosen;
    private ImageButton mBackButton;
    private Button mSelectButton;
    private Button mCreateButton;
    private ImageView mNewFolderImageView;
    private EditText mFolderNameEditText;

    private RelativeLayout mNewFolderView;

    private String mBundlePath;
    private ListView listView;


    private static final String INTERNAL_STORAGE_TITLE = "Internal Storage";
    private static final String EXTERNAL_STORAGE_TITLE = "ExtSD";


    private static final int FLAG_DISSMISS_NORMAL = 0;
    private static final int FLAG_DISSMISS_INIT_DIALOG = 1;

    private boolean isOpen;

    private static String theSelectedPath = "";
    private static String mAddressClippedPath = "";

    private List<String> customStoragesList;
    private SecondaryChooserAdapter secondaryChooserAdapter;

    private FileUtil fileUtil;

    private Config mConfig;
    private Content mContent;
    private Context mContext;
    private Handler mHandler;
    private ResourceUtil mResourceUtil;

    private View.OnClickListener mSelectButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mConfig.isActionSave()) {
                DiskUtil.saveChooserPathPreference(mConfig.getPreference(), theSelectedPath);
            } else {
                Log.d("StorageChooser", "Chosen path: " + theSelectedPath);
            }

            StorageChooser.onSelectListener.onSelect(theSelectedPath);
            dissmissDialog(FLAG_DISSMISS_NORMAL);
        }

    };

    private View.OnClickListener mBackButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            performBackAction();
        }
    };

    private View.OnClickListener mNewFolderButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showAddFolderView();
        }
    };

    private View.OnClickListener mNewFolderButtonCloseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideAddFolderView();
            hideKeyboard();
        }
    };

    private View.OnClickListener mCreateButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(validateFolderName()) {
                boolean success = FileUtil.createDirectory(mFolderNameEditText.getText().toString().trim(), theSelectedPath);
                if(success) {
                    Toast.makeText(mContext, mContent.getFolderCreatedToastText(), Toast.LENGTH_SHORT).show();
                    trimPopulate(theSelectedPath);
                    hideKeyboard();
                    hideAddFolderView();
                } else {
                    Toast.makeText(mContext, mContent.getFolderErrorToastText(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    private boolean keyboardToggle;
    private String TAG = "StorageChooser";
    private boolean isFilePicker;

    private void showAddFolderView() {
        mNewFolderView.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.anim_new_folder_view);
        mNewFolderView.startAnimation(anim);
        mInactiveGradient.startAnimation(anim);


            if (DiskUtil.isLollipopAndAbove()) {
                mNewFolderImageView.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.drawable_plus_to_close));
                // image button animation
                Animatable animatable = (Animatable) mNewFolderImageView.getDrawable();
                animatable.start();
            }
            mNewFolderImageView.setOnClickListener(mNewFolderButtonCloseListener);

//        mNewFolderButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.window_close));

        //listview should not be clickable
        SecondaryChooserAdapter.shouldEnable = false;
    }

    private void hideAddFolderView() {
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.anim_close_folder_view);
        mNewFolderView.startAnimation(anim);
        mNewFolderView.setVisibility(View.INVISIBLE);

            if (DiskUtil.isLollipopAndAbove()) {
                mNewFolderImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.drawable_close_to_plus));
                // image button animation
                Animatable animatable = (Animatable) mNewFolderImageView.getDrawable();
                animatable.start();
            }
            mNewFolderImageView.setOnClickListener(mNewFolderButtonClickListener);

        //listview should be clickable
        SecondaryChooserAdapter.shouldEnable = true;

        mInactiveGradient.startAnimation(anim);
        mInactiveGradient.setVisibility(View.INVISIBLE);

//        mNewFolderButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.plus));
    }

    private boolean isFolderViewVisible() {
        return mNewFolderView.getVisibility() == View.VISIBLE;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mFolderNameEditText.getWindowToken(), 0);

    }

    private void performBackAction() {
        int slashIndex = theSelectedPath.lastIndexOf("/");

        if(!mConfig.isSkipOverview()) {
            if(theSelectedPath.equals(mBundlePath)) {
                SecondaryChooserFragment.this.dismiss();

                //delay until close animation ends
               mHandler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       dissmissDialog(FLAG_DISSMISS_INIT_DIALOG);
                   }
               }, 200);
            } else {
                theSelectedPath = theSelectedPath.substring(0, slashIndex);
                Log.e("SCLib", "Performing back action: " + theSelectedPath);
                populateList("");
            }
        } else {
            dissmissDialog(FLAG_DISSMISS_NORMAL);
        }
    }

    private void dissmissDialog(int flag) {
        theSelectedPath = "";
        mAddressClippedPath = "";

        switch (flag) {
            case FLAG_DISSMISS_INIT_DIALOG:
                ChooserDialogFragment c = new ChooserDialogFragment();
                c.show(mConfig.getFragmentManager(), "storagechooser_dialog");
                break;
            case FLAG_DISSMISS_NORMAL:
                this.dismiss();
                break;
        }
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
        mConfig = StorageChooser.sConfig;
        mHandler = new Handler();

        // init storage-chooser content [localization]
        if(mConfig.getContent() == null) {
            mContent = new Content();
        } else {
            mContent = mConfig.getContent();
        }

        mContext = getActivity().getApplicationContext();
        mResourceUtil = new ResourceUtil(mContext);
        mLayout = inflater.inflate(R.layout.custom_storage_list, container, false);
        initListView(mContext, mLayout, mConfig.isShowMemoryBar());

        initUI();
        initNewFolderView();
        updateUI();

        return mLayout;
    }


    private void initUI()  {

        mBackButton = (ImageButton) mLayout.findViewById(R.id.back_button);
        mSelectButton = (Button) mLayout.findViewById(R.id.select_button);

        mCreateButton = (Button) mLayout.findViewById(R.id.create_folder_button);

        mNewFolderView = (RelativeLayout) mLayout.findViewById(R.id.new_folder_view);
        mFolderNameEditText = (EditText) mLayout.findViewById(R.id.et_folder_name);

        mInactiveGradient = mLayout.findViewById(R.id.inactive_gradient);


    }

    private void updateUI() {

        //at start dont show the new folder view unless user clicks on the add/plus button
        mNewFolderView.setVisibility(View.INVISIBLE);
        mInactiveGradient.setVisibility(View.INVISIBLE);


        mFolderNameEditText.setHint(mContent.getTextfieldHintText());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mFolderNameEditText.setHintTextColor(mResourceUtil.getColor(mContent.getTextfieldHintColor()));
        }


        // set label of buttons [localization]
        mSelectButton.setText(mContent.getSelectLabel());
        mCreateButton.setText(mContent.getCreateLabel());

        mSelectButton.setTextColor(mResourceUtil.getColor(R.color.select_color));

        mBackButton.setOnClickListener(mBackButtonClickListener);
        mSelectButton.setOnClickListener(mSelectButtonClickListener);
        mCreateButton.setOnClickListener(mCreateButtonClickListener);

        if(mConfig.getSecondaryAction() == StorageChooser.FILE_PICKER) {
            mSelectButton.setVisibility(View.GONE);
            setBottomNewFolderView();
        }

    }

    private void setBottomNewFolderView() {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mNewFolderView.setLayoutParams(lp);
    }

    private void initNewFolderView() {

        RelativeLayout mNewFolderButtonHolder = (RelativeLayout) mLayout.findViewById(R.id.new_folder_button_holder);

        mNewFolderImageView = (ImageView) mLayout.findViewById(R.id.new_folder_iv);
        mNewFolderImageView.setOnClickListener(mNewFolderButtonClickListener);

        if(!mConfig.isAllowAddFolder()) {
            mNewFolderButtonHolder.setVisibility(View.GONE);
        }

    }


    /**
     * storage listView related code in this block
     */
    private void initListView(Context context, View view, boolean shouldShowMemoryBar) {
        listView = (ListView) view.findViewById(R.id.storage_list_view);
        mPathChosen = (TextView) view.findViewById(R.id.path_chosen);
        mBundlePath = this.getArguments().getString(DiskUtil.SC_PREFERENCE_KEY);
        isFilePicker = this.getArguments().getBoolean(DiskUtil.SC_CHOOSER_FLAG, false);
        populateList(mBundlePath);
        secondaryChooserAdapter =new SecondaryChooserAdapter(customStoragesList, context, shouldShowMemoryBar);
        secondaryChooserAdapter.setPrefixPath(theSelectedPath);

        listView.setAdapter(secondaryChooserAdapter);
        //listview should be clickable at first
        SecondaryChooserAdapter.shouldEnable = true;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String jointPath = theSelectedPath + "/" + customStoragesList.get(i);
                        if(FileUtil.isDir(jointPath)) {
                            populateList("/" + customStoragesList.get(i));
                        } else {
                            StorageChooser.onSelectListener.onSelect(jointPath);
                            dissmissDialog(FLAG_DISSMISS_NORMAL);
                        }
                    }
                }, 300);
            }
        });

    }


    /**
     * evaluates path with respect to the list click position
     * @param i position in list
     */
    private void evaluateAction(int i) {
        String preDefPath = mConfig.getPredefinedPath();
        boolean isCustom = mConfig.isAllowCustomPath();
        if(preDefPath == null) {
            Log.w(TAG, "No predefined path set");
        } else if(isCustom) {
            populateList("/" + customStoragesList.get(i));
        }
    }

    private boolean doesPassMemoryThreshold(long threshold, String memorySuffix, long availableSpace) {
        return true;
    }

    /**
     * populate storageList with necessary storages with filter applied
     * @param path defines the path for which list of folder is requested
     */
    private void populateList(String path) {
        if(customStoragesList == null) {
            customStoragesList = new ArrayList<>();
        } else {
            customStoragesList.clear();
        }

        fileUtil = new FileUtil();
        theSelectedPath = theSelectedPath +  path;
        if(secondaryChooserAdapter !=null && secondaryChooserAdapter.getPrefixPath() !=null) {
            secondaryChooserAdapter.setPrefixPath(theSelectedPath);
        }

        //if the path length is greater than that of the addressbar length
        // we need to clip the starting part so that it fits the length and makes some room
        int pathLength = theSelectedPath.length();
        if(pathLength >= 25) {
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

        File[] volumeList;

        if(isFilePicker) {
            volumeList = fileUtil.listFilesInDir(theSelectedPath);
        } else {
            volumeList = fileUtil.listFilesAsDir(theSelectedPath);
        }

        Log.e("SCLib", theSelectedPath);
        if(volumeList != null) {
            for (File f : volumeList) {
                if(mConfig.isShowHidden()) {
                    customStoragesList.add(f.getName());
                } else {
                    if (!f.getName().startsWith(".")) {
                        customStoragesList.add(f.getName());
                    }
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


        if(secondaryChooserAdapter !=null) {
            secondaryChooserAdapter.notifyDataSetChanged();
        }

        playTheAddressBarAnimation();
    }

    /**
     * Unlike populate list trim populate only updates the list not the addressbar.
     * This is used when creating new folder and update to list is required
     * @param s is the path to be refreshed
     * */
    private void trimPopulate(String s) {
        if(customStoragesList == null) {
            customStoragesList = new ArrayList<>();
        } else {
            customStoragesList.clear();
        }
        File[] volumeList = fileUtil.listFilesInDir(theSelectedPath);

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


        if(secondaryChooserAdapter !=null) {
            secondaryChooserAdapter.setPrefixPath(s);
            secondaryChooserAdapter.notifyDataSetChanged();
        }
    }

    private void playTheAddressBarAnimation() {
        mPathChosen.setText(mAddressClippedPath);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_address_bar);
        mPathChosen.startAnimation(animation);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = StorageChooser.dialog;
        d.setContentView(getLayout(LayoutInflater.from(getActivity().getApplicationContext()), mContainer));
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

    /**
     * Checks if edit text field is empty or not. Since there is only one edit text here no
     * param is required for now.
     */
    private boolean validateFolderName() {
        if (mFolderNameEditText.getText().toString().trim().isEmpty()) {
            mFolderNameEditText.setError(mContent.getTextfieldErrorText());
            return false;
        }
        return true;
    }
}
