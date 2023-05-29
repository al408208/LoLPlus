package net.uji.lolplus.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import net.uji.lolplus.views.FragmentSkills

class SkillsModel(private val view: FragmentSkills) {

    private lateinit var db: FirebaseFirestore

    fun getData(champ: Champ) {//just load the info
        db = FirebaseFirestore.getInstance()

        db.collection("campeones").document(champ.name).collection("habilidades")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val skills = documentToList(task.result!!)
                    view.showSkills(skills)
                } else {
                    view.showError("Error getting documents.")
                }
            }
    }

    private fun documentToList(documents: QuerySnapshot): ArrayList<Skill> {
        val skills = ArrayList<Skill>()
        documents.forEach { d ->
            val tecla = d["tecla"] as String
            val nombre = d["nombre"] as String
            val descripcion = d["descripcion"] as String
            val imagen = d["imagen"] as String
            skills.add(Skill(key = tecla, name = nombre, description = descripcion, img = imagen))
        }
        return skills
    }
}