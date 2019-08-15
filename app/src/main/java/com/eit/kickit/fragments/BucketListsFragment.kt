package com.eit.kickit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.eit.kickit.Adapters.BucketLists_Adapter

import com.eit.kickit.R
import com.eit.kickit.models.BucketList
import kotlinx.android.synthetic.main.activity_bucket_lists.*

/**
 * Sets up the recycler view for the layout.
 * The layout connected to this fragment displays all the bucket lists in the application.
 * Selected a bucket list will take the user to a new layout with all the challenges of that bucket list
 *
 * TO DO: Set on click of bucket lists to take to challanges layout(and initialise challenge frag)
 */
class BucketListsFragment : Fragment() {

    private val bucketlists : ArrayList<BucketList> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b1 = BucketList(0, "Easy", "First BucketList", 0)
        val b2 = BucketList(1,"Medium", "Getting interesting", 25)
        val b3 = BucketList(2, "Memer", "Do it for the vine", 420)
        bucketlists.add(b1)
        bucketlists.add(b2)
        bucketlists.add(b3)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return LayoutInflater.from(container?.context).inflate(R.layout.activity_bucket_lists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvBucketLists.layoutManager = LinearLayoutManager(activity)
        rvBucketLists.adapter = BucketLists_Adapter(bucketlists)
        }
    }


