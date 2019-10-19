package com.eit.kickit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.eit.kickit.R
import com.eit.kickit.database.Database
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_suggest_challenge.*
import java.sql.ResultSet

class SuggestChallengeFragment : Fragment(){

    private var txtName : TextInputEditText? = null
    private var txtDesc : TextInputEditText? = null
    private var txtPoints : TextInputEditText? = null
    private var txtPrice : TextInputEditText? = null
    private var spinBucket : Spinner? = null

    private var scID : Int = 0
    private var scName : String = ""
    private var scDesc : String = ""
    private var scPoints : Int = 0
    private var scStatus : Int = 0
    private var scBucket : String = ""

    private val bucketlists : ArrayList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_suggest_challenge, container, false)

        val btnSuggestChallenge = view.findViewById<Button>(R.id.btnSuggest)
        btnSuggestChallenge.setOnClickListener{
            suggestChallenge()
        }

        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            cancelSuggestion()
        }

        txtName = view.findViewById(R.id.challengeName)
        txtDesc = view.findViewById(R.id.challengeDescription)
        txtPoints = view.findViewById(R.id.challengePoints)
        txtPrice = view.findViewById(R.id.challengePrice)
        spinBucket = view.findViewById(R.id.bucketSpinner)

        //Get all bucketlists
        val query = "SELECT * FROM bucketlists"
        Database().runQuery(query, true)
        {
            result -> loadBucketChoices(result)
        }

        return view
    }

    private fun loadBucketChoices(result: Any)
    {
        val resultSet: ResultSet = result as ResultSet

        while(resultSet.next())
        {
            bucketlists.add(resultSet.getString("bl_name"))
        }

        spinBucket?.onItemSelectedListener
        spinBucket!!.adapter = ArrayAdapter(activity!!.applicationContext, R.layout.spin_item, bucketlists)
        spinBucket?.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent?.getItemAtPosition(position).toString()
                scBucket = type
            }

        }
    }


    private fun suggestChallenge()
    {
        progressBarSuggest.visibility = View.VISIBLE

        scName = txtName!!.text.toString()
        scDesc = txtDesc!!.text.toString()
        scPoints = Integer.parseInt(txtPoints!!.text.toString())
        scStatus = 0
        val scPrice = txtPrice!!.text
        val query = "INSERT INTO suggested_challenges(sc_name, sc_description, sc_points, sc_price, sc_bucketlist, sc_status) VALUES('$scName','$scDesc',$scPoints,$scPrice, '$scBucket', $scStatus)"

        Database().runQuery(query, false)
        {
                result -> sendSuggestion(result)
        }
    }

    private fun sendSuggestion(result : Any)
    {
        if (result is String)
        {
            Toast.makeText(this@SuggestChallengeFragment.context,result, Toast.LENGTH_LONG).show()
        }
        else
        {
            Toast.makeText(this@SuggestChallengeFragment.context,"Suggestion sent", Toast.LENGTH_SHORT).show()
        }

        progressBarSuggest.visibility = View.INVISIBLE
    }

    private fun cancelSuggestion()
    {
        challengeName.setText("")
        challengeDescription.setText("")
        challengePoints.setText("")
        challengePrice.setText("")
    }
}

