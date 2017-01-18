package com.codekidlabs.storagechooserdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.StorageChooserView;
import com.codekidlabs.storagechooserdemo.utils.TypefaceUtil;

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
            Preference customPref = preferenceScreen.findPreference("pref_overview");
            Preference filePickerPref = preferenceScreen.findPreference("pref_overview");

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
                            .withFragmentManager(fragmentManager)
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
                            .withMemoryBar(true)
                            .build();

                    chooser.show();
                    return true;
                }
            });
        }
    }
}
