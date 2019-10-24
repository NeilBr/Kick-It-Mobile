package com.eit.kickit.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eit.kickit.R
import com.eit.kickit.adapters.BucketLists_Adapter
import com.eit.kickit.database.Database
import com.eit.kickit.models.BucketList
import kotlinx.android.synthetic.main.fragment_bucket_lists.*
import java.sql.ResultSet

class BucketListFragment : Fragment() {
    private var curBucketLists : ArrayList<BucketList> = ArrayList()
    private var loaded : Boolean = false
    private var advID : Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(container?.context).inflate(R.layout.fragment_bucket_lists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        advID = arguments!!.getInt("advID")
        progressBarBucketList.visibility = View.VISIBLE
        val query = "SELECT adv_totalPoints FROM adventurers WHERE adv_id = $advID"
        Database().runQuery(query, true)
        {
            result -> getPoints(result)
        }
    }

    private fun getPoints(result: Any)
    {
        val resultSet: ResultSet = result as ResultSet
        if (resultSet.next())
        {
            val points = resultSet.getDouble("adv_totalPoints")
            loadBucketLists(points)
        }
    }

    private fun loadBucketLists(points : Double)
    {
        val query = "SELECT * FROM bucketlists"



        Database().runQuery(query, true)
        {
            result -> getBucketLists(result, points)
        }
    }

    private fun getBucketLists(result : Any, points: Double)
    {
        val resultSet: ResultSet = result as ResultSet

        while (resultSet.next())
        {
            var bucketListItem = BucketList(
                resultSet.getInt("bl_id"),
                resultSet.getString("bl_name"),
                resultSet.getString("bl_description"),
                resultSet.getInt("bl_reqPoints")
            )
            curBucketLists.add(bucketListItem)
            loaded = true
            progressBarBucketList.visibility = View.INVISIBLE
        }
        setAdapters(points)
    }

    private fun setAdapters(points: Double)
    {
        rvBucketLists.layoutManager = LinearLayoutManager(activity)
        rvBucketLists.adapter = BucketLists_Adapter(curBucketLists, advID, points)
    }
}