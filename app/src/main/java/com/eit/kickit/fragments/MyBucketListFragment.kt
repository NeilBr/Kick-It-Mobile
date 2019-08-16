package com.eit.kickit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eit.kickit.R
import com.eit.kickit.adapters.Challenge_Adapter
import com.eit.kickit.adapters.MyBucketList_Adapter
import com.eit.kickit.models.Challenge
import kotlinx.android.synthetic.main.activity_challenges.*
import kotlinx.android.synthetic.main.fragment_my_bucket_list.*

class MyBucketListFragment : Fragment() {

    private val temp : ArrayList<Challenge> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(container?.context).inflate(R.layout.fragment_my_bucket_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val c1 = Challenge(1, "Walk 5km", "Take a stroll", 1, 0.00, false, 0)
        val c2 = Challenge(2, "Waltz backwards", "Dance tings innit", 50, 0.00, false, 2)
        val c3 = Challenge(3,"Clap twice", "Meme Review", 69, 10000.00, false, 2)

        temp.add(c1)
        temp.add(c2)
        temp.add(c3)

        rvMyBucketList.layoutManager = LinearLayoutManager(activity)
        rvMyBucketList.adapter = MyBucketList_Adapter(temp)
    }

}