package com.eit.kickit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eit.kickit.MainActivity
import com.eit.kickit.R
import com.eit.kickit.adapters.Post_Adapter
import com.eit.kickit.database.Database
import com.eit.kickit.models.Post
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_home.*
import java.sql.ResultSet

class HomeFragment : Fragment() {

    private var posts: ArrayList<Post> = ArrayList()
    private lateinit var post_adapter: Post_Adapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(container?.context).inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeLoadBar.visibility = View.VISIBLE

        val reloadFab: FloatingActionButton = view.findViewById(R.id.fabReloadList)

        reloadFab.setOnClickListener {
            getPosts()
        }

        if(MainActivity.posts == null || MainActivity.posts is String){
            getPosts()
        }
        else{
            loadPosts(MainActivity.posts!!)
        }
    }

    private fun getPosts(){
        homeLoadBar.visibility = View.VISIBLE

        post_adapter.clear_all()
        posts = ArrayList()


        val query = "select p.p_id, a.adv_id, CONCAT(a.adv_firstName, ' ', a.adv_surname) as poster_name, p.p_caption, p.p_status, p.p_photoUrl, p.p_likes " +
                "from posts as p inner join adventurers as a on p.adv_id = a.adv_id ORDER BY p.p_id DESC"

        Database().runQuery(query, true){
                result -> loadPosts(result)
        }
    }

    private fun loadPosts(result: Any){

        homeLoadBar.visibility = View.VISIBLE

        val postList = activity!!.findViewById<RecyclerView>(R.id.posts_list)

        post_adapter = Post_Adapter(posts)
        val post_list_layout = LinearLayoutManager(activity!!.applicationContext)

        postList.layoutManager = post_list_layout
        postList.adapter = post_adapter

        if(result is ResultSet){

            while(result.next()){

                val post = Post(
                    result.getInt("p_id"),
                    result.getInt("adv_id"),
                    result.getString("poster_name"),
                    result.getString("p_caption"),
                    result.getBoolean("p_status"),
                    result.getString("p_photoUrl"),
                    result.getInt("p_likes")
                )

                post_adapter.add(post)

            }

        }
        else{
            println(result)
        }

        homeLoadBar.visibility = View.INVISIBLE

    }
}