package net.uji.lolplus.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import net.uji.lolplus.views.ChampionsFragment

class ChampionsModel(private val view: ChampionsFragment) {
    private val champions: ArrayList<Champ> = ArrayList()
    private var norden = 0



    fun filter(query: String): List<Champ> {
        val original = ArrayList<Champ>(champions)
        return original.filter { cham -> cham.name.contains(query,ignoreCase = true) }
    }

    fun orderChampions(): List<Champ> { //control the order
        norden++
        if(norden==0){
            view.showMessage("Orden alfabetico")
            return champions.sortedBy { it.name }

        }else if(norden==1){
            view.showMessage("Orden alfabetico descendiente")
            return champions.sortedByDescending { it.name }
        }else if(norden==2){
            view.showMessage("Orden por rol")
            return champions.sortedBy { it.role }
        }else if(norden==3){
            view.showMessage("Orden de menor a mayor dificultad")
            norden=-1
            return champions.sortedBy { it.difficulty }
        }else{
            return champions
        }
    }

    fun getData() { //get the firebase data
        val db = FirebaseFirestore.getInstance()
        db.collection("campeones")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    documentToList(task.result!!)
                    view.showChampions(champions.sortedBy { it.name })
                } else {
                    Log.w("david","Error getting documents.", task.exception)
                }
            }
    }
    private fun documentToList(documents: QuerySnapshot) {
        champions.clear()
        documents.forEach { d ->
            val nombre = d["nombre"] as String
            val historia = d["historia"] as String
            val rol = d["rol"] as String
            val posicion = d["posicion"] as String
            val dificultad = d["dificultad"] as String
            champions.add(Champ(name = nombre, history = historia, role = rol, position = posicion, difficulty = dificultad))
        }
    }

}