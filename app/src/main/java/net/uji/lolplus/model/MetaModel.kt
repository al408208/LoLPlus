package net.uji.lolplus.model

import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import net.uji.lolplus.views.ChampionsFragment
import net.uji.lolplus.views.ChoosePictureActivity
import net.uji.lolplus.views.MetaFragment
import org.jetbrains.anko.makeCall

class MetaModel(private val view: MetaFragment) {

    private lateinit var champions: List<Champ>
    private lateinit var champ: Champ
    private lateinit var db: FirebaseFirestore

    fun getData() {
        db = FirebaseFirestore.getInstance()
        db.collection("campeones")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    documentToList(task.result!!)
                    view.makePictures()
                } else {
                    view.showMessage("Error getting documents.")
                }
            }
    }


    private fun documentToList(documents: QuerySnapshot) {
        val champs = ArrayList<Champ>()
        documents.forEach { d ->
            val nombre = d["nombre"] as String
            val historia = d["historia"] as String
            val rol = d["rol"] as String
            val posicion = d["posicion"] as String
            val dificultad = d["dificultad"] as String
            champs.add(Champ(name = nombre, history = historia, role = rol, position = posicion, difficulty = dificultad))
        }
        champions = ArrayList(champs).sortedBy { it.name }
    }

    fun getChampionDetails(camp: String): Champ {
        for (c in champions){
            if(camp==c.name){
                champ=c
            }
        }
        return champ

    }

    fun getUsers() {
        val usuariosShare = view.userShare.all
        view.usersAL = ArrayList()
        for (entry in usuariosShare.entries) {
            val jsonUsuario = entry.value.toString()
            val user = Gson().fromJson(jsonUsuario, User::class.java)
            view.usersAL.add(user)
        }

        if(view.usersAL.isNotEmpty()){
            view.user=  view.usersAL[0]
        }
    }
}