package net.uji.lolplus.views

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_comments.*

import net.uji.lolplus.R
import net.uji.lolplus.adapters.ComentAdapter
import net.uji.lolplus.model.Champ
import net.uji.lolplus.model.Comment
import net.uji.lolplus.model.User
import net.uji.lolplus.presenter.CommentPresenter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FragmentComments : Fragment(),ComentAdapter.OnLongClickListenerComent {

    private lateinit var listener: ComentAdapter.OnLongClickListenerComent
    private lateinit var champ: Champ
    private lateinit var adapter: ComentAdapter
    private lateinit var rvComments: RecyclerView
    lateinit var userShare: SharedPreferences
    lateinit var usersAL: ArrayList<User>
    lateinit var user: User
    lateinit var users: ArrayList<User>

    private lateinit var presenter: CommentPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        val datos = arguments
        presenter= CommentPresenter(this)
        champ= datos!!.getSerializable("champ") as Champ
        userShare = requireActivity().getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)
        rvComments = view.findViewById(R.id.rvComentarios) as RecyclerView
        listener=this
        initRV()
        presenter.loadComments(champ)
        presenter.loadUser()
        btn_send.setOnClickListener{presenter.sendComment(champ)}
    }

    private fun initRV() {
        adapter = ComentAdapter(requireActivity().baseContext, R.layout.row_comment,listener)
        rvComments.adapter = adapter
        rvComments.layoutManager = LinearLayoutManager(activity)
    }

    fun showComments(comments: List<Comment>) {
        adapter.setComentarios(comments)
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.second, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ordensecond -> {
                presenter.orderC()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun OnLongClickComentario(comentario: Comment): Boolean {

        if(comentario.user.nick==user.nick){
            presenter.showDialog(champ,comentario)

        }
        return true
    }

}
