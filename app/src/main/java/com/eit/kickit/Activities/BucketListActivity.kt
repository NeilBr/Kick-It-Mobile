package com.eit.kickit.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eit.kickit.Adapters.BucketLists_Adapter
import com.eit.kickit.R
import com.eit.kickit.models.BucketList
import kotlinx.android.synthetic.main.activity_bucket_lists.*

class BucketListActivity : AppCompatActivity() {

    private val bucketlists : ArrayList<BucketList> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bucket_lists)
        val b1 = BucketList(0, "Easy", "First BucketList", 0)
        val b2 = BucketList(1,"Medium", "Getting interesting", 25)
        val b3 = BucketList(2, "Memer", "Do it for the vine", 420)
        bucketlists.add(b1)
        bucketlists.add(b2)
        bucketlists.add(b3)

        rvBucketLists.layoutManager = LinearLayoutManager(this)
        rvBucketLists.adapter = BucketLists_Adapter(bucketlists)
    }

}