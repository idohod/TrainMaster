package models

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.finalproject.R
import fragments.HomeFragment
import fragments.InfoFragment
import fragments.HistoryFragment
import fragments.ShareFragment
class MenuActivity : AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener{

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private var numOfQuiz:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        initViews()
        getNumOfQuiz()

        if(savedInstanceState ==null){
            replaceFragment(HomeFragment())
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    private fun getNumOfQuiz() {
        numOfQuiz = intent.getIntExtra("numOfQuiz",0)
    }

    private fun initViews() {
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer)

        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }
    private fun replaceFragment(fragment:Fragment){
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,fragment)
        transaction.commit()

    }
    override fun onBackPressed(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            onBackPressedDispatcher.onBackPressed()
    }
    private fun moveToStart() {
        val intent = Intent(this, StartPage::class.java)
        intent.putExtra("numOfQuiz",numOfQuiz)
        startActivity(intent)
        finish()
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.nav_home->replaceFragment(HomeFragment())
            R.id.nav_setting->replaceFragment(HistoryFragment())
            R.id.nav_share->replaceFragment(ShareFragment())
            R.id.nav_info->replaceFragment(InfoFragment())
            R.id.nav_logout->moveToStart()

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

