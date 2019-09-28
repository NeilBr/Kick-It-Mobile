package com.eit.kickit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eit.kickit.R
import com.eit.kickit.adapters.MyBucketList_Adapter
import com.eit.kickit.database.Database
import com.eit.kickit.models.Challenge
import kotlinx.android.synthetic.main.fragment_my_bucket_list.*
import java.sql.ResultSet

class MyBucketListFragment : Fragment() {

    private val myChallenges : ArrayList<Challenge> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(container?.context).inflate(R.layout.fragment_my_bucket_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*
        val c1 = Challenge(1, "Walk 5km", "Take a stroll", 1, 0.00, false, 0)
        val c2 = Challenge(2, "Waltz backwards", "Dance tings innit", 50, 0.00, false, 2)
        val c3 = Challenge(3,"Clap twice", "Meme Review", 69, 10000.00, false, 2)
*/

        loadMyChallenges()
    }

    private fun loadMyChallenges()
    {
        val query = "SELECT * FROM my_bucketlist"

        progressBarMyChallenges.visibility = View.VISIBLE

        Database().runQuery(query, true)
        {
                result -> getMyChallenges(result)
        }
    }

    private fun getMyChallenges(result : Any)
    {
        val resultSet: ResultSet = result as ResultSet
        var status : Boolean = false
        while (resultSet.next())
        {
            if (resultSet.getInt("c_status") == 1)
            {
                status = true
            }
            var challengeItem = Challenge(
                resultSet.getInt("c_id"),
                resultSet.getString("c_name"),
                resultSet.getString("c_description"),
                resultSet.getInt("c_points"),
                resultSet.getDouble("c_price"),
                status,
                resultSet.getInt("bl_id")
            )
            myChallenges.add(challengeItem)
            progressBarMyChallenges.visibility = View.INVISIBLE
        }
        setAdapters()
    }

    private fun setAdapters()
    {
        rvMyBucketList.layoutManager = LinearLayoutManager(activity)
        rvMyBucketList.adapter = MyBucketList_Adapter(myChallenges)
    }
}