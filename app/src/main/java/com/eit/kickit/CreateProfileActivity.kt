package com.eit.kickit

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.eit.kickit.database.DatabaseController
import com.eit.kickit.models.Adventurer
import com.google.android.material.textfield.TextInputLayout
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
        }
    }

    fun validation(): Boolean{

        var fnValid = false
        var snValid = false
        var emValid = false
        var tpValid = false
        var pValid = false
        var rtpValid = false

        fnValid = validateView(fn_textInput, firstname, 1)
        snValid = validateView(sn_textInput, surname, 1)
        emValid = validateView(em_textInput, Email, 2)
        tpValid = validateView(tn_textInput, phone, 3)
        pValid = validateView(pw_textInput, pword, 4)
        rtpValid = validateView(pwr_textInput, rtpword, 5)

        return true

    }

    fun validateView(view: TextInputLayout, text: String, type: Int): Boolean{


        var result = false

        when(type){
            1 -> {
                if(text.isNotBlank()){
                    view.helperText = ""
                    result = true
                }
                else{
                    view.helperText = "Required"
                    result = false
                }
            }

            2 -> {
                result = validateView(view, text, 1)
                if (result){
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                        result = false
                        view.helperText = ""
                    }
                    else{
                        result = true
                        view.helperText = "Enter Valid Email"
                    }

                }
            }

            3 -> {
                result = validateView(view, text, 1)
                if (result){
                    if(android.util.Patterns.PHONE.matcher(text).matches() && (text.length == 10)){
                        result = true
                        view.helperText = ""
                    }
                    else {
                        result = false
                        view.helperText = "Enter Valid Phone Number"
                    }
                }
            }

            4 -> {
                result = validateView(view, text, 1)
                //Longer than 8 characters
                //One number
                val check = "a[a-z0-9]+d?".toRegex()

                if(result){
                    if(check.containsMatchIn(text) && (text.length >= 8)){
                        result = true
                        view.helperText = ""
                    }
                    else {
                        result = false
                        view.helperText = "Must contain a number, a letter and be longer than 8 characters"
                    }
                }
            }

            5 -> {
                result = validateView(view, text, 1)

                if(result){
                    if (text.equals(pword)){
                        result = true
                        view.helperText = ""
                    }
                    else {
                        result = false
                        view.helperText = "Passwords Don't match"
                    }
                }
            }
        }

        return result
    }
}
