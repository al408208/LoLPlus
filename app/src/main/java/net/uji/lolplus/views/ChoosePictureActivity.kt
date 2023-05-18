package net.uji.lolplus.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.size
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_choose_picture.*
import kotlinx.android.synthetic.main.row_skills.view.*
import net.uji.lolplus.R
import net.uji.lolplus.model.Champ
import net.uji.lolplus.model.User

class ChoosePictureActivity : AppCompatActivity(), View.OnClickListener {

    private var ivmatriz = Array(10) { arrayOfNulls<ImageView>(5)}
    private lateinit var champions: List<Champ>
    private lateinit var userShare: SharedPreferences
    private lateinit var usersAL: ArrayList<User>
    private lateinit var user: User
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_picture)
        db = FirebaseFirestore.getInstance()
        title="CHOOSE YOUR ICON"
        userShare = getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)
        loadUser()
        getData()
    }
    private fun getData() {
        db.collection("campeones")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    documentToList(task.result!!)
                    makePictures()
                } else {
                    Log.w("david","Error getting documents.", task.exception)
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
            champs.add(Champ(name = nombre, history = historia,role = rol, position = posicion, difficulty = dificultad))
        }
        champions=ArrayList(champs).sortedBy { it.name }
    }

    private fun  makePictures() {
        var cont = 0
        for (i in 0 until lv.size){
            val lh = lv.getChildAt(i) as LinearLayout
            for (j in 0 until lh.size) {
                ivmatriz[i][j] = lh.getChildAt(j) as ImageView
                ivmatriz[i][j]!!.tag = champions[cont]

                var rq = Volley.newRequestQueue(this)
                val imageRequest = ImageRequest("https://opgg-static.akamaized.net/images/lol/champion/${champions[cont].name}.png?image=q_auto,w_140&v=1585730185",
                    { response ->
                        ivmatriz[i][j]?.setImageBitmap(response)
                    }, 0, 0, null, null,
                    { error ->
                        // Manejar el error aquí
                    }
                )
                rq.add(imageRequest)
                //Picasso.get().load("https://opgg-static.akamaized.net/images/lol/champion/${champions[cont].name}.png?image=q_auto,w_140&v=1585730185").into( ivmatriz[i][j])

                ivmatriz[i][j]!!.setOnClickListener(this)
                cont++
            }
        }
    }

    override fun onClick(v: View?) {
        val imagenPulsada = v as ImageView
        val camp = imagenPulsada.tag as Champ
        user.champfav= camp.name
        saveUser(user)
        db.collection("users").document(user.nick).update("champfav",camp.name )
        val myintent = Intent(this, MainActivity::class.java)
        startActivity(myintent)
    }

    private fun loadUser () {
        val usuariosShare = userShare.all
        usersAL = ArrayList()
        for (entry in usuariosShare.entries) {
            val jsonUsuario = entry.value.toString()
            val user = Gson().fromJson(jsonUsuario, User::class.java)
            usersAL.add(user)
        }
        user= usersAL[0]
    }
    private fun saveUser(u:User) {
        val edit = userShare.edit()
        edit.putString(u.nick, Gson().toJson(u))
        edit.apply()
    }

}
