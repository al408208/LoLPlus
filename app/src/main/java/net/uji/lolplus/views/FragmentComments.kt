package net.uji.lolplus.views

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_comments.*

import net.uji.lolplus.R
import net.uji.lolplus.adapters.ComentAdapter
import net.uji.lolplus.model.Champ
import net.uji.lolplus.model.Comment
import net.uji.lolplus.model.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FragmentComments : Fragment(),ComentAdapter.OnLongClickListenerComent {

    private lateinit var listener: ComentAdapter.OnLongClickListenerComent
    private lateinit var champ: Champ
    private lateinit var adapter: ComentAdapter
    private lateinit var comments: List<Comment>
    private lateinit var users: ArrayList<User>
    private lateinit var rvComments: RecyclerView
    private lateinit var userShare: SharedPreferences
    private lateinit var usersAL: ArrayList<User>
    private lateinit var user: User
    private lateinit var db: FirebaseFirestore
    private var contcoment=0
    private var norden =0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        db = FirebaseFirestore.getInstance()
        val datos = arguments
        champ= datos!!.getSerializable("champ") as Champ
        userShare = requireActivity().getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)
        rvComments = view.findViewById(R.id.rvComentarios) as RecyclerView
        listener=this
        initRV()
        setListener()
        loadUser()
        btn_send.setOnClickListener{sendComment()}
    }

    private fun initRV() {
        adapter = ComentAdapter(requireActivity().baseContext, R.layout.row_comment,listener)
        rvComments.adapter = adapter
        rvComments.layoutManager = LinearLayoutManager(activity)
    }
    private fun setListener() {
        val docRef = db.collection("campeones").document(champ.name).collection("comentarios")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                documentToList(snapshot.documents)
                update()

            } else {
                Log.d("TAG", "Current data: null")
                comments=ArrayList()
                adapter.setComentarios(comments)
            }
        }
    }
    private fun documentToList(documents: List<DocumentSnapshot>) {
        val champs=ArrayList<Comment>()
        comments=ArrayList()
        documents.forEach { d ->
            val ncoment = d.id
            val usuario = d["usuario"] as String
            val fecha = d["fecha"] as String
            val comentario = d["comentario"] as String
            champs.add(Comment(ncoment = ncoment.toInt(),user = User(usuario), date = fecha, comment = comentario))
        }
        comments = if(norden==0){
            ArrayList(champs).sortedBy { it.ncoment }
        }else{
            ArrayList(champs).sortedByDescending { it.ncoment }
        }
    }

    private fun sendComment(){
        val sdf= SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val fecha= sdf.format(Date())
        val coment=etcomentario.text.toString()

        val comentario: MutableMap<String, Any> = HashMap() // diccionario key value
        comentario["usuario"] = user.nick
        comentario["fecha"] = fecha
        comentario["comentario"] = coment
        contcoment++
        db.collection("campeones").document(champ.name).collection("comentarios").document(contcoment.toString()).set(comentario)

        etcomentario.setText("")
    }

    private fun loadUser () {
        val usuariosShare = userShare.all
        usersAL = ArrayList()
        for (entry in usuariosShare.entries) {
            val jsonUsuario = entry.value.toString()
            val user = Gson().fromJson(jsonUsuario, User::class.java)
            usersAL.add(user)
        }
        user= usersAL[0]
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.second, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ordensecond -> {
                order()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun order(){
        norden++
        if(norden==0){
            adapter.setComentarios(comments.sortedBy { it.ncoment})
            Toast.makeText(context,"Order by oldest comment", Toast.LENGTH_LONG).show()
        }else if(norden==1){
            adapter.setComentarios(comments.sortedByDescending { it.ncoment })
            Toast.makeText(context,"Order by most recent comment", Toast.LENGTH_LONG).show()
            norden=-1
        }
    }

    private fun update() {
        db.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    documentToUsuarios(task.result!!)
                    fill()
                } else {
                    Log.w("TAG","Error getting documents.", task.exception)
                }
            }
    }
    private fun documentToUsuarios(documents: QuerySnapshot) {
        users=ArrayList()
        documents.forEach { d ->
            val nick = d["nick"] as String
            val champfav = d["champfav"] as String
            val posicionfav = d["posicionfav"] as String
            val finicio = d["finicio"] as String
            val estado = d["estado"] as String
            users.add(User(nick = nick,champfav = champfav, positionfav = posicionfav, fstart = finicio, state = estado))
        }
    }
    private fun fill(){
        contcoment=0
        for(c in comments){
            for(u in users){
                if(c.user.nick==u.nick){
                    c.user=u
                }
            }
        }
        contcoment=comments.last().ncoment
        adapter.setComentarios(comments)
    }

    override fun OnLongClickComentario(comentario: Comment): Boolean {
        if(comentario.user.nick==user.nick){
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete comment")
            builder.setMessage("Are you sure you want to delete this comment?")

            builder.setPositiveButton("OK") { dialog, whichButton ->
                db.collection("campeones").document(champ.name).collection("comentarios").document(comentario.ncoment.toString())
                    .delete()
                    .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
            }
            builder.setNegativeButton("CANCEL") { dialog, whichButton ->}
            builder.show()
        }
        return true
    }

}
