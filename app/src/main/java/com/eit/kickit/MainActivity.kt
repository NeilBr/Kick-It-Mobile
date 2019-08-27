package com.eit.kickit

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import com.eit.kickit.database.DatabaseConnection
import com.eit.kickit.fragments.*
import com.eit.kickit.models.Adventurer
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {

        var adventurer: Adventurer? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        val sharedPref = getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE)
        val tempPref = sharedPref.getString("adventurer", "null")

        if (tempPref != "null"){
            loadAdventurer(tempPref)
            Toast.makeText(this, "Welcome ${adventurer?.advFirstName}!", Toast.LENGTH_SHORT).show()
            loadFragment(frag = HomeFragment())
            getProfilePic().execute()
        }
        else{
            loadFragment(frag = LoginFragment())
        }

    }

    fun updateProfileClick(view: View){

        if (adventurer == null)
            Toast.makeText(this, "Please Login to be able to view profile!", Toast.LENGTH_LONG).show()
        else{
            val intent = Intent(this, ViewProfileActivity::class.java)
            startActivity(intent)

            val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    fun loadAdventurer (advString: String) {
        val props = advString.split(',')

        adventurer = Adventurer(props[1], props[2], props[3], props[4],  props[5].toDouble(), true, props[6].toBoolean())
        adventurer?.setID(Integer.parseInt(props[0]))
    }

    fun logoutClicked(view: View){

        val sharedPref = getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE)
        val tempPref = sharedPref.getString("adventurer", "null")

        if (tempPref != "null"){
            val editor = sharedPref.edit()
            editor.putString("adventurer", "null")
            editor.commit()

            Toast.makeText(this, "Bye For Now!", Toast.LENGTH_SHORT).show()
            adventurer = null
            loadFragment(frag = LoginFragment())

            val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
            drawerLayout.closeDrawer(GravityCompat.START)
        }


    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                loadFragment(frag = HomeFragment())
                toolbar.title = "Home"
            }

            R.id.nav_bucket_lists -> {
                loadFragment(frag = BucketListsFragment())
                toolbar.title = "Bucket Lists"
            }

            R.id.nav_suggest_challenge -> {
                loadFragment(frag = SuggestChallengeFragment())
                toolbar.title = "Suggest Challenge"
            }

            R.id.nav_my_bucket_list -> {
                loadFragment(frag = MyBucketListFragment())
                toolbar.title = "My Bucketlist"
            }

            R.id.nav_progress -> {
                loadFragment(frag = ProgressFragment())
                toolbar.title = "Progress"
            }

            R.id.nav_comrades -> {
                loadFragment(frag = ComradesFragment())
                toolbar.title = "Comrades"
            }


            /*Over Here I wanna find a way to change the onClick and the name of the menu option according to login state.
               So if the are logged in it displays "Logout" and if the are logged out it displays "Login"
             */
            R.id.nav_login -> {
                loadFragment(frag = LoginFragment())
                toolbar.title = "Login"
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun loadFragment(frag: Fragment){
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.frameLayout, frag)
        fm.commit()
    }

    inner class getProfilePic : AsyncTask<Void, Void, Boolean>() {

        private var CONN: Connection? = null
        private var STMNT: Statement? = null
        private lateinit var RESULT: ResultSet

        override fun onPostExecute(result: Boolean?) {

            try{
                val byteImage = RESULT.getBytes("adv_profilepic")
                headerName.text = "${adventurer?.advFirstName} ${adventurer?.advSurname}"
                if (byteImage == null)
                    loadPlaceholder()
                else
                    loadProfilePic(byteImage)
                CONN?.close()

            }catch(ex: Exception){
                ex.printStackTrace()
            }

        }

        override fun doInBackground(vararg p0: Void?): Boolean {
            try {
                CONN = DatabaseConnection().createConnection()

                val qry = "SELECT adv_profilepic FROM adventurers WHERE adv_email = '${MainActivity.adventurer?.advEmail}'"

                STMNT = CONN!!.createStatement()
                RESULT = STMNT!!.executeQuery(qry)

                return RESULT.next()

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
        }
    }

    private fun loadProfilePic(imageBytes: ByteArray){
        var byteSize = imageBytes.size
        val res = resources
        val src = BitmapFactory.decodeByteArray(imageBytes, 0, byteSize)
        val dr = RoundedBitmapDrawableFactory.create(res, src)
        dr.isCircular = true
        headerImage.setImageDrawable(dr)
    }

    private fun loadPlaceholder(){
        val res = resources
        val src = BitmapFactory.decodeResource(res, R.drawable.placeholder_image)
        val dr = RoundedBitmapDrawableFactory.create(res, src)
        dr.isCircular = true
        headerImage.setImageDrawable(dr)
    }
}
