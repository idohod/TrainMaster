package models

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.trainMaster.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fragments.HomeFragment
import fragments.InfoFragment
import fragments.HistoryFragment
import fragments.ShareFragment
import utilities.SharedViewModel
class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var headerView: View
    private lateinit var userNameView: TextView
    private lateinit var userEmailView: TextView
    private var numOfQuiz: Int = 0
    private lateinit var traineeName: String
    private  var isCoach = false
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        initViews()
        getNumOfQuiz()
        getTraineeName()

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        sharedViewModel.traineeName.value = this.traineeName

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }
    private fun getTraineeName() {
        val i = intent
        isCoach = i.getBooleanExtra("isCoach", false)
        traineeName = i.getStringExtra("userName").toString()
    }
    private fun getNumOfQuiz() {
        numOfQuiz = intent.getIntExtra("numOfQuiz", 0)
    }
    private fun initViews() {
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        headerView = navigationView.getHeaderView(0)
        userNameView = headerView.findViewById(R.id.nav_user_name)
        userEmailView = headerView.findViewById(R.id.nav_user_email)

        initValues()

        val toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }
    private fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else {
            if (isCoach)
                startActivity(Intent(this, CoachActivity::class.java))
            else
                startActivity(Intent(this, StartPage::class.java))
        }
    }
    private fun moveToStart() {
        val intent = Intent(this, StartPage::class.java)
        intent.putExtra("numOfQuiz", numOfQuiz)
        startActivity(intent)
        finish()
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_home -> replaceFragment(HomeFragment())
            R.id.nav_setting -> replaceFragment(HistoryFragment())
            R.id.nav_share -> replaceFragment(ShareFragment())
            R.id.nav_info -> replaceFragment(InfoFragment())
            R.id.nav_logout -> moveToStart()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun initValues() {
        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("user").document(userId)

        ref.get().addOnSuccessListener {
            if (it != null)
                getUserData(it)
        }
    }
    private fun getUserData(it: DocumentSnapshot) {
        userNameView.text = it.data?.get("name")?.toString() ?: return
        userEmailView.text = it.data?.get("email")?.toString() ?: return
    }
}