package com.eit.kickit.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.eit.kickit.CreateProfileActivity
import com.eit.kickit.MainActivity
import com.eit.kickit.R
import com.eit.kickit.common.FileHandler
import com.eit.kickit.common.S3LINK
import com.eit.kickit.common.Validator
import com.eit.kickit.database.Database
import com.eit.kickit.models.Adventurer
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.io.File
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

                val advString = "${resultSet.getString("adv_id")},${resultSet.getString("adv_firstName")},${resultSet.getString("adv_surname")},${resultSet.getString("adv_email")},${resultSet.getString("adv_telephone")},${resultSet.getDouble("adv_totalPoints")},${resultSet.getBoolean("adv_admin")},${resultSet.getString("adv_profilepic")}"

                MainActivity.adventurer = Adventurer(
                    resultSet.getString("adv_firstName"),
                    resultSet.getString("adv_surname"),
                    resultSet.getString("adv_email"),
                    resultSet.getString("adv_telephone"),
                    resultSet.getDouble("adv_totalPoints"),
                    true,
                    resultSet.getBoolean("adv_admin"),
                    resultSet.getDouble("adv_TotalSpent"),
                    resultSet.getInt("adv_GoldenBootCount")
                )
                MainActivity.adventurer?.setID(resultSet.getInt("adv_id"))
                MainActivity.adventurer?.setPicLink(resultSet.getString("adv_profilepic"))

                if(MainActivity.adventurer?.getPicLink() == "placeholder"){
                    progressBarL.visibility = View.INVISIBLE

                    MainActivity.header.headerName.text = MainActivity.adventurer?.advFirstName

                    saveToLocal(advString)
                    Toast.makeText(this@LoginFragment.context, "Welcome ${MainActivity.adventurer?.advFirstName}!", Toast.LENGTH_SHORT).show()

                    progressBarL.visibility = View.INVISIBLE

                    val fm = fragmentManager!!.beginTransaction()
                    fm.replace(R.id.frameLayout, HomeFragment())
                    fm.commit()
                }
                else{
                    val fileName = MainActivity.adventurer?.getPicLink()

                    val transferUtility = FileHandler(activity!!.applicationContext).createTransferUtil()

                    val tempFile = File.createTempFile(fileName!!, ".jpg")

                    val downloadObserver = transferUtility.download(S3LINK + fileName, tempFile)

                    downloadObserver.setTransferListener(object : TransferListener {

                        override fun onStateChanged(id: Int, state: TransferState) {
                            if (TransferState.COMPLETED == state) {
                                val bitMap = BitmapFactory.decodeFile(tempFile.path)
                                MainActivity.adventurer?.setPic(bitMap)

                                val dr = RoundedBitmapDrawableFactory.create(resources, MainActivity.adventurer?.getPic())
                                dr.isCircular = true

                                MainActivity.header.headerName.text = MainActivity.adventurer?.advFirstName
                                MainActivity.header.headerImage.setImageDrawable(dr)

                                saveToLocal(advString)
                                Toast.makeText(this@LoginFragment.context, "Welcome ${MainActivity.adventurer?.advFirstName}!", Toast.LENGTH_SHORT).show()

                                progressBarL.visibility = View.INVISIBLE

                                val fm = fragmentManager!!.beginTransaction()
                                fm.replace(R.id.frameLayout, HomeFragment())
                                fm.commit()
                            }
                        }

                        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                            val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                            val percentDone = percentDonef.toInt()
                            println(percentDone)
                        }

                        override fun onError(id: Int, ex: Exception) {
                            progressBarL.visibility = View.INVISIBLE
                            Toast.makeText(activity!!.applicationContext, "Downloading File Failed", Toast.LENGTH_SHORT).show()
                            ex.printStackTrace()
                        }
                    })
                }
            }
            else {
                progressBarL.visibility = View.INVISIBLE
                passwordLayout.helperText = "Incorrect Password"
            }
        }
        else{
            progressBarL.visibility = View.INVISIBLE
            email_Layout.helperText = "Incorrect Email"
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun saveToLocal(advString: String){

        val sharedPreferences = this.context?.getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString("adventurer", advString)
        editor?.commit()

    }
}