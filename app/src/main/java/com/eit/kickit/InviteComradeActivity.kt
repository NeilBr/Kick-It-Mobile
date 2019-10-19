package com.eit.kickit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.eit.kickit.database.Database
import com.eit.kickit.models.Adventurer
import kotlinx.android.synthetic.main.activity_invite_comrade.*
import java.sql.ResultSet

class InviteComradeActivity : AppCompatActivity() {

    private val friendNames : ArrayList<String> = ArrayList()
    private val friendIDs : ArrayList<Int> = ArrayList()
    private var inviteName : String = ""
    private var spinFriend : Spinner? = null
    private var advID : Int = -1
    private var advID2 : Int = -1
    private var cID : Int = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_comrade)

        advID = intent.getIntExtra("advID", 0)
        cID = intent.getIntExtra("cID", 0)
        spinFriend = findViewById(R.id.friendSpinner)

        progressBarViewInviteComrade.visibility = View.VISIBLE
/*
        val query = "SELECT adv_id, adv_firstName, adv_surname " +
                "FROM adventurers AS a INNER JOIN friends AS f ON a.adv_id = f.adv_id2 " +
                "WHERE f.adv_id1 = ${advID} "

        Database().runQuery(query, true){
               // result -> getFriends(result)
        }
*/
        val query2 = "SELECT concat(adv_firstName, ' ', adv_surname) AS Name, adv_id " +
                "FROM adventurers AS a INNER JOIN friends AS f ON a.adv_id = f.adv_id2 " +
                "WHERE f.adv_id1 = ${advID} "
        Database().runQuery(query2, true)
        {
                result -> getFriendNames(result)
        }
    }

    private fun getFriends(result: Any)
    {

    }

    private fun getFriendNames(result: Any)
    {
        val resultSet : ResultSet = result as ResultSet

        while (resultSet.next())
        {
            friendNames.add(resultSet.getString("Name"))
            friendIDs.add(resultSet.getInt("adv_id"))
        }

        progressBarViewInviteComrade.visibility = View.INVISIBLE
        setUpSpinner()
    }

    private fun setUpSpinner() {
        spinFriend!!.adapter = ArrayAdapter(this, R.layout.spin_item, friendNames)
        spinFriend?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val fullName = parent?.getItemAtPosition(position).toString()
                inviteName = fullName
                var pos = 0
                var counter = 0
                for (x  in friendNames)
                {
                    if (x.equals(inviteName))
                    {
                        pos = counter
                    }
                    counter++
                }
                advID2 = friendIDs.get(pos)
            }
        }
    }

    fun onInviteClick(view : View)
    {
        progressBarViewInviteComrade.visibility = View.VISIBLE
        val query = "INSERT INTO challenge_invites(adv_id1, adv_id2, c_id, ci_status) VALUES($advID, $advID2, $cID, false)"
        Database().runQuery(query, false)
        {
            result ->
            if (result is String)
            {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                progressBarViewInviteComrade.visibility = View.INVISIBLE
            }
            else
            {
                 Toast.makeText(this, "Invite has been sent!", Toast.LENGTH_SHORT).show()
                progressBarViewInviteComrade.visibility = View.INVISIBLE
            }
        }
    }
}
