package com.eit.kickit.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eit.kickit.Adapters.Challenge_Adapter
import com.eit.kickit.R
import com.eit.kickit.models.Challenge
import kotlinx.android.synthetic.main.activity_challenges.*

class ChallengesListActivity : AppCompatActivity() {

    private val temp : ArrayList<Challenge> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenges)

        val c1 = Challenge(1, "Walk 5km", "Take a stroll", 1, 0.00, false, 0)
        val c2 = Challenge(2, "Waltz backwards", "Dance tings innit", 50, 0.00, false, 2)
        val c3 = Challenge(3,"Clap twice", "Meme Review", 69, 10000.00, false, 2)

        temp.add(c1)
        temp.add(c2)
        temp.add(c3)

        rvChallenges.layoutManager = LinearLayoutManager(this)
        rvChallenges.adapter = Challenge_Adapter(temp)
    }
}