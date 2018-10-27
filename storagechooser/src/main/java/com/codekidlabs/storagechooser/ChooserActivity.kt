package com.codekidlabs.storagechooser

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.codekidlabs.storagechooser.models.File
import com.codekidlabs.storagechooser.utils.ResourceUtil

import kotlinx.android.synthetic.main.activity_chooser.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

internal class ChooserActivity : AppCompatActivity(), CoroutineScope {

    val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var config: Config
    private lateinit var resourceUtil: ResourceUtil
    private lateinit var fileRecyclerView: RecyclerView
    private var fileList = mutableListOf<File>()

    private var masterPath: String = "/storage/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chooser)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val bundle = intent.getBundleExtra("configBundle")
        config = bundle?.getParcelable("test") as Config
        resourceUtil = ResourceUtil(applicationContext)

        initMasterPath()
        initUI()
    }

    private fun initMasterPath() {
        config.selection.onSingleSelection("huhu")
    }

    private fun refresh() {

    }

    private fun setTitleText() {
        when (config.type) {
            ChooserType.BASIC -> toolbar.title = "Choose Storage"
            else -> {
                toolbar.title = "Testing ..."
            }
        }
    }

    private fun initUI() {
        fileRecyclerView = findViewById(R.id.file_recycler_view)
        toolbar.setBackgroundColor(resourceUtil.getColor(android.R.color.white))
        toolbar.setTitleTextColor(resourceUtil.getColor(android.R.color.black))

        setTitleText()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resourceUtil.getColor(android.R.color.white)
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}
