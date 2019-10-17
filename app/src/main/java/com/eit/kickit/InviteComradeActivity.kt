package com.eit.kickit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.eit.kickit.database.Database
import com.eit.kickit.models.Adventurer
import kotlinx.android.synthetic.main.activity_invite_comrade.*
import java.sql.ResultSet

class InviteComradeActivity : AppCompatActivity() {

    private val friendNames : ArrayList<String> = ArrayList()
    private val friends : ArrayList<Adventurer> = ArrayList()
    private var inviteName : String = ""
    private var spinFriend : Spinner? = null
    private var advID : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_comrade)

        advID = intent.getIntExtra("advID", 0)
        spinFriend = findViewById(R.id.friendSpinner)

        progressBarViewInviteComrade.visibility = View.VISIBLE
        val query = "SELECT adv_id, adv_firstName, adv_surname, adv_email, adv_telephone, adv_totalPoints, adv_profilepic " +
                "FROM adventurers AS a INNER JOIN friends AS f ON a.adv_id = f.adv_id2 " +
                "WHERE f.adv_id1 = ${advID} "

        Database().runQuery(query, true){
                result -> getFriends(result)
        }

        val query2 = "SELECT concat(adv_firstName, ' ', adv_surname) AS Name " +
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
            }
        }
    }

    fun onInviteClick(view : View)
    {
        val firstName : String
        val query = "SELECT adv_id FROM adventurers WH"
    }
}
