package com.eit.kickit.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eit.kickit.R

class ViewChallengeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutV = getIntent().getStringExtra("MyChallenge")
        if (layoutV.equals("View my challenge layout"))
        {
            setContentView(R.layout.activity_view_my_challenge)
        }
        else if (layoutV.equals("View challenge layout"))
        {
            setContentView(R.layout.activity_view_challenge)
        }

        val lblPrice : TextView = findViewById(R.id.txtPrice)
        val lblDescr : TextView = findViewById(R.id.txtDescr)
        val lblPoints : TextView = findViewById(R.id.txtPoints)

        var Price : Double = getIntent().getDoubleExtra("Price", 0.00)
        var Points : Int = getIntent().getIntExtra("Points", 0)
        var Descr : String = getIntent().getStringExtra("Description")

        lblPrice.text = Price.toString()
        lblDescr.text = Descr
        lblPoints.text = Points.toString()
    }

    fun onAddClick(view : View)
    {
        Toast.makeText(this,"Added to My BucketList", Toast.LENGTH_SHORT).show()
    }

    fun onRemoveClick(view : View)
    {
        Toast.makeText(this,"Removed to My BucketList", Toast.LENGTH_SHORT).show()
    }
}