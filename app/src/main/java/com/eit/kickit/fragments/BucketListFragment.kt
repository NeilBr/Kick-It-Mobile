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
        val c1 = BucketList(0, "Test","Testty", 500)
        temp.add(c1)

        rvBucketLists.layoutManager = LinearLayoutManager(activity)
        rvBucketLists.adapter = BucketLists_Adapter(temp)
    }
}