package com.codekidlabs.storagechooserdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast

import com.codekidlabs.storagechooser.Content
import com.codekidlabs.storagechooser.OldStorageChooser
import com.codekidlabs.storagechooser.StorageChooser
import com.codekidlabs.storagechooser.utils.DiskUtil

import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private var coordinatorLayout: CoordinatorLayout? = null
    private var dirCheckBox: CheckBox? = null
    private var fileCheckBox: CheckBox? = null

    private val builder = OldStorageChooser.Builder()
    private var chooser: OldStorageChooser? = null
    private val TAG = javaClass.getName()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coordinatorLayout = findViewById<View>(R.id.coord) as CoordinatorLayout
        dirCheckBox = findViewById<View>(R.id.checkbox_dir) as CheckBox
        fileCheckBox = findViewById<View>(R.id.checkbox_file) as CheckBox

        (findViewById<View>(R.id.checkbox_membar) as CheckBox).setOnCheckedChangeListener { buttonView, isChecked -> builder.withMemoryBar(isChecked) }

        (findViewById<View>(R.id.checkbox_free_space_label) as CheckBox).setOnCheckedChangeListener { buttonView, isChecked -> builder.hideFreeSpaceLabel(isChecked) }

        (findViewById<View>(R.id.checkbox_add_folder) as CheckBox).setOnCheckedChangeListener { buttonView, isChecked -> builder.allowAddFolder(isChecked) }

        (findViewById<View>(R.id.checkbox_hidden) as CheckBox).setOnCheckedChangeListener { buttonView, isChecked -> builder.showHidden(isChecked) }

        dirCheckBox!!.setOnCheckedChangeListener { buttonView, isChecked ->
            builder.allowCustomPath(isChecked)

            if (isChecked) {
                builder.setType(OldStorageChooser.DIRECTORY_CHOOSER)
                fileCheckBox!!.isEnabled = false
            } else {
                fileCheckBox!!.isEnabled = true
            }
        }

        (findViewById<View>(R.id.checkbox_skip) as CheckBox).setOnCheckedChangeListener { buttonView, isChecked -> builder.skipOverview(isChecked) }

        (findViewById<View>(R.id.checkbox_session) as CheckBox).setOnCheckedChangeListener { buttonView, isChecked -> builder.shouldResumeSession(isChecked) }

        fileCheckBox!!.setOnCheckedChangeListener { buttonView, isChecked ->
            builder.allowCustomPath(isChecked)

            if (isChecked) {
                builder.setType(OldStorageChooser.FILE_PICKER)
                dirCheckBox!!.isEnabled = false
            } else {
                dirCheckBox!!.isEnabled = true
            }
        }

        (findViewById<View>(R.id.checkbox_threshold) as CheckBox).setOnCheckedChangeListener { buttonView, isChecked ->
            builder.applyMemoryThreshold(isChecked)
            builder.withThreshold(1, DiskUtil.IN_GB)
        }

        (findViewById<View>(R.id.checkbox_dark) as CheckBox).setOnCheckedChangeListener { buttonView, isChecked -> builder.setTheme(getScTheme(isChecked)) }


        val spinner = findViewById<View>(R.id.filter_spinner) as Spinner
        val adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                when (i) {
                    0 -> {
                        builder.crunch()
                        builder.filter(null)
                    }
                    1 -> {
                        builder.crunch()
                        builder.filter(OldStorageChooser.FileType.VIDEO)
                    }
                    2 -> {
                        builder.crunch()
                        builder.filter(OldStorageChooser.FileType.AUDIO)
                    }
                    3 -> {
                        builder.crunch()
                        builder.filter(OldStorageChooser.FileType.DOCS)
                    }
                    4 -> {
                        builder.crunch()
                        builder.filter(OldStorageChooser.FileType.IMAGES)
                    }
                    5 -> {

                        val formats = ArrayList<String>()
                        formats.add("txt")
                        formats.add("mkv")
                        builder.customFilter(formats)
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }


        // ----------------- Localization -------------------
        val c = Content()
        c.createLabel = "Create"
        c.internalStorageText = "My Storage"
        c.cancelLabel = "Cancel"
        c.selectLabel = "Select"
        c.overviewHeading = "Choose Drive"

        builder.withActivity(this)
                .withFragmentManager(supportFragmentManager)
                .setMemoryBarHeight(1.5f)
                //                .skipOverview(true, "/storage/emulated/0/DCIM/Camera")
                //                .disableMultiSelect()
                .withContent(c)


        findViewById<View>(R.id.fab).setOnClickListener {
            chooser = builder.build()
            chooser!!.setOnSelectListener { path -> Toast.makeText(applicationContext, path, Toast.LENGTH_SHORT).show() }

            chooser!!.setOnCancelListener { Toast.makeText(applicationContext, "Storage Chooser Cancelled.", Toast.LENGTH_SHORT).show() }

            chooser!!.setOnMultipleSelectListener { selectedFilePaths ->
                for (s in selectedFilePaths) {
                    Log.e(TAG, s)
                }
            }

            chooser!!.show()

            //                AlertDialog d = new AlertDialog.Builder(MainActivity.this)
            //                        .setTitle("Also")
            //                        .setMessage("Android P")
            //                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            //                            @Override
            //                            public void onClick(DialogInterface dialog, int which) {
            //                                Log.e("Awesome", "Awesome");
            //                            }
            //                        })
            //                        .create();
            //                d.show();
        }
    }

    private fun getScTheme(isChecked: Boolean): OldStorageChooser.Theme {
        val theme = OldStorageChooser.Theme(applicationContext)
        theme.scheme = if (isChecked) resources.getIntArray(R.array.paranoid_theme) else theme.defaultScheme
        return theme
    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && coordinatorLayout != null && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(coordinatorLayout!!, "Demo app needs storage permission to display file list", Snackbar.LENGTH_INDEFINITE)
                    .setAction("GRANT") { ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 199) }.show()
        }
    }
}
