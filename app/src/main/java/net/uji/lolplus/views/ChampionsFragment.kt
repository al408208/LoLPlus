package net.uji.lolplus.views

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import net.uji.lolplus.R
import net.uji.lolplus.adapters.ChampionsAdapter
import net.uji.lolplus.model.Champ

class ChampionsFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var adapter: ChampionsAdapter
    private lateinit var searchView: SearchView
    private lateinit var champions: ArrayList<Champ>
    private lateinit var rvChampions: RecyclerView
    private var norden =0
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_champs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //if (savedInstanceState == null) {//por problemas de duplicado de menu
        setHasOptionsMenu(true)
        db = FirebaseFirestore.getInstance()
        val fab= requireActivity().findViewById(R.id.fab) as FloatingActionButton
        fab.show()
        rvChampions = view.findViewById(R.id.rvChampions) as RecyclerView
        initRV()
        getData()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu)
// ************* <Filtro> ************
        val searchItem = menu.findItem(R.id.search)
        searchView = searchItem.actionView as SearchView
        searchView.queryHint ="Search..."
        searchView.setOnQueryTextListener(this)
// ************* </Filtro> ************
    }
    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }
    override fun onQueryTextChange(newText: String): Boolean {
        val original = ArrayList<Champ>(champions)
        adapter.setCampeones(original.filter { campeon -> campeon.name.contains(newText,ignoreCase = true) })
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.orden -> {
                order()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun order(){
        norden++
        if(norden==0){
            adapter.setCampeones(champions.sortedBy { it.name })
            Toast.makeText(context,"Alphabetical order", Toast.LENGTH_LONG).show()
        }else if(norden==1){
            adapter.setCampeones(champions.sortedByDescending { it.name })
            Toast.makeText(context,"Descending alphabetical order", Toast.LENGTH_LONG).show()
        }else if(norden==2){
            adapter.setCampeones(champions.sortedBy { it.role })
            Toast.makeText(context,"Role order", Toast.LENGTH_LONG).show()
        }else if(norden==3){
            adapter.setCampeones(champions.sortedBy { it.difficulty })
            Toast.makeText(context,"Order from least to most difficult", Toast.LENGTH_LONG).show()
            norden=-1
        }
    }

    private fun initRV() {
        adapter = ChampionsAdapter(requireActivity().baseContext, R.layout.row_champ)
        rvChampions.adapter = adapter
        rvChampions.layoutManager = LinearLayoutManager(activity)
    }
    private fun getData(){
        db.collection("campeones")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    documentToList(task.result!!)
                    adapter.setCampeones(champions.sortedBy { it.name })
                } else {
                    Log.w("david","Error getting documents.", task.exception)
                }
            }
    }
    private fun documentToList(documents: QuerySnapshot) {
        champions=ArrayList()
        documents.forEach { d ->
            val nombre = d["nombre"] as String
            val historia = d["historia"] as String
            val rol = d["rol"] as String
            val posicion = d["posicion"] as String
            val dificultad = d["dificultad"] as String
            champions.add(Champ(name = nombre,history = historia,role = rol, position = posicion, difficulty = dificultad))
        }
    }

}
