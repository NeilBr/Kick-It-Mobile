package com.eit.kickit

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.eit.kickit.common.FileHandler
import com.eit.kickit.database.Database
import kotlinx.android.synthetic.main.activity_create_profile.*
import kotlinx.android.synthetic.main.activity_view_challenge.*
import kotlinx.android.synthetic.main.activity_view_my_challenge.*
import java.io.InputStream
import java.sql.ResultSet

class ViewChallengeActivity : AppCompatActivity() {

    private var cID : Int = -1
    private var cName : String = ""
    private var cDesc : String = ""
    private var cPoints : Double = 0.00
    private var cPrice : Double = 0.00
    private var cStatus : Boolean = false
    private var blID : Int = -1
    private var advID : Int = -1
    private var advName : String = ""
    private var advSurname : String = ""
    private var blcID : Int = 0
    private val comradeNames : ArrayList<String> = ArrayList()
    private var uri: Uri? = Uri.EMPTY
    lateinit var pic: InputStream

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val layoutV = getIntent().getStringExtra("MyChallenge")
        cID = getIntent().getIntExtra("cID", 0)
        cName = getIntent().getStringExtra("Name")
        cDesc = getIntent().getStringExtra("Description")
        cPoints = getIntent().getDoubleExtra("Points", 0.00)
        cPrice = getIntent().getDoubleExtra("Price", 0.00)
        cStatus = getIntent().getBooleanExtra("Status", false)
        blID = getIntent().getIntExtra("blID", -1)
        advID = getIntent().getIntExtra("advID", -1)
        advName = MainActivity.adventurer!!.advFirstName
        advSurname = MainActivity.adventurer!!.advSurname

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

        val query = "SELECT concat(a.adv_firstname, ' ', a.adv_surname) As Name FROM adventurers as a INNER JOIN challenge_invites as c ON a.adv_id = c.adv_id2 WHERE c.c_id = $cID AND c.adv_id1 = $advID and c.ci_status = true"
        Database().runQuery(query, true)
        {
            result -> loadComrades(result)
        }

    }

    private fun loadComrades(result : Any)
    {
        val resultSet : ResultSet = result as ResultSet
        if (result is String)
        {
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            println("-----------------------------------------------> " + result)
            progressBarViewMyChallenges.visibility = View.INVISIBLE
        }
        else
        {
            while (resultSet.next())
            {
                comradeNames.add(resultSet.getString("Name"))
            }
            listComrades.adapter = ArrayAdapter(this, R.layout.spin_item, comradeNames)
            progressBarViewMyChallenges.visibility = View.INVISIBLE
        }
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
            val query = "SELECT * FROM bucketlist_challenges"
            Database().runQuery(query, true)
            {
                result -> setBlcID(result)
            }
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
            progressBarViewChallenges.visibility = View.INVISIBLE
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
        progressBarViewMyChallenges.visibility = View.VISIBLE
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
        intent.putExtra("cID", cID)
        startActivity(intent)
    }

    fun onCompleteClick(view : View)
    {
        //Remove the challenge
        progressBarViewMyChallenges.visibility = View.VISIBLE
        val query = "Select blc_id FROM bucketlist_challenges WHERE c_id = $cID AND adv_id = $advID"
        Database().runQuery(query, true)
        {
                result -> getBLCID(result)
        }

        //Add to completed challenge
        val query2 = "INSERT INTO adv_challenges_completed(adv_id, c_id) VALUES($advID, $cID)"
        Database().runQuery(query2, false)
        {
            result ->
            if (result is String)
            {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                progressBarViewMyChallenges.visibility = View.INVISIBLE
            }
            else
            {
                Toast.makeText(this, "Added to completed challenges", Toast.LENGTH_SHORT).show()
                progressBarViewMyChallenges.visibility = View.INVISIBLE
            }
        }

        //Allocate points and cost to adventurer
        val query3 = "SELECT adv_totalPoints, adv_TotalSpent FROM adventurers WHERE adv_id = $advID"
        Database().runQuery(query3, true)
        {
            result -> updateProfile(result)
        }

        //Post to S3
        //Add to post table
        selectPicture()
    }

    private fun updateProfile(result: Any)
    {
        if (result is ResultSet)
        {
            progressBarViewMyChallenges.visibility = View.VISIBLE
            if (result.next())
            {
                val advTP = result.getDouble("adv_totalPoints") + cPoints
                val advTS = result.getDouble("adv_TotalSpent") + cPrice
                val query = "UPDATE adventurers SET adv_totalPoints = $advTP, adv_TotalSpent = $advTS WHERE adv_id = $advID"
                Database().runQuery(query, false)
                {
                    result ->
                    if (result is String)
                    {
                        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(this, "Points and total spent has been updated", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun selectPicture(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        progressBarViewMyChallenges.visibility = View.VISIBLE
        if(resultCode == Activity.RESULT_OK && requestCode == 1000){
            uri = data!!.data
            val fileName = "$advID" + "_" + "$cID" + "_" + "$cName"
            FileHandler(this).uploadFile(uri, fileName, false)

            val caption = advName + " " + advSurname + ": " + cName
            val query = "INSERT INTO posts(adv_id, p_caption, p_status, p_photoUrl, p_likes) VALUES ($advID, '$caption', 0, '$fileName', 0)"
            Database().runQuery(query, false)
            {
                result ->
                if (result is String)
                {
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                    progressBarViewMyChallenges.visibility = View.INVISIBLE
                }
                else
                {
                    Toast.makeText(this, "Post has been uploaded!", Toast.LENGTH_SHORT).show()
                    progressBarViewMyChallenges.visibility = View.INVISIBLE
                }
            }
        }
    }

}