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
    private var blcID : Int = 0
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

        //Select all friends name and surnames -> for recyclerView
        //Find all friends

        //select concat(adv_firstName, ' ', adv_surname) AS Name
        //from adventurers
        //
        //select adv_id2
        //FROM challenge_invites
        //WHERE adv_id1 = 6
        //AND c_id = 2

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
        progressBarViewChallenges.visibility = View.VISIBLE
        val query = "SELECT * FROM bucketlist_challenges"
        Database().runQuery(query, true)
        {
                result -> setBlcID(result)
        }
    }

    private fun setBlcID(result: Any)
    {
        val resultSet : ResultSet = result as ResultSet

        while (resultSet.next())
        {
            blcID++
        }
        blcID++

        val query = "INSERT INTO bucketlist_challenges VALUES($blcID, -1, $cID, $advID)"
        Database().runQuery(query, false)
        {
                result -> postChallenge(result)
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
       // val query = "DELETE FROM my_bucketlist WHERE c_id = $cID"
        val query = "DELETE FROM bucketlist_challenges WHERE c_id = $cID"
        progressBarViewMyChallenges.visibility = View.VISIBLE

        Database().runQuery(query, false)
        {
            result -> removeChallenge(result)
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

}