package com.eit.kickit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eit.kickit.R
import com.eit.kickit.models.Comment

class Comment_Adapter(var comments: ArrayList<Comment>) : RecyclerView.Adapter<Comment_Adapter.CommentViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {

        val view : View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.comment_item,
                parent,
                false)
        return CommentViewHolder(view)
    }

    override fun getItemCount(): Int {

        return comments.size

    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {

        val comment : Comment = comments[position]

        holder.bind(comment)

    }

    fun add(comment: Comment){
        comments.add(comment)
        notifyItemChanged(comments.size - 1)
    }


    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private lateinit var comment: Comment

        private val commenter: TextView = itemView.findViewById(R.id.commenter)
        private val commentView: TextView = itemView.findViewById(R.id.comment)

        fun bind(comment: Comment){

            this.comment = comment

            commenter.text = comment.adv_name + " - "
            commentView.text = comment.com_text

        }
    }
}