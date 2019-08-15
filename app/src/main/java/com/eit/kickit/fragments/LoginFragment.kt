package com.eit.kickit.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.eit.kickit.activities.CreateProfileActivity
import com.eit.kickit.R

class LoginFragment : Fragment() {

    private val CREATE_PROFILE_RESULT = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_login, container, false)

        val btnASignUp = view.findViewById<Button>(R.id.btnSignUp)
        btnASignUp.setOnClickListener{ view ->
            createProfile()
        }

        return view
    }


    fun createProfile(){
        val intent = Intent(this.context, CreateProfileActivity::class.java)
        startActivityForResult(intent, CREATE_PROFILE_RESULT)
    }


}