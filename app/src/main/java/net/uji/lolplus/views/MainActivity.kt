package net.uji.lolplus.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import net.uji.lolplus.R
import net.uji.lolplus.model.Champ
import net.uji.lolplus.model.User
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener  {

    private var user: User?=null
    private var tema: Int=0
    private lateinit var userShare: SharedPreferences
    private lateinit var temaShare: SharedPreferences
    private lateinit var usersAL: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initShare()
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES || tema==1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { tema() }
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.itemIconTintList=null//para que se vean bien los iconos
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        //setInitialFragment()
        loadUser()
        updateHeader()
        if (savedInstanceState != null) {
            val fragment = supportFragmentManager.getFragment(savedInstanceState, "yourFragmentKey")
            // Reemplazar el fragmento actual con el fragmento restaurado si es necesario
            if (fragment != null) {
                // Reemplazar el fragmento actual con el fragmento restaurado
                replaceFragment(fragment)
            }
        } else {
            // Configuración inicial de los fragmentos si no hay estado guardado
            setInitialFragment()
        }

    }

    private fun initShare() {
        userShare = getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)
        temaShare=getSharedPreferences("tema", Context.MODE_PRIVATE)
        tema = temaShare.getInt("tema",0)
    }
    private fun tema(){//Change the colors and styles
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            tema=0
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            tema=1
        }
        val edit=temaShare.edit()
        edit.putInt("tema",tema)
        edit.apply()

        finish()
        val intent = intent
        startActivity(intent)
    }

    private fun updateHeader() { //to update changes in the name and photo
        val miivavatar = nav_view.getHeaderView(0).ivavatar
        val mitvavatar = nav_view.getHeaderView(0).tvavatar
        //here we would select a photo or a name
        if(user!=null){
            mitvavatar.text = user!!.nick
            if(user!!.champfav=="noone"){
                miivavatar.setImageResource(R.drawable.noone)
            }else{

                val rq = Volley.newRequestQueue(this)
                val imageRequest = ImageRequest("https://opgg-static.akamaized.net/images/lol/champion/${user!!.champfav}.png?image=q_auto,w_140&v=1585730185",
                    { response ->
                        miivavatar.setImageBitmap(response)
                    }, 0, 0, null, null,
                    { error ->
                        // Manejar el error aquí
                    }
                )
                rq.add(imageRequest)
            }
        }else{
            miivavatar.setImageResource(R.drawable.noone)
            mitvavatar.text = "Who are you?"
        }

        miivavatar.setOnClickListener{
            loadUser()
            if(user!=null){
                val myintent = Intent(this, ChoosePictureActivity::class.java)
                startActivity(myintent)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        var fragment: Fragment? = null
        when (item.itemId) {
            R.id.nav_Inicio -> {
                fragment = StartFragment()
            }
            R.id.nav_Champions -> {
                fragment = ChampionsFragment()
            }
            R.id.nav_Meta -> {
                fragment = MetaFragment()
            }
            R.id.nav_user -> {
                fragment = UserFragment()
            }
            R.id.nav_perfil -> {
                loadUser()
                fragment = if(user!=null){
                    ProfileFragment()
                }else{
                    Toast.makeText(this,"There is no active session", Toast.LENGTH_LONG).show()
                    UserFragment()
                }
            }
            R.id.nav_exit -> {
                checkSignout()
                fragment = UserFragment()
            }
        }
        replaceFragment(fragment!!)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setInitialFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(
            R.id.frame,
            StartFragment(),
            "yourFragmentTag"
        )
        fragmentTransaction.commit()
    }

    private fun checkSignout(){// to control if the user sign out, its just a dialog
        loadUser()
        if(user!=null){
            alert("Are you sure you want to sing out?", "Exit") {
                yesButton {
                    deleteUsuarioShare(user!!)
                    updateHeader()
                    FirebaseAuth.getInstance().signOut()
                }
                noButton {}
            }.show()
        }else{
            Toast.makeText(this,"There is no active session", Toast.LENGTH_LONG).show()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun clickChampion(v: View){//go to the second view
        loadUser()
        if(user!=null) {
            val campeonp = v.tag as Champ
            val myintent = Intent(this, DetailActivity::class.java)
            myintent.putExtra("campeon", campeonp)
            startActivity(myintent)
        }else{
            Toast.makeText(this,"To see details you must connect", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadUser () {//control the state of the sesion
        val usuariosShare = userShare.all
        usersAL = ArrayList()
        for (entry in usuariosShare.entries) {
            val jsonUsuario = entry.value.toString()
            val user = Gson().fromJson(jsonUsuario, User::class.java)
            usersAL.add(user)
        }

        if(usersAL.isNotEmpty()){
            user= usersAL[0]
        }
    }
    private fun deleteUsuarioShare (u: User) {
        val edit = userShare.edit()
        toast("${user!!.nick}'s session has been logged out.")
        edit.remove(u.nick)
        edit.apply()
        user=null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val yourFragment = supportFragmentManager.findFragmentByTag("yourFragmentTag")
        if (yourFragment != null) {
            supportFragmentManager.putFragment(outState, "yourFragmentKey", yourFragment)
        }

    }
}
