package net.uji.lolplus.views

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.dialogperfil.view.*
import kotlinx.android.synthetic.main.row_comment.view.*
import net.uji.lolplus.R
import net.uji.lolplus.model.Champ
import net.uji.lolplus.model.Comment

class DetailActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener
{
    private val datos = Bundle()
    private lateinit var nameapp: String
    private lateinit var championselected: Champ
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        nameapp = resources.getString(R.string.app_name)
        nav_view.setOnNavigationItemSelectedListener(this)

        championselected=intent.getSerializableExtra("campeon") as Champ
        setInitialFragment()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        when (item.itemId) {
            R.id.navigation_champs -> {
                title = "$nameapp - Campeón"
                fragment = FragmentChampion()
            }
            R.id.navigation_habi-> {
                title = "$nameapp - Habilidades"
                fragment = FragmentSkills()
            }
            R.id.navigation_comen-> {
                title = "$nameapp - Foro"
                fragment = FragmentComments()
            }
        }
        replaceFragment(fragment!!)
        return true
    }
    private fun setInitialFragment() {
        val frag=FragmentChampion()
        datos.putSerializable("champ", championselected)
        frag.arguments = datos
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frame, frag)
        fragmentTransaction.commit()
        title = "$nameapp - Campeón"
    }
    private fun replaceFragment(fragment: Fragment) {
        datos.putSerializable("champ", championselected)
        fragment.arguments = datos
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }

    fun clickComentary(v: View){
        val comentariop = v.tag as Comment
        val builder = AlertDialog.Builder(this)
        builder.setTitle("User profile")
        val inflater = layoutInflater
        val perfilLayout = inflater.inflate(R.layout.dialogperfil, null)

        if(comentariop.user.champfav=="noone"){
            perfilLayout.ivperfildialog.setImageResource(R.drawable.noone)
        }else{

            var rq = Volley.newRequestQueue(this)
            val imageRequest = ImageRequest("https://opgg-static.akamaized.net/images/lol/champion/${comentariop.user.champfav}.png?image=q_auto,w_140&v=1585730185",
                { response ->
                    perfilLayout.ivperfildialog.setImageBitmap(response)
                }, 0, 0, null, null,
                { error ->
                    // Manejar el error aquí
                }
            )
            rq.add(imageRequest)
            //Picasso.get().load("https://opgg-static.akamaized.net/images/lol/champion/${comentariop.user.champfav}.png?image=q_auto,w_140&v=1585730185").into(perfilLayout.ivperfildialog)
        }
        perfilLayout.tvnombredialog.text=comentariop.user.nick
        perfilLayout.tvestadodialog.text=comentariop.user.state
        perfilLayout.tvposicionfavdialog.text=comentariop.user.positionfav
        perfilLayout.tvfechainiciodialog.text=comentariop.user.fstart

        builder.setView(perfilLayout)
        builder.setPositiveButton("Accept") { dialog, whichButton ->  }
        builder.show()
    }

}
