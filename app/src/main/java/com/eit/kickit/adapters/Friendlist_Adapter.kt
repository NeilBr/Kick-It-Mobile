package com.eit.kickit.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eit.kickit.R
import com.eit.kickit.ViewProfileActivity
import com.eit.kickit.models.Adventurer

class Friendlist_Adapter(private var friends: ArrayList<Adventurer>) :
    RecyclerView.Adapter<Friendlist_Adapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recycler_view_item,
                parent,
                false)

        val listHolder = ListViewHolder(view)
        view.setOnClickListener { view ->
            val intent = Intent(view.context, ViewProfileActivity::class.java)
            intent.putExtra("display", 1)
            intent.putExtra("friend", listHolder.friend)
            view.context.startActivity(intent)
        }

        return listHolder
    }

    override fun getItemCount(): Int {

        return friends.size

    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        val friend: Adventurer = friends.get(position)

        holder.bind(friend)

    }

    fun add(friend: Adventurer){
        friends.add(friend)
        notifyItemChanged(friends.size - 1)
    }

    fun remove(position: Int){
        friends.removeAt(position)
        notifyItemChanged(position)
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        lateinit var friend: Adventurer
        private val title: TextView = itemView.findViewById(R.id.titleText)
        private val subtitle: TextView = itemView.findViewById(R.id.normalText)

        fun bind(friend: Adventurer){

            this.friend = friend

            title.text = "${this.friend.advFirstName} ${this.friend.advSurname}"
            subtitle.text = this.friend.advPoints.toString()

        }
    }
}