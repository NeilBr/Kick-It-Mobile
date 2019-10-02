package com.eit.kickit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eit.kickit.common.FileHandler
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


}