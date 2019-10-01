package com.eit.kickit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eit.kickit.common.FileHandler
import com.eit.kickit.database.Database
import kotlinx.android.synthetic.main.activity_view_challenge.*
import kotlinx.android.synthetic.main.activity_view_my_challenge.*

class ViewChallengeActivity : AppCompatActivity() {

    private var cID : Int = -1
    private var cName : String = ""
    private var cDesc : String = ""
    private var cPoints : Int = -1
    private var cPrice : Double = 0.00
    private var cStatus : Boolean = false
    private var bl_id : Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutV = getIntent().getStringExtra("MyChallenge")
        cID = getIntent().getIntExtra("cID", 0)
        cName = getIntent().getStringExtra("Name")
        cDesc = getIntent().getStringExtra("Description")
        cPoints = getIntent().getIntExtra("Points", 0)
        cPrice = getIntent().getDoubleExtra("Price", 0.00)
        cStatus = getIntent().getBooleanExtra("Status", false)
       // bl_id = getIntent().getIntExtra("blID", 0)
        bl_id = 99


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
       // val query = "INSERT INTO my_bucketlist VALUES($cID, '$cName', '$cDesc', $cPoints, $cPrice, $cStatus, $bl_id)"
        val query = "INSERT INTO bucketlist_challenges VALUES($bl_id, $cID, null)"
        progressBarViewChallenges.visibility = View.VISIBLE

        Database().runQuery(query, false)
        {
            result -> postChallenge(result)
        }
    }

    private fun postChallenge(result : Any)
    {
        if (result is String)
        {
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
            Toast.makeText(this,result, Toast.LENGTH_LONG).show()
        }
        else
        {
            progressBarViewMyChallenges.visibility = View.INVISIBLE
            Toast.makeText(this,"Removed to My BucketList", Toast.LENGTH_SHORT).show()
        }
    }
}