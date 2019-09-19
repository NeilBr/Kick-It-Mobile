package com.eit.kickit.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.eit.kickit.CreateProfileActivity
import com.eit.kickit.MainActivity
import com.eit.kickit.R
import com.eit.kickit.common.Validator
import com.eit.kickit.database.Database
import com.eit.kickit.models.Adventurer
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_login.*
import java.sql.ResultSet

class LoginFragment : Fragment() {

    private var loginText: TextInputEditText? = null
    private var passwordText: TextInputEditText? = null

    private var email: String = ""
    private var pword:  String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val btnASignUp = view.findViewById<Button>(R.id.btnSignUp)
        btnASignUp.setOnClickListener{
            createProfile()
        }

        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener{
            login()
        }

        loginText = view.findViewById(R.id.emailText)
        passwordText = view.findViewById(R.id.passwordText)

        return view
    }

    private fun createProfile(){
        val intent = Intent(this.context, CreateProfileActivity::class.java)
        intent.putExtra(getString(R.string.LOAD_TYPE), getString(R.string.LOAD_CREATE_PROFILE))
        startActivity(intent)
    }

    private fun login(){
        if (Validator.validateView(email_Layout, loginText!!.text.toString(), 1) || Validator.validateView(passwordLayout, passwordText!!.text.toString(), 1)){

            email = loginText!!.text.toString()
            pword = passwordText!!.text.toString()

            progressBarL.visibility = View.VISIBLE
            val query = "SELECT * FROM adventurers WHERE adv_email= '$email'"

            Database().runQuery(query, true){
                result -> checkLogin(result)
            }
        }
    }

    private fun checkLogin(result: Any){

        val resultSet: ResultSet = result as ResultSet

        if(resultSet.next()){
            email_Layout.helperText = ""

            if (pword == resultSet.getString("adv_password")){

                val advString = "${resultSet.getString("adv_id")},${resultSet.getString("adv_firstName")},${resultSet.getString("adv_surname")},${resultSet.getString("adv_email")},${resultSet.getString("adv_telephone")},${resultSet.getDouble("adv_totalPoints")}, ${resultSet.getBoolean("adv_admin")}, ${resultSet.getString("adv_profilepic")}"

                MainActivity.adventurer = Adventurer(
                    resultSet.getString("adv_firstName"),
                    resultSet.getString("adv_surname"),
                    resultSet.getString("adv_email"),
                    resultSet.getString("adv_telephone"),
                    resultSet.getDouble("adv_totalPoints"),
                    true,
                    resultSet.getBoolean("adv_admin")
                )
                MainActivity.adventurer?.setID(resultSet.getInt("adv_id"))

                saveToLocal(advString)
                Toast.makeText(this@LoginFragment.context, "Welcome ${MainActivity.adventurer?.advFirstName}!", Toast.LENGTH_SHORT).show()

                val fm = fragmentManager!!.beginTransaction()
                fm.replace(R.id.frameLayout, HomeFragment())
                fm.commit()

            }
            else
                passwordLayout.helperText = "Incorrect Password"
        }
        else{
            email_Layout.helperText = "Incorrect Email"
        }

        progressBarL.visibility = View.INVISIBLE
    }

    @SuppressLint("ApplySharedPref")
    private fun saveToLocal(advString: String){

        val sharedPreferences = this.context?.getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString("adventurer", advString)
        editor?.commit()

    }
}