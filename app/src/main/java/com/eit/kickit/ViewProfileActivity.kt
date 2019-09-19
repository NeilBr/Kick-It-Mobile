package com.eit.kickit

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Visibility
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.eit.kickit.common.FileHandler
import com.eit.kickit.database.DatabaseConnection
import com.eit.kickit.fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_view_profile.*
import kotlinx.android.synthetic.main.fragment_login.*
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class ViewProfileActivity : AppCompatActivity() {

    private val UPDATE_PROFILE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)
        loadDetails()
        profilePicLoading.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == UPDATE_PROFILE){
                loadDetails()
            }
        }
    }

    fun editProfileClicked(view: View){
        val intent = Intent(this, CreateProfileActivity::class.java)
        intent.putExtra(getString(R.string.LOAD_TYPE), getString(R.string.LOAD_EDIT_PROFILE))
        startActivityForResult(intent, UPDATE_PROFILE)
    }

    private fun loadDetails(){
        viewEmail.text = MainActivity.adventurer?.advEmail
        viewFirstName.text = MainActivity.adventurer?.advFirstName
        viewSurname.text = MainActivity.adventurer?.advSurname

        loadCompletedChallenges()

        if(MainActivity.adventurer?.getPic() != null){
            profilePicture.setImageBitmap(MainActivity.adventurer?.getPic())
            profilePicLoading.visibility = View.INVISIBLE
        }
        else{
            val bitmap = FileHandler(this).downloadFile(MainActivity.adventurer!!.advFirstName + MainActivity.adventurer!!.advSurname, profilePicture, profilePicLoading)
        }
    }

    private fun loadProfilePic(imageBytes: ByteArray){
        var byteSize = imageBytes.size
        val res = resources
        val src = BitmapFactory.decodeByteArray(imageBytes, 0, byteSize)
        val dr = RoundedBitmapDrawableFactory.create(res, src)
        dr.isCircular = true
        profilePicture.setImageDrawable(dr)
    }

    private fun loadPlaceholder(){
        val res = resources
        val src = BitmapFactory.decodeResource(res, R.drawable.placeholder_image)
        val dr = RoundedBitmapDrawableFactory.create(res, src)
        dr.isCircular = true
        profilePicture.setImageDrawable(dr)
    }

    private fun loadCompletedChallenges(){
        //Will include a a get and then will display them!
        //But for now, DUMMY DATA BOIII

        if (MainActivity.adventurer?.getID() == 1){

            val listItems = arrayOf("Pet a Cat \t\t\t\t 1 point" ,
                "Attend a lecture \t\t\t\t 20 Points",
                "Go to bed on time \t\t\t\t 15 Points",
                "Hike Lady Slipper \t\t\t\t 9 Points",
                "Binge a Series \t\t\t\t 5 Points",
                "Study for a test \t\t\t\t 1 Point",
                "Donate to a charity \t\t\t\t 15 Points",
                "Visit and old Age Home \t\t\t\t 6 Points",
                "Run a 5Km \t\t\t\t 10 Points",
                "Finish the Third year Project \t\t\t\t 50 Points")

            val adapter = ArrayAdapter(this , R.layout.dummy_will_delete, listItems)
            completed_challenges.adapter = adapter
        }
    }
}
