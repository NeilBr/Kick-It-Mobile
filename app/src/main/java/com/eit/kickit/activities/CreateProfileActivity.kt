package com.eit.kickit.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.eit.kickit.R
import com.eit.kickit.common.Validator
import kotlinx.android.synthetic.main.activity_create_profile.*

class CreateProfileActivity : AppCompatActivity() {

    var firstname = ""
    var surname = ""
    var Email = ""
    var phone = ""
    var pword = ""
    var rtpword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
    }

    //Get the data from the form
    //Validate the data
    //Create the object if data is valid
    //Post the object to the DB - Need to wait for a DB
    //Go back to the login screen.
    //Add the onclick for the cancel button.

    fun singupClick(view: View){
        firstname = firstName.text.toString()
        surname = surnameName.text.toString()
        Email = email.text.toString()
        phone = telephone.text.toString()
        pword = password.text.toString()
        rtpword = retype_password.text.toString()

        if(validation()){
            //DatabaseController.createAdventurer(); --Return an adventurer object.
            Toast.makeText(this, "Valid, Do Da Tings", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, "Please enter all the fields correctly", Toast.LENGTH_SHORT).show()
        }
    }

    fun validation(): Boolean{

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
}
