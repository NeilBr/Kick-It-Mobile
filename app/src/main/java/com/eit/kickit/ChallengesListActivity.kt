package com.eit.kickit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eit.kickit.adapters.Challenge_Adapter
import com.eit.kickit.database.Database
import com.eit.kickit.models.Challenge
import kotlinx.android.synthetic.main.activity_challenges.*
import java.sql.ResultSet

class ChallengesListActivity : AppCompatActivity() {

  //  private var bucketlists : ArrayList<BucketList> = ArrayList()
    private var blID : Int = -1
    private var blName : String = ""
    private val temp : ArrayList<Challenge> = ArrayList()
    private val curChallenges : ArrayList<Challenge> = ArrayList()
   // private var loaded : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenges)

      //  bucketlists = getIntent().getParcelableArrayListExtra("BucketLists");
        blID = getIntent().getIntExtra("ID", 0)
        blName = getIntent().getStringExtra("Name")
/*
        val c1 = Challenge(1, "Walk 5km", "Take a stroll", 1, 0.00, false, 0)
        val c2 = Challenge(2, "Waltz backwards", "Dance tings innit", 50, 0.00, false, 1)
        val c3 = Challenge(3,"Walk 10km", "Take a hike", 69, 0.00, false, 1)
        val c4 = Challenge(4,"Attempt a test", "Try pass", 10000, 0.00, false, 2)
        val c5 = Challenge(5, "Eat bread for supper", "Fight off poverty", 5, 0.00, false, 2)
        val c6 = Challenge(6, "Drink Cocktails", "Drink exotic drinks", 500, 250.00, false, 3)
        val c7 = Challenge(7, "Run 15km", "You wanna be fit?", 100, 0.00, false, 4)
        val c8 = Challenge(8, "Ignore Assignments", "Just accept the laziness", 275, 0.00, false, 5)
*/
        lbl_curBL.setText(blName)
        loadChallenges()
    }

    private fun loadChallenges()
    {
        val query = "SELECT * FROM challenges"

        progressBarChallenges.visibility = View.VISIBLE

        Database().runQuery(query, true)
        {
                result -> getChallenges(result)
        }
    }

    private fun getChallenges(result: Any)
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
            temp.add(challengeItem)
          //  loaded = true
            progressBarChallenges.visibility = View.INVISIBLE
        }
        loadRelevantChallenges()
        setAdapters()
    }

    private fun loadRelevantChallenges()
    {
        while (temp.size != 0)
        {
            val cur = temp.get(0)
            if (cur.blID == blID)
            {
                curChallenges.add(cur)
            }
            temp.removeAt(0)
        }
    }

    private fun setAdapters()
    {
        rvChallenges.layoutManager = LinearLayoutManager(this)
        rvChallenges.adapter = Challenge_Adapter(curChallenges)
    }
}