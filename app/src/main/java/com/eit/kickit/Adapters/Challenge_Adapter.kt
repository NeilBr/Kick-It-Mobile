package com.eit.kickit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eit.kickit.R

class Challenge_Adapter(private val challenges: ArrayList<String>): RecyclerView.Adapter<Challenge_Adapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_challenges, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return challenges.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.blName.text = challenges[position]
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val blName : TextView = itemView.findViewById(R.id.titleText)
    }
}