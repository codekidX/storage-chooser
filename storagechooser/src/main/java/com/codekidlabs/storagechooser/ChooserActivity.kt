package com.codekidlabs.storagechooser

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView
import com.codekidlabs.storagechooser.managers.DisplayType
import com.codekidlabs.storagechooser.managers.FileManager
import com.codekidlabs.storagechooser.utils.ResourceUtil

import kotlinx.android.synthetic.main.activity_chooser.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.File
import kotlin.coroutines.CoroutineContext

internal class ChooserActivity : AppCompatActivity(), CoroutineScope {

    val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var config: Config
    private lateinit var resourceUtil: ResourceUtil
    private lateinit var fileRecyclerView: RecyclerView
    private var fileList = mutableListOf<File>()
    private lateinit var fileManager: FileManager

    private var masterPath: String = "/storage/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chooser)

        val bundle = intent.getBundleExtra("configBundle")
        config = bundle?.getParcelable("test") as Config
        resourceUtil = ResourceUtil(applicationContext)
        fileManager = FileManager(this)

        initMasterPath()
        initUI()
    }

    override fun onBackPressed() {
        fileManager.goBack()
        if (fileManager.shouldExit()) {
            super.onBackPressed()
        }

    }

    private fun initMasterPath() {
        config.selection.onSingleSelection("huhu")
    }

    private fun refresh() {

    }

    fun updateFileRecyclerView(newFileList: MutableList<File>, type: DisplayType) {
        fileList = newFileList
        if (type != DisplayType.ROOT) {
            // TODO - pass type to adapter and notify change
        }
    }

    private fun initUI() {

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}
