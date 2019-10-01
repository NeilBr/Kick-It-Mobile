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
        /*
        var c1 = BucketList(0, "Beginner","The Starter List for any adveturer", 0)
        var c2 = BucketList(1, "Intermediate", "For adventurers wanting a challenge", 500)
        var c3 = BucketList(2, "Student", "Just Student struggles", 750)
        var c4 = BucketList(3, "Exotic", "For those who want a taste of strange", 1000)
        var c5 = BucketList(4, "Fitness" ,"Let's hit that swol grind", 250)
        var c6 = BucketList(5, "Lazy", "For the more relaxed", 120)
*/
        advID = arguments!!.getInt("advID")
        loadBucketLists()
    }

    private fun loadBucketLists()
    {
        val query = "SELECT * FROM bucketlists"

        progressBarBucketList.visibility = View.VISIBLE

        Database().runQuery(query, true)
        {
            result -> getBucketLists(result)
        }
    }

    private fun getBucketLists(result : Any)
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
        setAdapters()
    }

    private fun setAdapters()
    {
        rvBucketLists.layoutManager = LinearLayoutManager(activity)
        rvBucketLists.adapter = BucketLists_Adapter(curBucketLists, advID)
    }
}