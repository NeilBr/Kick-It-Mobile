package com.eit.kickit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eit.kickit.adapters.Challenge_Adapter
import com.eit.kickit.R
import com.eit.kickit.models.BucketList
import com.eit.kickit.models.Challenge
import kotlinx.android.synthetic.main.activity_challenges.*
import kotlinx.android.synthetic.main.recycler_view_item.view.*
import java.util.concurrent.ConcurrentHashMap

class ChallengesListActivity : AppCompatActivity() {

    private var bucketlists : ArrayList<BucketList> = ArrayList()
    private var blID : Int = -1
    private val temp : ArrayList<Challenge> = ArrayList()
    private val curChallenges : ArrayList<Challenge> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenges)

        bucketlists = getIntent().getParcelableArrayListExtra("BucketLists");
        blID = getIntent().getIntExtra("ID", 0)

        val c1 = Challenge(1, "Walk 5km", "Take a stroll", 1, 0.00, false, 0)
        val c2 = Challenge(2, "Waltz backwards", "Dance tings innit", 50, 0.00, false, 1)
        val c3 = Challenge(3,"Walk 10km", "Take a hike", 69, 0.00, false, 1)
        val c4 = Challenge(4,"Attempt a test", "Try pass", 10000, 0.00, false, 2)
        val c5 = Challenge(5, "Eat bread for supper", "Fight off poverty", 5, 0.00, false, 2)
        val c6 = Challenge(6, "Drink Cocktails", "Drink exotic drinks", 500, 250.00, false, 3)
        val c7 = Challenge(7, "Run 15km", "You wanna be fit?", 100, 0.00, false, 4)
        val c8 = Challenge(8, "Ignore Assignments", "Just accept the laziness", 275, 0.00, false, 5)

        temp.add(c1)
        temp.add(c2)
        temp.add(c3)
        temp.add(c4)
        temp.add(c5)
        temp.add(c6)
        temp.add(c7)
        temp.add(c8)

        while (temp.size != 0)
        {
            val cur = temp.get(0)
            if (cur.blID == blID)
            {
                curChallenges.add(cur)
            }
            temp.removeAt(0)
        }

        rvChallenges.layoutManager = LinearLayoutManager(this)
        rvChallenges.adapter = Challenge_Adapter(curChallenges)
    }
}