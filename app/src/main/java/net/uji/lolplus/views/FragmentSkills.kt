package net.uji.lolplus.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import net.uji.lolplus.R
import net.uji.lolplus.adapters.SkillsAdapter
import net.uji.lolplus.model.Champ
import net.uji.lolplus.model.Skill
import net.uji.lolplus.model.SkillsModel

class FragmentSkills : Fragment() {

    private lateinit var champ: Champ
    private lateinit var model: SkillsModel
    private lateinit var adapter: SkillsAdapter
    private lateinit var rvSkills: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_skills, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        champ = arguments?.getSerializable("champ") as Champ
        rvSkills = view.findViewById(R.id.rvSkills)
        initRV()

        // Crear instancia del presentador
        model = SkillsModel(this)
        model.getData(champ)
    }

    private fun initRV() {
        adapter = SkillsAdapter(requireActivity().baseContext, R.layout.row_skills)
        rvSkills.adapter = adapter
        rvSkills.layoutManager = LinearLayoutManager(activity)
    }



    // Método para mostrar las habilidades
    fun showSkills(skills: ArrayList<Skill>) {
        adapter.setHabilidades(skills)
    }

    // Método para mostrar errores
    fun showError(message: String) {
        Log.w("david", "Error getting skills: $message")
    }

}
