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
import net.uji.lolplus.presenter.ChampionsPresenter

class ChampionsFragment : Fragment(), SearchView.OnQueryTextListener {

    lateinit var adapter: ChampionsAdapter
    private lateinit var searchView: SearchView
    private lateinit var champions: ArrayList<Champ>
    private lateinit var rvChampions: RecyclerView
    private lateinit var presenter: ChampionsPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_champs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        presenter = ChampionsPresenter(this)
        val fab = requireActivity().findViewById(R.id.fab) as FloatingActionButton
        fab.show()
        rvChampions = view.findViewById(R.id.rvChampions) as RecyclerView
        initRV()
        presenter.loadDatas()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.search)
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search..."
        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        presenter.filterChampions(newText)
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.orden -> {
                presenter.orderChampions()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showChampions(champions: List<Champ>) {
        adapter.setCampeones(champions)
    }


    fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun initRV() {
        adapter = ChampionsAdapter(requireActivity().baseContext, R.layout.row_champ)
        rvChampions.adapter = adapter
        rvChampions.layoutManager = LinearLayoutManager(activity)
    }
}
