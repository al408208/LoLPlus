package net.uji.lolplus.model

import android.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_comments.*
import net.uji.lolplus.views.FragmentComments
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommentModel(private val view: FragmentComments ) {
    private lateinit var db: FirebaseFirestore

    private var contcoment=0
    private var norden =0
    private lateinit var comments: List<Comment>




    fun showD(champ: Champ, comentario: Comment) {
        db = FirebaseFirestore.getInstance()
        val builder = AlertDialog.Builder(view.context)
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

    fun loadC(champ: Champ) {
        db = FirebaseFirestore.getInstance()
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
                view.showComments( comments)
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
        view.users=ArrayList()
        documents.forEach { d ->
            val nick = d["nick"] as String
            var champfav = ""
            var posicionfav = ""
            var finicio = ""
            var estado = ""
            if(d["champfav"] !=null){
                champfav = d["champfav"] as String
            }
            if(d["posicionfav"] !=null){
                posicionfav = d["posicionfav"] as String
            }
            if(d["finicio"] !=null){
                finicio = d["finicio"] as String
            }
            if(d["estado"] !=null){
                estado = d["estado"] as String
            }

            view.users.add(User(nick = nick,champfav = champfav, positionfav = posicionfav, fstart = finicio, state = estado))
        }
    }

    private fun fill(){
        contcoment=0
        for(c in comments){
            for(u in view.users){
                if(c.user.nick==u.nick){
                    c.user=u
                }
            }
        }
        contcoment=comments.last().ncoment
        view.showComments(comments)
    }

    fun loadUser () {
        val usuariosShare = view.userShare.all
        view.usersAL = ArrayList()
        for (entry in usuariosShare.entries) {
            val jsonUsuario = entry.value.toString()
            val user = Gson().fromJson(jsonUsuario, User::class.java)
            view.usersAL.add(user)
        }
        view.user= view.usersAL[0]
    }

    fun orderComments(): List<Comment>{
        norden++
        if(norden==0){
            Toast.makeText(view.context,"Order by oldest comment", Toast.LENGTH_LONG).show()
            return comments.sortedBy { it.ncoment}

        }else if(norden==1){
            Toast.makeText(view.context,"Order by most recent comment", Toast.LENGTH_LONG).show()
            norden=-1
            return comments.sortedByDescending { it.ncoment }

        }else{
            return comments
        }
    }

    fun sendC(champ: Champ) {
        db = FirebaseFirestore.getInstance()
        val sdf= SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val fecha= sdf.format(Date())
        val coment=view.etcomentario.text.toString()

        val comentario: MutableMap<String, Any> = HashMap() // diccionario key value
        comentario["usuario"] = view.user.nick
        comentario["fecha"] = fecha
        comentario["comentario"] = coment
        contcoment++
        db.collection("campeones").document(champ.name).collection("comentarios").document(contcoment.toString()).set(comentario)

        view.etcomentario.setText("")
    }


}