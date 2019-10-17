package com.eit.kickit.adapters

import android.app.DownloadManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.eit.kickit.R
import com.eit.kickit.common.FileHandler
import com.eit.kickit.common.S3LINK
import com.eit.kickit.database.Database
import com.eit.kickit.models.Comment
import com.eit.kickit.models.Post
import java.io.File
import java.sql.ResultSet

class Post_Adapter(private var posts: ArrayList<Post>) : RecyclerView.Adapter<Post_Adapter.PostViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        val view : View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.post_item,
                parent,
                false)

        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {

        return posts.size

    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

        val post: Post = posts.get(position)

        holder.bind(post)

    }

    fun add(post: Post){
        posts.add(post)
        notifyItemChanged(posts.size - 1)
    }


    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        lateinit var post: Post

        private val poster: TextView = itemView.findViewById(R.id.poster_name)
        private val post_image: ImageView = itemView.findViewById(R.id.post_image)
        private val report_post: Button = itemView.findViewById(R.id.report_post)
        private val verify_post: Button = itemView.findViewById(R.id.verify_post)
        private val feedback_post: Button = itemView.findViewById(R.id.add_feedback)
        private val post_caption: TextView = itemView.findViewById(R.id.post_caption)
        private val feedback_text: EditText = itemView.findViewById(R.id.feedback_text)
        private val image_loading: ProgressBar = itemView.findViewById(R.id.postImageLoading)
        private val loadingComments: ProgressBar = itemView.findViewById(R.id.loadingComments)
        private val comment_list: RecyclerView = itemView.findViewById(R.id.comment_list)

        private lateinit var comment_adapter: Comment_Adapter
        private var comments : ArrayList<Comment> = ArrayList()

        fun bind(post: Post){
            this.post = post

            poster.text = post.poster_name
            post_caption.text = post.post_caption

            image_loading.visibility = View.VISIBLE
            loadingComments.visibility = View.VISIBLE

            val query = "SELECT c.com_id, c.com_text, CONCAT(a.adv_firstName, ' ', a.adv_surname) AS adv_name, c.p_id  " +
                    "FROM post_comments AS c INNER JOIN adventurers AS a ON c.adv_id = a.adv_id " +
                    "WHERE c.p_id = ${post.post_id}"

            Database().runQuery(query, true){
                results -> loadComments(results)
            }

            val fileName = post.post_url

            val transferUtil = FileHandler(itemView.context).createTransferUtil()

            val tempFile = File.createTempFile(fileName, ".jpg")

            val downloadObserver = transferUtil.download(S3LINK + fileName, tempFile)



            downloadObserver.setTransferListener(object : TransferListener {

                override fun onStateChanged(id: Int, state: TransferState) {
                    if (TransferState.COMPLETED == state) {
                        val bitMap = BitmapFactory.decodeFile(tempFile.path)
                        post_image.setImageBitmap(bitMap)

                        image_loading.visibility = View.INVISIBLE
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                    val percentDone = percentDonef.toInt()
                    println(percentDone)
                }

                override fun onError(id: Int, ex: Exception) {
                    ex.printStackTrace()
                }
            })

            report_post.setOnClickListener(this::report)
            verify_post.setOnClickListener(this::verify)
            feedback_post.setOnClickListener(this::feedback)

            if (post.post_status)
            {
                verify_post.isClickable = false
                verify_post.setTextColor(Color.GRAY)
            }
        }

        private fun loadComments(results: Any){

            if(results is ResultSet){

                comment_adapter = Comment_Adapter(comments)
                val linearLayoutManager = LinearLayoutManager(itemView.context)

                comment_list.layoutManager = linearLayoutManager
                comment_list.adapter = comment_adapter

                while(results.next()){

                    val comment = Comment(
                        results.getInt("com_id"),
                        results.getInt("p_id"),
                        results.getString("adv_name"),
                        results.getString("com_text")
                    )

                    comment_adapter.add(comment)

                }

            }
            else{
                println(results)
            }

            loadingComments.visibility = View.INVISIBLE

        }

        private fun report(view: View){

            Toast.makeText(itemView.context, "Report Post", Toast.LENGTH_SHORT).show()

        }

        fun verify(view: View){

            val query = "UPDATE posts SET p_status = 1 WHERE p_id = ${post.post_id}"
            Database().runQuery(query, false)
            {
                Toast.makeText(itemView.context, "${post.post_caption} has been verified", Toast.LENGTH_SHORT).show()
            }
        }

        fun feedback(view: View){

            Toast.makeText(itemView.context, "Add Feedback", Toast.LENGTH_SHORT).show()

        }


    }

}