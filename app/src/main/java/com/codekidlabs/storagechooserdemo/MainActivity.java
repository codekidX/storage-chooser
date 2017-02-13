package com.codekidlabs.storagechooserdemo;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.codekidlabs.storagechooser.StorageChooser;

public class MainActivity extends AppCompatActivity {

    Context context;
    private static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(DialogFragment.STYLE_NO_TITLE);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fragmentManager = getSupportFragmentManager();


        getFragmentManager().beginTransaction().replace(R.id.container, new DemoPreference()).commit();

    }

    public static int getColorFromResource(Context context, int id) {
        return ContextCompat.getColor(context, id);
    }

    public static class DemoPreference extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_demo);

            PreferenceScreen preferenceScreen = getPreferenceScreen();
            Preference overviewPref = preferenceScreen.findPreference("pref_overview");
            Preference customPref = preferenceScreen.findPreference("pref_custom");
            Preference filePickerPref = preferenceScreen.findPreference("pref_file_picker");

            overviewPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    StorageChooser chooser = new StorageChooser.Builder()
                            .withActivity(getActivity())
                            .withFragmentManager(fragmentManager)
                            .withMemoryBar(true)
                            .build();

                    chooser.show();
                    return true;
                }
            });

            customPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    StorageChooser chooser = new StorageChooser.Builder()
                            .withActivity(getActivity())
                            .allowCustomPath(true)
                            .withFragmentManager(fragmentManager)
                            .setType(StorageChooser.DIRECTORY_CHOOSER)
                            .withMemoryBar(true)
                            .build();

                    chooser.show();
                    return true;
                }
            });


            filePickerPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    StorageChooser chooser = new StorageChooser.Builder()
                            .withActivity(getActivity())
                            .withFragmentManager(fragmentManager)
                            .allowCustomPath(true)
                            .setType(StorageChooser.FILE_PICKER)
                            .withMemoryBar(true)
                            .build();

                    chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                        @Override
                        public void onSelect(String path) {
                            Log.e("File name", path);
                        }
                    });

                    chooser.show();
                    return true;
                }
            });
        }
    }
}
