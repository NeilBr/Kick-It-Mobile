package com.eit.kickit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eit.kickit.adapters.Challenge_Adapter
import com.eit.kickit.database.Database
import com.eit.kickit.models.Challenge
import kotlinx.android.synthetic.main.activity_challenges.*
import java.sql.ResultSet

class ChallengesListActivity : AppCompatActivity() {

    private var blID : Int = -1
    private var blName : String = ""
    private var blDesc : String = ""
    private var blReqP : Int = -1
    private val temp : ArrayList<Challenge> = ArrayList()
    private val curChallenges : ArrayList<Challenge> = ArrayList()
    private var advID : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenges)
        blID = getIntent().getIntExtra("ID", 0)
        blName = getIntent().getStringExtra("Name")
        blDesc = getIntent().getStringExtra("Desc")
        blReqP = getIntent().getIntExtra("reqPoints", -1)
        advID = getIntent().getIntExtra("advID", -1)

        lbl_curBL.setText(blName)
        lbl_Description.setText(blDesc)
        lbl_reqPoints.setText("$blReqP")
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
                var challengeItem = Challenge(
                    resultSet.getInt("c_id"),
                    resultSet.getString("c_name"),
                    resultSet.getString("c_description"),
                    resultSet.getDouble("c_points"),
                    resultSet.getDouble("c_price"),
                    status
                )
                temp.add(challengeItem)
            }
        }
      //  val query = "Select * FROM bucketlist_challenges WHERE bl_id = ${blID}"

        val query = "Select * FROM bucketlist_challenges as bc INNER JOIN challenges as c ON bc.c_id  = c.c_id WHERE bc.bl_id = ${blID} AND c.c_status = true"

        Database().runQuery(query, true)
        {
                result -> loadRelevantChallenges(result)
        }

    }

    private fun loadRelevantChallenges(result : Any)
    {
        val resultSet: ResultSet = result as ResultSet
        while (resultSet.next())
        {
            var size : Int = temp.size
            var pos : Int = 0
            while (size != 0)
            {
                val cur = temp.get(pos)
                val curID = resultSet.getInt("c_id")
                if (cur.cID == curID)
                {
                    curChallenges.add(cur)
                }
                pos++
                size--
            }
        }
        progressBarChallenges.visibility = View.INVISIBLE
        setAdapters()
    }


    private fun setAdapters()
    {
        rvChallenges.layoutManager = LinearLayoutManager(this)
        rvChallenges.adapter = Challenge_Adapter(curChallenges, advID, blID)
    }
}