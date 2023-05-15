package com.example.feemanagementsystem.activity

import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.feemanagementsystem.R
import com.example.feemanagementsystem.databinding.ActivityHomeBinding
import com.example.feemanagementsystem.databinding.FragmentChangePasswordBinding
import com.example.feemanagementsystem.fragment.*
import com.example.feemanagementsystem.global.DB
import com.example.feemanagementsystem.manager.SessionManager
import com.google.android.material.navigation.NavigationView

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
        private val TAG="HomeActivity"
        var session: SessionManager?=null
        var db: DB?=null
        private lateinit var drawer: DrawerLayout
        private lateinit var toggle: ActionBarDrawerToggle
        lateinit var binding: ActivityHomeBinding
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityHomeBinding.inflate(layoutInflater)
            setContentView(binding.root)
            var nav:NavigationView=findViewById(R.id.nav_item)
            var logout:TextView=nav.findViewById(R.id.nav_log_out)
            logout.setOnClickListener{
                logOut()
            }
            db = DB(this)
            session = SessionManager(this)
            setSupportActionBar(binding.homeInclude.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            binding.navItem.setNavigationItemSelectedListener(this)
            drawer = binding.drawerLayout
            toggle = ActionBarDrawerToggle(
                this, drawer, binding.homeInclude.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            drawer.addDrawerListener(toggle)
            toggle.syncState()

            val fragment=FragmentAllMember()
            loadFragment(fragment)
        }
            override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
                super.onPostCreate(savedInstanceState, persistentState)
                toggle.syncState()
            }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        if(item.itemId==R.id.logOutMenu)
            logOut()
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home->{
                    val fragment=FragmentAllMember()
                    loadFragment(fragment)
                    if (drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START)
                }
            }
            R.id.nav_add->{
                loadFragment()
                if (drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START)
                }
            }
            R.id.nav_nav_fee_pending->{
                val fragment=FragmentFeePending()
                loadFragment(fragment)
                if (drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START)
                }
            }
            R.id.nav_update_fee->{
                val fragment=FragmentAddUpdateFee()
                loadFragment(fragment)
                if (drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START)
                }
            }
            R.id.nav_change_password->{
                val fragment=FragmentChangePassword()
                loadFragment(fragment)
                if (drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START)
                }
            }
            R.id.rate_bar->{
                val rateFeedbackDialog = Dialog(this)
                rateFeedbackDialog.setContentView(R.layout.activity_rate_app)
                val feedbackInput = rateFeedbackDialog.findViewById<EditText>(R.id.feedback_input)
                 rateFeedbackDialog.setCancelable(false)
                var lbtn: Button = rateFeedbackDialog.findViewById(R.id.laterbtn)
                    var rbtn:Button = rateFeedbackDialog.findViewById(R.id.ratenow)
                    var rating:RatingBar = rateFeedbackDialog.findViewById(R.id.ratingBar)
                    var emoji:ImageView = rateFeedbackDialog.findViewById(R.id.emoji)
//                var ratee:RatingBarWithEmojis=rateFeedbackDialog.findViewById(R.id.rating_bar)

                    rating.onRatingBarChangeListener= RatingBar.OnRatingBarChangeListener{ _, _, _->
                        val rate = rating.rating
                        if(rate <= 1){
                            emoji.setImageResource(R.drawable.z1)
                        }else if(rate <= 2){
                            emoji.setImageResource(R.drawable.z2)
                        }else if(rate <= 3){
                            emoji.setImageResource(R.drawable.z3)
                        }else if(rate <= 4){
                            emoji.setImageResource(R.drawable.z4)
                        }else{
                            emoji.setImageResource(R.drawable.z5)
                        }
                        lbtn.setOnClickListener{
                            Toast.makeText(this,"Beta feedback dedena baad me",Toast.LENGTH_SHORT).show()
                            rateFeedbackDialog.dismiss()
                        }
                        rbtn.setOnClickListener{
                            Toast.makeText(this,"Thanks for your $rate rating", Toast.LENGTH_SHORT).show()
                            rateFeedbackDialog.dismiss()
                        }
                    }
                    rateFeedbackDialog.create()
                rateFeedbackDialog.show()
                if (drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START)
                }
            }
        }
        return true

    }
    private fun logOut(){
        session?.setLogin(false)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    override fun onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

    private fun loadFragment(fragment:Fragment) {
        var fragmentManager: FragmentManager? = null
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, "Home").commit()
    }
    private fun loadFragment(){
        val fragment = FragmentAddMember()
        val args = Bundle()
        args.putString("ID","")
        fragment.arguments = args
        val fragmentManager:FragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.frame_container,fragment,"FragmentAdd").commit()
    }
        override fun onCreateOptionsMenu(menu: Menu?): Boolean {

            try {

                val inflater = menuInflater
                inflater.inflate(R.menu.menu_main, menu)
            }catch(e:Exception) {
                e.printStackTrace()
            }
                return super.onCreateOptionsMenu(menu)

    }
}