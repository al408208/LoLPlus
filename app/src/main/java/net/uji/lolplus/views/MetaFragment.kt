package net.uji.lolplus.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.size
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_meta.*
import kotlinx.android.synthetic.main.row_skills.view.*

import net.uji.lolplus.R
import net.uji.lolplus.model.Champ
import net.uji.lolplus.model.User

class MetaFragment : Fragment(), View.OnClickListener  {
    companion object {
        const val TAG = "campeon"
    }
    private var ivmatriz = Array(5) { arrayOfNulls<ImageView>(3)}
    private val bestchamp = arrayListOf ("Maokai","Mordekaiser","Renekton","Zac","Elise","Ekko","Galio","Cassiopeia","Kassadin","Ezreal","Varus","Vayne","Blitzcrank","Leona","Zilean")
    private lateinit var userShare: SharedPreferences
    private lateinit var usersAL: ArrayList<User>
    private var user: User?=null
    lateinit var champions: List<Champ>
    private lateinit var champ: Champ
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_meta, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        val fab= requireActivity().findViewById(R.id.fab) as FloatingActionButton
        fab.hide()
        userShare = requireActivity().getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)
        loadUser()
        getData()
    }

    private fun  makePictures() {
        var cont = 0
        for (i in 0 until lvmeta.size){
            val lh = lvmeta.getChildAt(i) as LinearLayout
            for (j in 0 until lh.size) {
                ivmatriz[i][j] = lh.getChildAt(j) as ImageView
                ivmatriz[i][j]!!.tag = bestchamp[cont]

                var rq = Volley.newRequestQueue(context)
                val imageRequest = ImageRequest("https://opgg-static.akamaized.net/images/lol/champion/${bestchamp[cont]}.png?image=q_auto,w_140&v=1585730185",
                    { response ->
                        ivmatriz[i][j]?.setImageBitmap(response)
                    }, 0, 0, null, null,
                    { error ->
                        // Manejar el error aquí
                    }
                )
                rq.add(imageRequest)
                //Picasso.get().load("https://opgg-static.akamaized.net/images/lol/champion/${bestchamp[cont]}.png?image=q_auto,w_140&v=1585730185").into( ivmatriz[i][j])
                ivmatriz[i][j]!!.setOnClickListener(this)
                cont++
            }
        }
    }
    private fun getData() {
        db.collection("campeones")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    documentToList(task.result!!)
                    makePictures()
                } else {
                    Log.w(
                        TAG,"Error getting documents.", task.exception
                    )
                }
            }
    }
    private fun documentToList(documents: QuerySnapshot) {
        val champs=ArrayList<Champ>()
        documents.forEach { d ->
            val nombre = d["nombre"] as String
            val historia = d["historia"] as String
            val rol = d["rol"] as String
            val posicion = d["posicion"] as String
            val dificultad = d["dificultad"] as String
            champs.add(Champ(name = nombre, history = historia, role = rol, position = posicion, difficulty = dificultad))
        }
        champions=ArrayList(champs).sortedBy { it.name }
    }

    private fun loadUser () {
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

    override fun onClick(v: View?) {
        val imagenPulsada = v as ImageView
        val camp = imagenPulsada.tag as String
        for (c in champions){
            if(camp==c.name){
                champ=c
            }
        }
        if(user!=null) {
            val myintent = Intent(context, DetailActivity::class.java)
            myintent.putExtra("campeon", champ)
            startActivity(myintent)
        }else{
            Toast.makeText(context,"To see details you must connect", Toast.LENGTH_LONG).show()
        }

    }
}
