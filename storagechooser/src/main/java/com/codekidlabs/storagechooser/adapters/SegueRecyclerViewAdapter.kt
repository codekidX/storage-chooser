package com.codekidlabs.storagechooser.adapters

import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codekidlabs.storagechooser.R
import com.codekidlabs.storagechooser.inflate


internal class SegueRecyclerViewAdapter(private var segueList: MutableList<String>): RecyclerView.Adapter<SegueViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SegueViewHolder {
        val itemView = parent.inflate(R.layout.row_segue, false)
        return SegueViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return segueList.size
    }

    override fun onBindViewHolder(holder: SegueViewHolder, position: Int) {
        val s = if (position == segueList.count() - 1) {
            "${segueList[position]}   "
        } else {
            "${segueList[position]}   ▶"

        }
        holder.segueNameTV.text = s
    }

}

internal class SegueViewHolder(view: View): RecyclerView.ViewHolder(view) {

    val segueNameTV: TextView = view.findViewById(R.id.segue_name_tv)
}