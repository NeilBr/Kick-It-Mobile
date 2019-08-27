package com.eit.kickit

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
import com.eit.kickit.common.Validator
import com.eit.kickit.database.DatabaseConnection
import kotlinx.android.synthetic.main.activity_create_profile.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.security.MessageDigest
import java.sql.*

class CreateProfileActivity : AppCompatActivity() {

    var firstname = ""
    var surname = ""
    var Email = ""
    var phone = ""
    var pword = ""
    var rtpword = ""
    var newPic: Boolean = false
    var uri: Uri? = Uri.EMPTY
    lateinit var pic: InputStream

    var LOAD_TYPE: String? = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        LOAD_TYPE = intent.getStringExtra(getString(R.string.LOAD_TYPE))

        if (LOAD_TYPE.equals(getString(R.string.LOAD_CREATE_PROFILE)) || MainActivity.adventurer == null){
            welcome_textview.text = getString(R.string.create_profile)
            signUp.setOnClickListener { view -> singupClick(view)}
        }
        else{
            welcome_textview.text = getString(R.string.update_profile)
            pw_textInput.isEnabled = false
            pwr_textInput.isEnabled = false
            loadDetails()
        }

        val res = resources
        val src = BitmapFactory.decodeResource(res, R.drawable.placeholder_image)
        val dr = RoundedBitmapDrawableFactory.create(res, src)
        dr.isCircular = true
        uploadProfilePic.setImageDrawable(dr)
    }

    private fun loadDetails(){
        firstName.setText(MainActivity.adventurer?.advFirstName)
        surnameName.setText(MainActivity.adventurer?.advSurname)
        email.setText(MainActivity.adventurer?.advEmail)
        telephone.setText(MainActivity.adventurer?.advTelephone)

        signUp.text = getString(R.string.update_button)
        signUp.setOnClickListener { view -> updateClick(view)}
    }

    fun pickPicture(view: View){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode == 1000){
            createRoundImage(data)
        }
    }

    private fun createRoundImage(data: Intent?){
        try{
            val ist = contentResolver.openInputStream(data?.data)
            pic = ist
            val res = resources
            val src = BitmapFactory.decodeStream(ist)
            val dr = RoundedBitmapDrawableFactory.create(res, src)
            dr.isCircular = true
            uploadProfilePic.setImageDrawable(dr)
            newPic = true
            uri = data?.data
        }
        catch(ex: java.lang.Exception){
            ex.printStackTrace()
        }
    }

    fun singupClick(view: View) {
        firstname = firstName.text.toString()
        surname = surnameName.text.toString()
        Email = email.text.toString()
        phone = telephone.text.toString()
        pword = password.text.toString()
        rtpword = retype_password.text.toString()

        if (validation()) {
            createAdventurer().execute()
        } else {
            Toast.makeText(this, "Please enter all the fields correctly", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateClick(view: View){

        firstname = firstName.text.toString()
        surname = surnameName.text.toString()
        Email = email.text.toString()
        phone = telephone.text.toString()

        if (updateValidation()){
            updateAdventurer().execute()
        }
        else{
            Toast.makeText(this, "Please enter all the fields correctly", Toast.LENGTH_SHORT).show()
        }
    }

    fun cancelClick(view: View) {
        this.finish()
    }

    fun validation(): Boolean {

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

    private fun updateValidation(): Boolean {
        var fnValid = false
        var snValid = false
        var emValid = false
        var tpValid = false

        fnValid = Validator.validateView(fn_textInput, firstname, 1)
        snValid = Validator.validateView(sn_textInput, surname, 1)
        emValid = Validator.validateView(em_textInput, Email, 2)
        tpValid = Validator.validateView(tn_textInput, phone, 3)

        return fnValid && snValid && emValid && tpValid
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

    fun createByteArray(): ByteArray{
        val ist = contentResolver.openInputStream(uri)
        val bytes = ist.readBytes()
        return bytes
    }

    inner class createAdventurer : AsyncTask<Void, Void, String>() {

        private var CONN: Connection? = null
        private var STMNT: Statement? = null
        private var RESULT: Boolean = false

        override fun onPostExecute(result: String?) {

            progressBar.visibility = View.INVISIBLE

            if (result!! == "") {
                em_textInput.helperText = ""
                Toast.makeText(this@CreateProfileActivity, "Welcome to KickIT", Toast.LENGTH_SHORT).show()
                CONN?.close()
                this@CreateProfileActivity.finish()
            } else {
                val errors = result.split(' ')

                if (errors[0] == "Duplicate") {
                    em_textInput.helperText = "Email Already Exists"
                } else
                    Toast.makeText(this@CreateProfileActivity, result, Toast.LENGTH_LONG).show()
            }
        }

        override fun doInBackground(vararg p0: Void?): String {
            try {
                CONN = DatabaseConnection().createConnection()

                val password = hashPassword(pword)
                var qry = ""

                if (newPic){
                    val imageBytes = createByteArray()

                    val stmnt = CONN?.prepareStatement("INSERT INTO `adventurers` (`adv_firstName`,`adv_surname`,`adv_email`,`adv_telephone`,`adv_password`,`adv_profilepic`)\n" +
                            "VALUES(?,?,?,?,?,?)")
                    stmnt?.setString(1, firstname)
                    stmnt?.setString(2, surname)
                    stmnt?.setString(3, Email)
                    stmnt?.setString(4, phone)
                    stmnt?.setString(5, password)
                    stmnt?.setBytes(6, imageBytes)
                    stmnt?.execute()
                }
                else{
                    qry =
                        "INSERT INTO `adventurers` (`adv_firstName`, `adv_surname`, `adv_email`, `adv_telephone`, `adv_password`)\n" +
                                "VALUES('$firstname','$surname','$Email','$phone','$password')"

                    STMNT = CONN!!.createStatement()
                    STMNT!!.execute(qry)
                }

                return ""
            } catch (ex: SQLException) {
                ex.printStackTrace()
                return ex.message!!
            } catch (ex: Exception) {
                ex.printStackTrace()
                return ex.message!!
            }
        }

        override fun onPreExecute() {
            super.onPreExecute()

            progressBar.visibility = View.VISIBLE
        }
    }

    inner class updateAdventurer : AsyncTask<Void, Void, String>() {

        private var CONN: Connection? = null
        private var STMNT: Statement? = null
        private var RESULT: Boolean = false

        override fun onPostExecute(result: String?) {

            progressBar.visibility = View.INVISIBLE

            if (result!! == "") {
                em_textInput.helperText = ""
                Toast.makeText(this@CreateProfileActivity, "Profile Updated!", Toast.LENGTH_SHORT).show()

                val advString = "${MainActivity.adventurer?.getID()},$firstname,$surname,$Email,$phone,${MainActivity.adventurer?.advPoints},${MainActivity.adventurer?.advAdmin}"
                val sharedPreferences = getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE)
                var editor = sharedPreferences?.edit()
                editor?.putString("adventurer", advString)
                editor?.commit()

                CONN?.close()
                this@CreateProfileActivity.setResult(Activity.RESULT_OK)
                this@CreateProfileActivity.finish()
            } else {
                Toast.makeText(this@CreateProfileActivity, result, Toast.LENGTH_LONG).show()
            }
        }

        override fun doInBackground(vararg p0: Void?): String {
            try {
                CONN = DatabaseConnection().createConnection()

                if (newPic){
                    val imageBytes = createByteArray()

                    val stmnt = CONN?.prepareStatement("UPDATE `adventurers` SET " +
                            "adv_firstName = ?," +
                            "adv_surname = ?," +
                            "adv_email = ?," +
                            "adv_telephone = ?," +
                            "adv_profilepic = ?" +
                            "WHERE adv_id = ${MainActivity.adventurer?.getID()}")

                    stmnt?.setString(1, firstname)
                    stmnt?.setString(2, surname)
                    stmnt?.setString(3, Email)
                    stmnt?.setString(4, phone)
                    stmnt?.setBytes(5, imageBytes)
                    stmnt?.execute()
                }
                else {
                    val qry =
                        "UPDATE`adventurers` " +
                                "SET adv_firstName = '$firstname'," +
                                "adv_surname = '$surname'," +
                                "adv_email = '$Email'," +
                                "adv_telephone = '$phone'" +
                                "WHERE adv_id = ${MainActivity.adventurer?.getID()}"

                    STMNT = CONN!!.createStatement()
                    STMNT!!.execute(qry)
                }

                MainActivity.adventurer?.updateDetails(firstname, surname, Email, phone)
                return ""
            } catch (ex: SQLException) {
                ex.printStackTrace()
                return ex.message!!
            } catch (ex: Exception) {
                ex.printStackTrace()
                return ex.message!!
            }
        }

        override fun onPreExecute() {
            super.onPreExecute()

            progressBar.visibility = View.VISIBLE
        }
    }
}
