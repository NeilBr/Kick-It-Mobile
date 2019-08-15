package com.eit.kickit

import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import androidx.fragment.app.Fragment
import com.eit.kickit.Activities.BucketListActivity
import com.eit.kickit.fragments.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


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
        loadFragment(frag = HomeFragment())
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
/*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    */

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                loadFragment(frag = HomeFragment())
                toolbar.title = "Home"
            }

            R.id.nav_bucket_lists -> {
              //  loadFragment(frag = BucketListsFragment())
                startActivity(Intent(this,BucketListActivity::class.java))
                toolbar.title = "Home"
            }

            R.id.nav_suggest_challenge -> {
                loadFragment(frag = SuggestChallengeFragment())
                toolbar.title = "Home"
            }

            R.id.nav_my_bucket_list -> {
                loadFragment(frag = MyBucketListFragment())
                toolbar.title = "Home"
            }

            R.id.nav_progress -> {
                loadFragment(frag = ProgressFragment())
                toolbar.title = "Home"
            }

            R.id.nav_comrades -> {
                loadFragment(frag = ComradesFragment())
                toolbar.title = "Home"
            }


            /*Over Here I wanna find a way to change the onClick and the name of the menu option according to login state.
               So if the are logged in it displays "Logout" and if the are logged out it displays "Login"
             */
            R.id.nav_login -> {
                loadFragment(frag = LoginFragment())
                toolbar.title = "Home"
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
