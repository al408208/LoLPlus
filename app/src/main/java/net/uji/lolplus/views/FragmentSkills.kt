package net.uji.lolplus.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

import net.uji.lolplus.R
import net.uji.lolplus.adapters.SkillsAdapter
import net.uji.lolplus.model.Champ
import net.uji.lolplus.model.Skill

class FragmentSkills : Fragment() {

    private lateinit var champ: Champ
    private lateinit var adapter: SkillsAdapter
    private lateinit var skills: ArrayList<Skill>
    private lateinit var rvSkills: RecyclerView
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_skills, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = FirebaseFirestore.getInstance()
        val datos = arguments
        champ= datos!!.getSerializable("champ") as Champ
        rvSkills = view.findViewById(R.id.rvSkills) as RecyclerView
        initRV()
        getData()

    }
    private fun initRV() {
        adapter = SkillsAdapter(requireActivity().baseContext, R.layout.row_skills)
        rvSkills.adapter = adapter
        rvSkills.layoutManager = LinearLayoutManager(activity)
    }
    private fun getData() {
        db.collection("campeones").document(champ.name).collection("habilidades")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    documentToList(task.result!!)
                    adapter.setHabilidades(skills)
                } else {
                    Log.w("david","Error getting documents.", task.exception)
                }
            }
    }
    private fun documentToList(documents: QuerySnapshot) {
        skills=ArrayList()
        documents.forEach { d ->
            val tecla = d["tecla"] as String
            val nombre = d["nombre"] as String
            val descripcion = d["descripcion"] as String
            val imagen = d["imagen"] as String
            skills.add(Skill(key = tecla,name = nombre, description = descripcion, img = imagen))
        }
    }

}
