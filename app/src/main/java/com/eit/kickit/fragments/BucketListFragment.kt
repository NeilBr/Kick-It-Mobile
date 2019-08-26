package com.eit.kickit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eit.kickit.R
import com.eit.kickit.adapters.BucketLists_Adapter
import com.eit.kickit.models.BucketList
import kotlinx.android.synthetic.main.fragment_bucket_lists.*

class BucketListFragment : Fragment() {
    private val temp : ArrayList<BucketList> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(container?.context).inflate(R.layout.fragment_bucket_lists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val c1 = BucketList(0, "Beginner","The Starter List for any adveturer", 0)
        val c2 = BucketList(1, "Intermediate", "For adventurers wanting a challenge", 500)
        val c3 = BucketList(2, "Student", "Just Student struggles", 750)
        val c4 = BucketList(3, "Exotic", "For those who want a taste of strange", 1000)
        val c5 = BucketList(4, "Fitness" ,"Let's hit that swol grind", 250)
        val c6 = BucketList(5, "Lazy", "For the more relaxed", 120)

        temp.add(c1)
        temp.add(c2)
        temp.add(c3)
        temp.add(c4)
        temp.add(c5)
        temp.add(c6)

        rvBucketLists.layoutManager = LinearLayoutManager(activity)
        rvBucketLists.adapter = BucketLists_Adapter(temp)
    }
}