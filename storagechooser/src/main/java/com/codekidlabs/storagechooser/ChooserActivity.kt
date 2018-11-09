package com.codekidlabs.storagechooser

import android.os.Bundle
import android.util.DisplayMetrics
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.codekidlabs.storagechooser.adapters.FileRecyclerViewAdapter
import com.codekidlabs.storagechooser.adapters.SegueRecyclerViewAdapter
import com.codekidlabs.storagechooser.managers.DisplayType
import com.codekidlabs.storagechooser.managers.FileManager
import com.codekidlabs.storagechooser.utils.ResourceUtil

import kotlinx.android.synthetic.main.activity_chooser.*
import kotlinx.android.synthetic.main.content_chooser.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.CoroutineContext

internal class ChooserActivity : AppCompatActivity(), CoroutineScope {

    val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var config: Config
    private lateinit var resourceUtil: ResourceUtil

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var segueLayoutManager: LinearLayoutManager


    // DATA
    private var fileList = mutableListOf<File>()
    private var segueList = mutableListOf<String>()
    private lateinit var fileManager: FileManager
    private lateinit var segueRecyclerViewAdapter: SegueRecyclerViewAdapter
    private lateinit var fileRecyclerViewAdapter: FileRecyclerViewAdapter
    private lateinit var linearSmoothScroller: LinearSmoothScroller
    private var masterPath: String = "/storage/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chooser)

        val bundle = intent.getBundleExtra("configBundle")
        config = bundle?.getParcelable("test") as Config
        config.selection.onSingleSelection("huhu")
        resourceUtil = ResourceUtil(applicationContext)
        fileManager = FileManager(this)

        initUI()
        launch(context = coroutineContext) {
            updateFileRecyclerView(fileManager.loadFiles())
        }
    }

    override fun onBackPressed() {
        fileManager.goBack()
        if (fileManager.shouldExit()) {
            super.onBackPressed()
        }

    }

    fun updateFileManagerWithPath(file: File) {
        if (file.isDirectory) {
            fileManager.goForward(file.name)
        } else {
            config.selection.onSingleSelection(file.absolutePath)
            this.finish()
        }
    }

    fun updateFileRecyclerView(newFileList: MutableList<File>, type: DisplayType = DisplayType.BASE) {
        fileList.clear()
        fileList.addAll(newFileList)

        segueList.clear()
        segueList.addAll(fileManager.getSegues())

        launch(context = coroutineContext) {
            if (fileRecyclerView.adapter == null) {
                fileRecyclerViewAdapter = FileRecyclerViewAdapter(fileList, this@ChooserActivity)
                fileRecyclerView.adapter = fileRecyclerViewAdapter
                fileRecyclerViewAdapter.notifyDataSetChanged()
            } else {
                fileRecyclerViewAdapter.notifyDataSetChanged()
            }

            if (segueRecyclerView.adapter == null) {
                segueRecyclerViewAdapter = SegueRecyclerViewAdapter(segueList)
                segueRecyclerView.adapter = segueRecyclerViewAdapter
                segueRecyclerViewAdapter.notifyDataSetChanged()
            } else {
                segueRecyclerViewAdapter.notifyDataSetChanged()
            }

            linearSmoothScroller.targetPosition = segueList.size - 1
            segueLayoutManager.startSmoothScroll(linearSmoothScroller)
        }


//        if (type != DisplayType.ROOT) {
//            // TODO - pass type to adapter and notify change
//        }
    }

    private fun initUI() {
        linearSmoothScroller = object : LinearSmoothScroller(segueRecyclerView.context) {

            override fun getHorizontalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_END
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                displayMetrics?.let {
                    return 150f / it.densityDpi
                }
                return super.calculateSpeedPerPixel(displayMetrics)
            }
        }
        linearLayoutManager = LinearLayoutManager(this)
        segueLayoutManager = LinearLayoutManager(this)
        segueLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        fileRecyclerView.layoutManager = linearLayoutManager
        segueRecyclerView.layoutManager = segueLayoutManager


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}
