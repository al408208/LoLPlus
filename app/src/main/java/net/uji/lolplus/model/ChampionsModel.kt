package net.uji.lolplus.model

import android.util.Log
import android.widget.Toast
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

    fun orderChampions(): List<Champ> {
        norden++
        if(norden==0){
            Toast.makeText(view.requireContext(),"Orden alfabetico", Toast.LENGTH_LONG).show()
            return champions.sortedBy { it.name }

        }else if(norden==1){
            Toast.makeText(view.requireContext(),"Orden alfabetico descendiente", Toast.LENGTH_LONG).show()
            return champions.sortedByDescending { it.name }
        }else if(norden==2){
            Toast.makeText(view.requireContext(),"Orden por rol", Toast.LENGTH_LONG).show()
            return champions.sortedBy { it.role }
        }else if(norden==3){
            Toast.makeText(view.requireContext(),"Orden de menor a mayor dificultad", Toast.LENGTH_LONG).show()
            norden=-1
            return champions.sortedBy { it.difficulty }
        }else{
            return champions
        }
    }

    fun getData() {
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