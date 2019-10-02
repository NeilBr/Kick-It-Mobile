package com.eit.kickit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.eit.kickit.common.FileHandler
import com.eit.kickit.common.Validator
import com.eit.kickit.database.Database
import com.eit.kickit.database.DatabaseConnection
import kotlinx.android.synthetic.main.activity_create_profile.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.security.MessageDigest
import java.sql.*

class CreateProfileActivity : AppCompatActivity() {

    // Variables
    var firstname = ""
    var surname = ""
    var Email = ""
    var phone = ""
    var pword = ""
    var rtpword = ""
    var newPic: Boolean = false
    var profilePic = ""
    private var uri: Uri? = Uri.EMPTY
    lateinit var pic: InputStream

    var LOAD_TYPE: String? = "null"

    // System Functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        LOAD_TYPE = intent.getStringExtra(getString(R.string.LOAD_TYPE))

        if (LOAD_TYPE.equals(getString(R.string.LOAD_CREATE_PROFILE)) || MainActivity.adventurer == null){
            welcome_textview.text = getString(R.string.create_profile)
            signUp.setOnClickListener { view -> signupClick(view)}
        }
        else{
            welcome_textview.text = getString(R.string.update_profile)
            loadDetails()
        }

        val res = resources
        val src = BitmapFactory.decodeResource(res, R.drawable.placeholder_image)
        val dr = RoundedBitmapDrawableFactory.create(res, src)
        dr.isCircular = true
        uploadProfilePic.setImageDrawable(dr)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode == 1000){
            uri = data!!.data
            createRoundImage()
        }
    }

    // Creating Adventurers

    private fun signupClick(view: View) {
        firstname = firstName.text.toString()
        surname = surnameName.text.toString()
        Email = email.text.toString()
        phone = telephone.text.toString()
        pword = password.text.toString()
        rtpword = retype_password.text.toString()

        if (postValidate()) {
            progressBar.visibility = View.VISIBLE

            Database().runQuery(buildQuery(true), false){
                    result -> postAdventurer(result)
            }
        } else {
            Toast.makeText(this, "Please enter all the fields correctly", Toast.LENGTH_SHORT).show()
        }
    }

    private fun postAdventurer(result: Any){
        if(result is String){
            val errors = result.split(' ')

            if (errors[0] == "Duplicate") {
                em_textInput.helperText = "Email Already Exists"
            } else
                Toast.makeText(this@CreateProfileActivity, result, Toast.LENGTH_LONG).show()
            progressBar.visibility = View.INVISIBLE
        }
        else{
            if(newPic)
                FileHandler(this).uploadFile(uri, firstname + surname, false)
            em_textInput.helperText = ""
            Toast.makeText(this@CreateProfileActivity, "Welcome to KickIT", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.INVISIBLE
            this@CreateProfileActivity.finish()
        }
    }

    private fun postValidate(): Boolean {

        var fnValid = false
        var snValid = false
        var emValid = false
        var tpValid = false
        var pValid = false
        var rtpValid = false

        fnValid = Validator.validateView(fn_textInput, firstname, 1)
        snValid = Validator.validateView(sn_textInput, surname, 1)
        emValid = Validator.validateView(em_textInput, Email, 2)
        tpValid = Validator.validateView(tn_textInput, phone, 3)
        pValid = Validator.validateView(pw_textInput, pword, 4)
        rtpValid = Validator.validateView(pwr_textInput, rtpword, 1) && rtpword.equals(pword)

        return fnValid && snValid && emValid && tpValid && pValid && rtpValid
    }

    // Editing Adventurer

    private fun loadDetails(){
        firstName.setText(MainActivity.adventurer?.advFirstName)
        surnameName.setText(MainActivity.adventurer?.advSurname)
        email.setText(MainActivity.adventurer?.advEmail)
        telephone.setText(MainActivity.adventurer?.advTelephone)

        signUp.text = getString(R.string.update_button)
        signUp.setOnClickListener { view -> updateClick(view)}
    }

    private fun updateClick(view: View){

        firstname = firstName.text.toString()
        surname = surnameName.text.toString()
        Email = email.text.toString()
        phone = telephone.text.toString()

        if (updateValidate()){
            progressBar.visibility = View.VISIBLE

            Database().runQuery(buildQuery(false), false){
                    result -> updateAdventurer(result)
            }
        }
        else{
            Toast.makeText(this, "Please enter all the fields correctly", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun updateAdventurer(result: Any){

        if(result is String){
            Toast.makeText(this@CreateProfileActivity, result, Toast.LENGTH_LONG).show()
            progressBar.visibility = View.INVISIBLE
        }
        else{
            em_textInput.helperText = ""
            Toast.makeText(this@CreateProfileActivity, "Profile Updated!", Toast.LENGTH_SHORT).show()

            val advString = "${MainActivity.adventurer?.getID()},$firstname,$surname,$Email,$phone,${MainActivity.adventurer?.advPoints},${MainActivity.adventurer?.advAdmin},$profilePic"
            val sharedPreferences = getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.putString("adventurer", advString)
            editor?.commit()

            if(newPic){
                FileHandler(this).uploadFile(uri, firstname + surname, true)
            }

            MainActivity.header.headerName.text = firstname

            progressBar.visibility = View.INVISIBLE

            this@CreateProfileActivity.setResult(Activity.RESULT_OK)
            this@CreateProfileActivity.finish()
        }
    }

    private fun updateValidate() : Boolean{
        var fnValid = false
        var snValid = false
        var emValid = false
        var tpValid = false
        var pValid = false
        var rtpValid = false

        fnValid = Validator.validateView(fn_textInput, firstname, 1)
        snValid = Validator.validateView(sn_textInput, surname, 1)
        emValid = Validator.validateView(em_textInput, Email, 2)
        tpValid = Validator.validateView(tn_textInput, phone, 3)



        if(pword == ""){
            rtpValid = true
            pValid = true
        }
        else{
            pValid = Validator.validateView(pw_textInput, pword, 4)
            rtpValid = Validator.validateView(pwr_textInput, rtpword, 1) && rtpword.equals(pword)
        }

        return fnValid && snValid && emValid && tpValid && pValid && rtpValid
    }

    // Activity Wide Functions

    fun pickPicture(view: View){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1000)
    }

    private fun createRoundImage(){
        try{
            val ist = contentResolver.openInputStream(uri!!)
            pic = ist!!
            val res = resources
            val src = BitmapFactory.decodeStream(ist)
            val dr = RoundedBitmapDrawableFactory.create(res, src)
            MainActivity.adventurer?.setPic(dr.bitmap)
            dr.isCircular = true
            uploadProfilePic.setImageDrawable(dr)
            newPic = true
        }
        catch(ex: java.lang.Exception){
            ex.printStackTrace()
        }
    }

    private fun buildQuery(create: Boolean) : String{
        profilePic = if(newPic)
            firstname + surname
        else{
            if(!create){
                MainActivity.adventurer!!.getPicLink()
            } else{
                "placeholder"
            }
        }

        return when(create){
            true -> "INSERT INTO `adventurers` (`adv_firstName`,`adv_surname`,`adv_email`,`adv_telephone`,`adv_password`,`adv_profilepic`)" +
                    "VALUES('$firstname','$surname','$Email','$phone','$pword','$profilePic')"
            false -> {

                if(pword == "")
                    return "UPDATE `adventurers` SET " +
                            "adv_firstName = '$firstname'," +
                            "adv_surname = '$surname'," +
                            "adv_email = '$Email'," +
                            "adv_telephone = '$phone'," +
                            "adv_profilepic = '$profilePic'" +
                            "WHERE adv_id = ${MainActivity.adventurer?.getID()}"
                else
                    return "UPDATE `adventurers` SET " +
                            "adv_firstName = '$firstname'," +
                            "adv_surname = '$surname'," +
                            "adv_email = '$Email'," +
                            "adv_telephone = '$phone'," +
                            "adv_profilepic = '$profilePic'" +
                            "adv_password = '$pword'" +
                            "WHERE adv_id = ${MainActivity.adventurer?.getID()}"
            }
        }
    }

    fun cancelClick(view: View) {
        this.finish()
    }
}