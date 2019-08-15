package com.eit.kickit.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.eit.kickit.Activities.ViewChallengeActivity
import com.eit.kickit.R
import com.eit.kickit.models.Challenge

class Challenge_Adapter(private val challenges: ArrayList<Challenge>): RecyclerView.Adapter<Challenge_Adapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return challenges.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.blName.text = challenges.get(position).cName
        holder.blDescr.text = challenges.get(position).cDescription
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