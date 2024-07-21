package com.ashmit.firebaseauth

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.ashmit.firebaseauth.Firebase.FirebaseAuthHelper
import com.ashmit.firebaseauth.Helper.DialogBox
import com.ashmit.firebaseauth.NavigationDrawer.DeleteAccFragment
import com.ashmit.firebaseauth.NavigationDrawer.HomeFragment
import com.ashmit.firebaseauth.NavigationDrawer.UpdateEmailFragment
import com.ashmit.firebaseauth.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    lateinit var navigationView : NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Creating a nav drawer
        val drawerLayout = binding.drawerLayout
        val toolBar = binding.toolbar
        setSupportActionBar(toolBar)
        navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            R.string.openNavigationDrawer,
            R.string.closeNavigationDrawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            navigationView.setCheckedItem(R.id.nav_home)
        }

        val headerView = navigationView.getHeaderView(0)
        val navName = headerView.findViewById<TextView>(R.id.navHeaderName)
        val navEmail = headerView.findViewById<TextView>(R.id.navHeaderEmail)
        val navImage = headerView.findViewById<ImageView>(R.id.navHeaderImg)
        FirebaseAuthHelper(this).displayUserInfo(navName, navEmail, navImage)

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val fragmentManager = supportFragmentManager
                val currentFragment = fragmentManager.findFragmentById(R.id.fragment_Container)
                if(currentFragment is HomeFragment){
                    DialogBox(this@MainActivity).showDialogBox("Exit" , "Are you sure you want to exit ?"){
                        finish()
                    }
                }else if (fragmentManager.backStackEntryCount > 0){
                    fragmentManager.popBackStack()
                }else{
                    finish()
                }
            }

        })

    }
    //toogle bar
    private fun replaceFragment (fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.fragment_Container , fragment).addToBackStack(null).commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_home -> {
                replaceFragment(HomeFragment())
                navigationView.setCheckedItem(R.id.nav_home)
            }
            R.id.nav_logOut -> {
                DialogBox(this).showDialogBox("Logout" , "Are you sure u want to Logout ?" ){
                    FirebaseAuthHelper(this).signOut {
                        startActivity(Intent(this , LoginScreen::class.java))
                        finish()
                    }
                }
            }
            R.id.nav_deleteAcc -> {
                replaceFragment(DeleteAccFragment())
                navigationView.setCheckedItem(R.id.nav_deleteAcc)
            }
            R.id.nav_updateEmail -> {
                replaceFragment(UpdateEmailFragment())
                navigationView.setCheckedItem(R.id.nav_updateEmail)
            }
            R.id.exit -> {
                DialogBox(this).showDialogBox("Exit" , "Are you sure u want to Exit ?") {
                    finish()
                }
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}