package com.eit.kickit.fragments

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
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
import com.eit.kickit.database.DatabaseConnection
import com.eit.kickit.models.Adventurer
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_create_profile.*
import kotlinx.android.synthetic.main.fragment_login.*
import java.security.MessageDigest
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class LoginFragment : Fragment() {

    var loginText: TextInputEditText? = null
    var passwordText: TextInputEditText? = null

    var email: String = ""
    var pword:  String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_login, container, false)

        val btnASignUp = view.findViewById<Button>(R.id.btnSignUp)
        btnASignUp.setOnClickListener{ view ->
            createProfile()
        }

        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener{ view ->
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

            checkLogin().execute()
        }
    }

    fun hashPassword(pw: String): String {
        val HEX_CHARS = "0123456789ABCDEF"
        val digest = MessageDigest.getInstance("SHA-1").digest(pw.toByteArray())
        return digest.joinToString(
            separator = "",
            transform = { a ->
                String(
                    charArrayOf(
                        HEX_CHARS[a.toInt() shr 4 and 0x0f],
                        HEX_CHARS[a.toInt() and 0x0f]
                    )
                )
            })
    }

    fun saveToLocal(advString: String){

        val sharedPreferences = this.context?.getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE)
        var editor = sharedPreferences?.edit()
        editor?.putString("adventurer", advString)
        editor?.commit()

        loadAdventurer(advString)
    }

    fun loadAdventurer (advString: String) {
        val props = advString.split(',')

        MainActivity.adventurer = Adventurer(props[1], props[2], props[3], props[4],  props[5].toDouble(), true, props[6].toBoolean())
        MainActivity.adventurer?.setID(Integer.parseInt(props[0]))

    }

    inner class checkLogin : AsyncTask<Void, Void, Boolean>() {

        private var CONN: Connection? = null
        private var STMNT: Statement? = null
        private var RESULT: ResultSet? = null

        override fun onPostExecute(result: Boolean?) {

            progressBarL.visibility = View.INVISIBLE

            if(result!!){
                email_Layout.helperText = ""

                val password = hashPassword(pword)

                if (password.equals(RESULT!!.getString("adv_password"))){

                    val advString = "${RESULT!!.getString("adv_id")},${RESULT!!.getString("adv_firstName")},${RESULT!!.getString("adv_surname")},${RESULT!!.getString("adv_email")},${RESULT!!.getString("adv_telephone")},${RESULT!!.getDouble("adv_totalPoints")}, ${RESULT!!.getBoolean("adv_admin")}"

                    saveToLocal(advString)
                    Toast.makeText(this@LoginFragment.context, "Welcome ${MainActivity.adventurer?.advFirstName}!", Toast.LENGTH_SHORT).show()

                    CONN?.close()

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
        }

        override fun doInBackground(vararg p0: Void?): Boolean {
            try {
                CONN = DatabaseConnection().createConnection()

                val qry = "SELECT * FROM adventurers WHERE adv_email = '$email'"

                STMNT = CONN!!.createStatement()
                RESULT = STMNT!!.executeQuery(qry)

                return RESULT!!.next()

            } catch (ex: SQLException) {
                ex.printStackTrace()
                return false
            } catch (ex: Exception) {
                ex.printStackTrace()
                return false
            }
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progressBarL.visibility = View.VISIBLE
        }
    }

}