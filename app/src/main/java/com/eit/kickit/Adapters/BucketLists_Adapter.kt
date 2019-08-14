package com.eit.kickit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eit.kickit.R

/**
 * This adapter is for the bucketlists to bind to the layout in bucket list tab
 */
class BucketLists_Adapter(private val bucketlists: ArrayList<String>): RecyclerView.Adapter<BucketLists_Adapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
     return bucketlists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.blName.text = bucketlists[position]
        holder.blReqPnts.text = bucketlists[position]
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val blName : TextView = itemView.findViewById(R.id.titleText)
        val blReqPnts : TextView = itemView.findViewById(R.id.normalText)
    }
}