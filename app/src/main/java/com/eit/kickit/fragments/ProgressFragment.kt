package com.eit.kickit.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.eit.kickit.MainActivity
import com.eit.kickit.R
import com.eit.kickit.database.Database
import kotlinx.android.synthetic.main.activity_view_profile.*
import kotlinx.android.synthetic.main.fragment_progress.*
import java.sql.ResultSet

class ProgressFragment : Fragment() {

    private var advID : Int = -1
    private var completedChallenges : ArrayList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(container?.context).inflate(R.layout.fragment_progress, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBarProgress.visibility = View.VISIBLE
        lbl_totalSpent.visibility = View.INVISIBLE
        lbl_goldenBoots.visibility = View.INVISIBLE
        lbl_points.visibility = View.INVISIBLE
        advID = MainActivity.adventurer!!.getID()
        loadInfo(view.context)
    }

    private fun loadInfo(context: Context)
    {
        val query = "SELECT adv_totalPoints, adv_TotalSpent, adv_GoldenBootCount FROM adventurers WHERE adv_id = $advID"
        Database().runQuery(query, true)
        {
                result -> loadLabels(result)
        }

        val query2 = "SELECT c.c_id, c.c_name, c.c_points " +
                "FROM adv_challenges_completed AS a " +
                "INNER JOIN challenges AS c ON a.c_id = c.c_id " +
                "WHERE a.adv_id = $advID "
        Database().runQuery(query2, true){
                result -> loadCompletedChallenges(result, context)
        }


    }

    private fun loadCompletedChallenges(result : Any, context: Context)
    {
        if(result is ResultSet){

            while(result.next()){

                val display = result.getString("c_name") + " \t\t\t " + result.getString("c_points")
                completedChallenges.add(display)
            }

            val adapter = ArrayAdapter(context, R.layout.spin_item, completedChallenges)
            completed_myChallenges.adapter = adapter
            progressBarProgress.visibility = View.INVISIBLE
        }
        else{
            println("SOMETHING WENT WRONG --------" + result)
        }

    }

    private fun loadLabels(result : Any)
    {
        if (result is ResultSet)
        {
            if (result.next())
            {
                val points = result.getDouble("adv_totalPoints")
                val spent = result.getDouble("adv_TotalSpent")
                val goldenBoots = result.getInt("adv_GoldenBootCount")
                lbl_points.setText("$points")
                lbl_goldenBoots.setText("$goldenBoots")
                lbl_totalSpent.setText("$spent")

                lbl_points.visibility = View.VISIBLE
                lbl_goldenBoots.visibility = View.VISIBLE
                lbl_totalSpent.visibility = View.VISIBLE
            }
        } else
        {
            println("------------------------------------------>" + result)
        }
    }
}