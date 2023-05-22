package net.uji.lolplus.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import net.uji.lolplus.views.ChoosePictureActivity

class PictureModel(private val view: ChoosePictureActivity) {

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
                    view.makePictures(champions)
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
            champs.add(
                Champ(
                    name = nombre,
                    history = historia,
                    role = rol,
                    position = posicion,
                    difficulty = dificultad
                )
            )
        }
        champions = ArrayList(champs).sortedBy { it.name }
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

    fun saveUser(u: User, camp: Champ) {
        val edit = view.userShare.edit()
        edit.putString(u.nick, Gson().toJson(u))
        edit.apply()
        db.collection("users").document(view.user.nick).update("champfav",camp.name )
    }
}