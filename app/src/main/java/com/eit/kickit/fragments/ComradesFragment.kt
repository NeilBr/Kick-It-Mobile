package com.eit.kickit.fragments

import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eit.kickit.MainActivity
import com.eit.kickit.R
import com.eit.kickit.adapters.Friendlist_Adapter
import com.eit.kickit.database.Database
import com.eit.kickit.models.Adventurer
import kotlinx.android.synthetic.main.fragment_comrades.*
import java.sql.ResultSet

class ComradesFragment : Fragment() {

    private val friends: ArrayList<Adventurer> = ArrayList()
    private lateinit var friendsAdapter: Friendlist_Adapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(container?.context).inflate(R.layout.fragment_comrades, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendListProgress.visibility = View.VISIBLE

        addFriendFab.isEnabled = false

        val query = "SELECT adv_id, adv_firstName, adv_surname, adv_email, adv_telephone, adv_totalPoints, adv_profilepic, adv_TotalSpent, adv_GoldenBootCount " +
                "FROM adventurers AS a INNER JOIN friends AS f ON a.adv_id = f.adv_id2 " +
                "WHERE f.adv_id1 = ${MainActivity.adventurer?.getID()} "

        Database().runQuery(query, true){
            result -> showFriends(result)
        }

        addFriendFab.setOnClickListener(this::showSearchBar)
        searchFab.setOnClickListener(this::searchFriend)
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun showFriends(result: Any){
        addFriendFab.isEnabled = true

        if(result is ResultSet){

            val friendList: ResultSet = result

            while(friendList.next()){
                val friend = Adventurer(
                    friendList.getString("adv_firstName"),
                    friendList.getString("adv_surname"),
                    friendList.getString("adv_email"),
                    friendList.getString("adv_telephone"),
                    friendList.getDouble("adv_totalPoints"),
                    advActive = true,
                    advAdmin = false,
                    advTotalSpent = friendList.getDouble("adv_TotalSpent"),
                    advGoldenBootCount = friendList.getInt("adv_GoldenBootCount")
                )
                friend.setPicLink(friendList.getString("adv_profilepic"))
                friend.setID(friendList.getInt("adv_id"))

                friends.add(friend)
            }

            friendsAdapter = Friendlist_Adapter(friends)
            val layoutM = LinearLayoutManager(activity!!.applicationContext)

            friendsListRV.adapter = friendsAdapter
            friendsListRV.layoutManager = layoutM

            friendListProgress.visibility = View.INVISIBLE
        }
    }

    private fun showSearchBar(view: View){

        friend_email_layout.visibility = View.VISIBLE
        searchFab.visibility = View.VISIBLE
        addFriendFab.visibility = View.INVISIBLE

    }

    private fun searchFriend(view: View){

        friend_email_layout.hideKeyboard()

        friend_email_layout.error = ""

        friendListProgress.visibility = View.VISIBLE

        val email = friend_email_text.text.toString()

        if(email == ""){
            friend_email_layout.error = "Please enter something!"
            friendListProgress.visibility = View.INVISIBLE
        }
        else{
            val query = "SELECT adv_id, adv_firstName, adv_surname, adv_email, adv_telephone, adv_totalPoints, adv_profilepic, adv_TotalSpent, adv_GoldenBootCount FROM adventurers WHERE adv_email= '$email'"

            searchFab.isEnabled = false

            Database().runQuery(query, true){
                    result -> validateFriend(result)
            }
        }
    }

    private fun validateFriend(result: Any){

        searchFab.isEnabled = true

        if (result is ResultSet){

            if(result.next()){
                val friend = Adventurer(
                    result.getString("adv_firstName"),
                    result.getString("adv_surname"),
                    result.getString("adv_email"),
                    result.getString("adv_telephone"),
                    result.getDouble("adv_totalPoints"),
                    advActive = true,
                    advAdmin = false,
                    advTotalSpent = result.getDouble("adv_TotalSpent"),
                    advGoldenBootCount = result.getInt("adv_GoldenBootCount")
                )
                friend.setPicLink(result.getString("adv_profilepic"))
                friend.setID(result.getInt("adv_id"))

                if(friends.contains(friend)){
                    friend_email_layout.error = "Already Friends"
                    friendListProgress.visibility = View.INVISIBLE
                }
                else{
                    friendsAdapter.add(friend)

                    val query = "INSERT INTO `friends` (`adv_id1`,`adv_id2`) VALUES(${MainActivity.adventurer?.getID()},${friend.getID()})"
                    Database().runQuery(query, false){
                        result -> endAdd(result)
                    }
                }
            }
            else{
                friend_email_layout.error = "No such person, try again."
                friendListProgress.visibility = View.INVISIBLE
            }


        }
        else {
            friend_email_layout.error = "Catastrophic Failure"
            friendListProgress.visibility = View.INVISIBLE
        }
    }

    private fun endAdd(result: Any){
        if(result is Boolean){
            friend_email_layout.visibility = View.INVISIBLE
            friend_email_text.text = null
            searchFab.visibility = View.INVISIBLE
            addFriendFab.visibility = View.VISIBLE
        }
        else{
            println(result)
            Toast.makeText(activity!!.applicationContext, "Error adding friend to database![Could be connection]", Toast.LENGTH_SHORT).show()
        }


        friendListProgress.visibility = View.INVISIBLE

    }
}