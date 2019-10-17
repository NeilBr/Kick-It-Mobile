package com.eit.kickit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eit.kickit.database.Database
import kotlinx.android.synthetic.main.activity_view_challenge.*
import kotlinx.android.synthetic.main.activity_view_my_challenge.*
import java.sql.ResultSet

class ViewChallengeActivity : AppCompatActivity() {

    private var cID : Int = -1
    private var cName : String = ""
    private var cDesc : String = ""
    private var cPoints : Int = -1
    private var cPrice : Double = 0.00
    private var cStatus : Boolean = false
    private var blID : Int = -1
    private var advID : Int = -1
    private val comradeID : ArrayList<Int> = ArrayList()
    private val comradeNames : ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutV = getIntent().getStringExtra("MyChallenge")
        cID = getIntent().getIntExtra("cID", 0)
        cName = getIntent().getStringExtra("Name")
        cDesc = getIntent().getStringExtra("Description")
        cPoints = getIntent().getIntExtra("Points", 0)
        cPrice = getIntent().getDoubleExtra("Price", 0.00)
        cStatus = getIntent().getBooleanExtra("Status", false)
        blID = getIntent().getIntExtra("blID", -1)
        advID = getIntent().getIntExtra("advID", -1)


        if (layoutV.equals("View my challenge layout"))
        {
            setContentView(R.layout.activity_view_my_challenge)
            loadMyChallengeView()
        }
        else if (layoutV.equals("View challenge layout"))
        {
            setContentView(R.layout.activity_view_challenge)
            loadChallengeView()
        }
    }

    private fun loadMyChallengeView()
    {
        txtNameMy.setText(cName)
        txtDescrMy.setText(cDesc)
        txtPointsMy.setText("${cPoints}")
        txtPriceMy.setText("${cPrice}")
        progressBarViewMyChallenges.visibility = View.VISIBLE

        val query = "SELECT adv_id2 FROM challenge_invites WHERE adv_id1 = $advID AND c_id = $cID"
        Database().runQuery(query, true)
        {
            result -> getComradeIDs(result)
        }


    }

    private fun getComradeIDs(result : Any)
    {
        val resultSet : ResultSet = result as ResultSet

        while (resultSet.next())
        {
            comradeID.add(resultSet.getInt("adv_id2"))
        }

        for (x in comradeID)
        {
            progressBarViewMyChallenges.visibility = View.VISIBLE
            val query = "SELECT concat(adv_firstName, ' ', adv_surname) AS Name FROM adventurers WHERE adv_id = $x"
            Database().runQuery(query, true)
            {
                    result -> getComradeNames(result)
            }
        }

    }

    private fun getComradeNames(result: Any)
    {
        val resultSet : ResultSet = result as ResultSet

        while (resultSet.next())
        {
            comradeNames.add(resultSet.getString("Name"))
        }
        listComrades.adapter = ArrayAdapter(this, R.layout.spin_item, comradeNames)
        progressBarViewMyChallenges.visibility = View.INVISIBLE

    }

    private fun loadChallengeView()
    {
        txtName.setText(cName)
        txtDescr.setText(cDesc)
        txtPoints.setText("${cPoints}")
        txtPrice.setText("${cPrice}")
    }

    fun onAddClick(view : View)
    {
        //Check if challenge is already added
        progressBarViewChallenges.visibility = View.VISIBLE
        val query = "SELECT * FROM bucketlist_challenges WHERE adv_id = $advID"
        Database().runQuery(query, true)
        {
            result -> checkIfAdded(result)
        }
    }

    private fun checkIfAdded(result : Any)
    {
        var found = false
        val resultSet : ResultSet = result as ResultSet
        while (resultSet.next())
        {
            val curCID = resultSet.getInt("c_id")
            if (curCID == cID)
            {
                found = true
            }
        }
        if (found)
        {
            Toast.makeText(this, "Challenge already added to MyBucketList", Toast.LENGTH_SHORT).show()
            progressBarViewChallenges.visibility = View.INVISIBLE
        }
        else if (!found)
        {
            //Check if already completed
            val query = "SELECT * FROM adv_challenges_completed WHERE adv_id = $advID"
            Database().runQuery(query, true)
            {
                result -> checkIfCompleted(result)
            }
        }
    }

    private fun checkIfCompleted(result : Any)
    {
        var completed = false
        val resultSet : ResultSet = result as ResultSet
        while (resultSet.next())
        {
            val curCID = resultSet.getInt("c_id")
            if (curCID == cID)
            {
                completed = true
            }
        }
        if (completed)
        {
            Toast.makeText(this, "Challenge already been completed", Toast.LENGTH_SHORT).show()
            progressBarViewChallenges.visibility = View.INVISIBLE
        }
        else if (!completed)
        {
            val query = "INSERT INTO bucketlist_challenges(bl_id, c_id, adv_id) VALUES(-1, $cID, $advID)"
            Database().runQuery(query, false)
            {
                    result -> postChallenge(result)
            }
        }
    }

    private fun postChallenge(result : Any)
    {
        if (result is String)
        {
            progressBarViewMyChallenges.visibility = View.INVISIBLE
            Toast.makeText(this,result, Toast.LENGTH_LONG).show()
        }
        else
        {
            progressBarViewChallenges.visibility = View.INVISIBLE
            Toast.makeText(this,"Added to My BucketList", Toast.LENGTH_SHORT).show()
        }
    }

    fun onRemoveClick(view : View)
    {
        //Delete the correct challenge -> get blc_id
        progressBarViewMyChallenges.visibility = View.VISIBLE
        val query = "Select blc_id FROM bucketlist_challenges WHERE c_id = $cID AND adv_id = $advID"
        Database().runQuery(query, true)
        {
            result -> getBLCID(result)
        }
    }

    private fun getBLCID(result : Any)
    {
       val resultSet : ResultSet = result as ResultSet

        if (resultSet.next())
        {
            val blcID = resultSet.getInt("blc_id")
            val query = "DELETE FROM bucketlist_challenges WHERE c_id = $cID AND blc_id = $blcID"
            Database().runQuery(query, false)
            {
                    result -> removeChallenge(result)
            }
        }
    }

    private fun removeChallenge(result : Any)
    {
        if (result is String)
        {
            progressBarViewMyChallenges.visibility = View.INVISIBLE
            Toast.makeText(this,result, Toast.LENGTH_LONG).show()
        }
        else
        {
            progressBarViewMyChallenges.visibility = View.INVISIBLE
            Toast.makeText(this,"Removed to My BucketList", Toast.LENGTH_SHORT).show()
        }
    }

    fun onInviteClick(view : View)
    {
        val intent : Intent =  Intent(this, InviteComradeActivity::class.java)
        intent.putExtra("advID", advID)
        startActivity(intent)
    }

    fun onCompleteClick(view : View)
    {
        //When a challenge is completed ->  Complete the post
        //                                  In my bucketlist <- remove

        Toast.makeText(this, "Code here", Toast.LENGTH_SHORT).show()
    }

}