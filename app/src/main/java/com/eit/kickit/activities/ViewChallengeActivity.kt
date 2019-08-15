package com.eit.kickit.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eit.kickit.R

class ViewChallengeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_challenge)
    }

    fun onAddClick(view : View)
    {
        Toast.makeText(this,"Added to My BucketList", Toast.LENGTH_SHORT).show()
    }
}