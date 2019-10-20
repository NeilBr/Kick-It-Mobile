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
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.eit.kickit.common.FileHandler
import com.eit.kickit.common.S3LINK
import com.eit.kickit.database.Database
import com.eit.kickit.database.DatabaseConnection
import com.eit.kickit.fragments.HomeFragment
import com.eit.kickit.fragments.LoginFragment
import com.eit.kickit.models.Adventurer
import com.eit.kickit.models.ChallengeInvite
import kotlinx.android.synthetic.main.activity_view_profile.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.splash_screen.*
import java.io.File
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class ViewProfileActivity : AppCompatActivity() {

    private val UPDATE_PROFILE = 1001
    private var userID = 0

    private var displayType = -1

    private var itemArray: ArrayList<String> = ArrayList()
    private var inviteArray: ArrayList<ChallengeInvite> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)
        viewProfileBar.visibility = View.VISIBLE

        displayType = intent.getIntExtra("display", -1)

        if(displayType == 0)
            loadDetails()
        else{
            val friend = intent.getSerializableExtra("friend") as Adventurer
            loadFriend(friend)
        }
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
        userID = MainActivity.adventurer!!.getID()

        viewEmail.text = MainActivity.adventurer?.advEmail
        viewFirstName.text = MainActivity.adventurer?.advFirstName
        viewSurname.text = MainActivity.adventurer?.advSurname

        textView15.text = getString(R.string.invites)

        val query = "SELECT CONCAT(a.adv_firstName, ' ', a.adv_surname) AS adv_name, c.c_name, ci_id, c.c_id " +
                "FROM challenge_invites AS ci INNER JOIN adventurers AS a ON ci.adv_id1 = a.adv_id INNER JOIN challenges AS c ON ci.c_id = c.c_id " +
                "WHERE ci.adv_id2 = $userID AND ci.ci_status = 0"

        Database().runQuery(query, true){
            result -> loadChallengeInvites(result)
        }

        val dr = RoundedBitmapDrawableFactory.create(resources, MainActivity.adventurer?.getPic())
        dr.isCircular = true
        profilePicture.setImageDrawable(dr)
    }

    private fun loadChallengeInvites(result: Any){

        if(result is ResultSet){

            itemArray = ArrayList()

            while(result.next()){

                val newInvite = ChallengeInvite(
                    result.getInt("ci_id"),
                    result.getInt("c_id"),
                    result.getString("adv_name"),
                    result.getString("c_name"))

                inviteArray.add(newInvite)

                val string = newInvite.adv_name + " invited you to: \t " + newInvite.c_name
                itemArray.add(string)

            }

            val adapter = ArrayAdapter(this, R.layout.dummy_will_delete, itemArray)
            completed_challenges.adapter = adapter

            completed_challenges.setOnItemClickListener{ _, _, position, _ ->
                acceptInvite(position)
            }

            completed_challenges.setOnItemLongClickListener { _, _, position, _ ->
                declineInvite(position)
                true
            }


            viewProfileBar.visibility = View.INVISIBLE

        }

    }

    private fun declineInvite(position: Int){
        Toast.makeText(this, "Declining Invite", Toast.LENGTH_SHORT).show()

        val invite = inviteArray[position]

        val query = "DELETE FROM challenge_invites " +
                "WHERE ci_id = ${invite.id}"

        Database().runQuery(query, false){
            result ->
                if(result is String){
                    Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show()
                    println(result)
                }
                else{
                    val adapter = completed_challenges.adapter as ArrayAdapter<*>
                    itemArray.removeAt(position)
                    inviteArray.removeAt(position)
                    adapter.notifyDataSetChanged()

                    Toast.makeText(this, "Invite Declined!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun acceptInvite(position: Int){

        Toast.makeText(this, "Accepting Invite", Toast.LENGTH_SHORT).show()

        val invite = inviteArray[position]
        val query = "UPDATE challenge_invites " +
                "SET `ci_status` = 1 " +
                "WHERE ci_id = ${invite.id}"

        Database().runQuery(query, false){

            result ->
                if(result is String){
                    println(result)
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }
                else{
                    val query1 = "SELECT adv_id FROM bucketlist_challenges WHERE c_id = ${inviteArray[position].c_id} AND adv_id = ${MainActivity.adventurer!!.getID()}"

                    val adapter = completed_challenges.adapter as ArrayAdapter<*>
                    itemArray.removeAt(position)
                    inviteArray.removeAt(position)
                    adapter.notifyDataSetChanged()

                    Database().runQuery(query1, true){ result2 ->
                        if(result2 is ResultSet){
                            if(!(result2.next())){
                                val query2 = "INSERT INTO bucketlist_challenges(bl_id,c_id,adv_id) VALUES(-1,${inviteArray[position].c_id},${MainActivity.adventurer!!.getID()})"
                                Database().runQuery(query2, false){ result1  ->
                                    if(result1 is String){
                                        Toast.makeText(this, "Something went Wrong!", Toast.LENGTH_SHORT).show()
                                        println(result1)
                                    }
                                }
                            }
                            else{
                                Toast.makeText(this, "Uhhh", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    Toast.makeText(this, "Invite Accepted!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun addToBucketList(){



    }

    private fun loadFriend(friend: Adventurer){
        btnEditProfile.visibility = View.INVISIBLE

        userID = friend.getID()

        viewEmail.text = friend.advEmail
        viewFirstName.text = friend.advFirstName
        viewSurname.text = friend.advSurname

        val query = "SELECT c.c_id, c.c_name, c.c_points " +
                "FROM adv_challenges_completed AS a " +
                "INNER JOIN challenges AS c ON a.c_id = c.c_id " +
                "WHERE a.adv_id = $userID "
        Database().runQuery(query, true){
                result -> loadCompletedChallenges(result)
        }

        if(friend.getPicLink() == "placeholder"){
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.placeholder_image)
            val dr = RoundedBitmapDrawableFactory.create(resources, bitmap)
            dr.isCircular = true
            profilePicture.setImageDrawable(dr)
        }
        else{

            friendPicLoad.visibility = View.VISIBLE

            val fileName = friend.getPicLink()

            val transferUtil = FileHandler(this).createTransferUtil()

            val tempFile = File.createTempFile(fileName, ".jpg")

            val downloadObserver = transferUtil.download(S3LINK + fileName, tempFile)

            downloadObserver.setTransferListener(object : TransferListener {

                override fun onStateChanged(id: Int, state: TransferState) {
                    if (TransferState.COMPLETED == state) {
                        val bitMap = BitmapFactory.decodeFile(tempFile.path)
                        val dr = RoundedBitmapDrawableFactory.create(resources, bitMap)
                        dr.isCircular = true
                        profilePicture.setImageDrawable(dr)

                        friendPicLoad.visibility = View.INVISIBLE
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                    val percentDone = percentDonef.toInt()
                    println(percentDone)
                }

                override fun onError(id: Int, ex: Exception) {
                    ex.printStackTrace()
                }
            })

        }


    }

    private fun loadCompletedChallenges(result: Any){
        itemArray = ArrayList()

        if(result is ResultSet){

            while(result.next()){

                val display = result.getString("c_name") + " \t\t\t " + result.getString("c_points")
                itemArray.add(display)

            }

            val adapter = ArrayAdapter(this, R.layout.dummy_will_delete, itemArray)
            completed_challenges.adapter = adapter
            viewProfileBar.visibility = View.INVISIBLE

        }
        else{
            println("SOMETHING WENT WRONG --------" + result)
        }
    }
}
