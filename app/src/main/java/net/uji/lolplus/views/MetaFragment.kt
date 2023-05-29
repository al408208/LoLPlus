package net.uji.lolplus.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.size
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton

import net.uji.lolplus.R
import net.uji.lolplus.model.MetaModel
import net.uji.lolplus.model.User

class MetaFragment : Fragment(), View.OnClickListener  {

    private var ivmatriz = Array(5) { arrayOfNulls<ImageView>(3) }
    private lateinit var lvmeta: LinearLayout
    private val bestchamp = arrayListOf("Maokai", "Mordekaiser", "Renekton", "Zac", "Elise", "Ekko", "Galio", "Cassiopeia", "Kassadin", "Ezreal", "Varus", "Vayne", "Blitzcrank", "Leona", "Zilean")

    lateinit var userShare: SharedPreferences
    lateinit var usersAL: ArrayList<User>
    var user: User? = null
    private lateinit var model: MetaModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_meta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = MetaModel(this)
        val fab = requireActivity().findViewById(R.id.fab) as FloatingActionButton
        fab.hide()
        userShare = requireActivity().getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)
        lvmeta = view.findViewById(R.id.lvmeta) as LinearLayout
        model.getUsers()
        model.getData()
    }


    fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }


    fun makePictures() {//fill in all photos
        var cont = 0
        for (i in 0 until lvmeta.size) {
            val lh = lvmeta.getChildAt(i) as LinearLayout
            for (j in 0 until lh.size) {
                ivmatriz[i][j] = lh.getChildAt(j) as ImageView
                ivmatriz[i][j]!!.tag = bestchamp[cont]

                val rq = Volley.newRequestQueue(context)
                val imageRequest = ImageRequest("https://opgg-static.akamaized.net/images/lol/champion/${bestchamp[cont]}.png?image=q_auto,w_140&v=1585730185",
                    { response ->
                        ivmatriz[i][j]?.setImageBitmap(response)
                    }, 0, 0, null, null,
                    { error ->
                        // Manejar el error aquí
                    }
                )
                rq.add(imageRequest)

                ivmatriz[i][j]!!.setOnClickListener(this)
                cont++
            }
        }
    }

    override fun onClick(v: View?) {
        val imagenPulsada = v as ImageView
        val camp = imagenPulsada.tag as String

        if(user!=null) {
            val myintent = Intent(context, DetailActivity::class.java)
            myintent.putExtra("campeon", model.getChampionDetails(camp))
            startActivity(myintent)
        }else{
            Toast.makeText(context,"Para ver detalles debes conectarte", Toast.LENGTH_LONG).show()
        }
    }
}

