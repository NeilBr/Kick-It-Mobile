package com.eit.kickit

import android.content.Intent
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
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
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.eit.kickit.common.FileHandler
import com.eit.kickit.common.S3LINK
import com.eit.kickit.fragments.*
import com.eit.kickit.models.Adventurer
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.android.synthetic.main.splash_screen.*
import java.io.File

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //Temp arrayList just to show myBL works
    companion object {

        var adventurer: Adventurer? = null
        lateinit var header: View

    }

    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        sharedPref = getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE)
        val tempPref = sharedPref.getString("adventurer", "null")

        AWSMobileClient.getInstance().initialize(this).execute()

        if (tempPref != "null"){
            loadAdventurer(tempPref)

            if(adventurer?.getPicLink() == " placeholder"){
                loadMainView()

                val src = BitmapFactory.decodeResource(resources, R.drawable.placeholder_image)
                adventurer?.setPic(src)

                loadHeader()

                Toast.makeText(this@MainActivity, "Welcome ${adventurer?.advFirstName}!", Toast.LENGTH_SHORT).show()
                loadFragment(frag = HomeFragment())
            }
            else{
                loadData()
            }
        }
        else{
            loadMainView()
            val src = BitmapFactory.decodeResource(resources, R.drawable.placeholder_image)
            val dr = RoundedBitmapDrawableFactory.create(resources, src)
            dr.isCircular = true

            header.headerImage.setImageDrawable(dr)

            loadFragment(frag = LoginFragment())
        }
    }

    private fun loadMainView(){
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        header = navView.getHeaderView(0)
    }

    private fun loadData(){

        val fileName = adventurer?.getPicLink()

        val transferUtility = FileHandler(this).createTransferUtil()

        val tempFile = File.createTempFile(fileName, ".jpg")

        val downloadObserver = transferUtility.download(S3LINK + fileName, tempFile)

        downloadObserver.setTransferListener(object : TransferListener {

            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED == state) {
                    loadMainView()

                    val bitMap = BitmapFactory.decodeFile(tempFile.path)
                    adventurer?.setPic(bitMap)

                    loadHeader()

                    Toast.makeText(this@MainActivity, "Welcome ${adventurer?.advFirstName}!", Toast.LENGTH_SHORT).show()
                    loadFragment(frag = HomeFragment())
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                val percentDone = percentDonef.toInt()
                splashBar.progress = percentDone
                println(percentDone)
            }

            override fun onError(id: Int, ex: Exception) {
                loadMainView()

                val editor = sharedPref.edit()
                editor.putString("adventurer", "null")
                editor.commit()

                loadFragment(frag = LoginFragment())

                Toast.makeText(this@MainActivity, "Downloading File Failed", Toast.LENGTH_SHORT).show()
                ex.printStackTrace()
            }
        })

    }

    fun loadHeader(){

        val dr = RoundedBitmapDrawableFactory.create(resources, adventurer?.getPic())
        dr.isCircular = true

        header.headerImage.setImageDrawable(dr)
        header.headerName.text = adventurer?.advFirstName

    }

    fun updateProfileClick(view: View){


        if (adventurer == null)
            Toast.makeText(this, "Please Login to be able to view profile!", Toast.LENGTH_LONG).show()
        else{
            try{
                val intent = Intent(this, ViewProfileActivity::class.java)
                startActivity(intent)

                val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            catch(ex: Exception){
                ex.printStackTrace()
            }

        }
    }

    private fun loadAdventurer (advString: String?) {
        val props = advString?.split(',')

        adventurer = Adventurer(props!![1], props[2], props[3], props[4],  props[5].toDouble(), true, props[6].toBoolean())
        adventurer?.setID(Integer.parseInt(props[0]))
        adventurer?.setPicLink(props[7])
    }

    fun logoutClicked(view: View){

        sharedPref = getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE)
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
                loadFragment(frag = BucketListFragment())
                //startActivity(Intent(this,BucketListActivity::class.java))
                toolbar.title = "Home"
              //  loadFragment(frag = BucketListFragment())
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
}
