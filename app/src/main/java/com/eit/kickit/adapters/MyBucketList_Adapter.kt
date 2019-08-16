package com.eit.kickit.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eit.kickit.R
import com.eit.kickit.activities.ViewChallengeActivity
import com.eit.kickit.models.Challenge

class MyBucketList_Adapter(private val mybucketlist: ArrayList<Challenge>) : RecyclerView.Adapter<MyBucketList_Adapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mybucketlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.blName.text = mybucketlist.get(position).cName
        holder.blDescr.text = mybucketlist.get(position).cDescription
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val blName : TextView = itemView.findViewById(R.id.titleText)
        val blDescr : TextView = itemView.findViewById(R.id.normalText)

        init {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ViewChallengeActivity::class.java)
                itemView.context.startActivity(intent)
            }
        }
    }
}