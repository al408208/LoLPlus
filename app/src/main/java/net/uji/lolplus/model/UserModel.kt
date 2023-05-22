package net.uji.lolplus.model

import android.util.Log
import android.view.View
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.nav_header_main.view.*
import net.uji.lolplus.R
import net.uji.lolplus.views.UserFragment

class UserModel(private val view: UserFragment) {
    private lateinit var db: FirebaseFirestore

    fun loadUser() {
        val usuariosShare = view.userShare.all
        view.usersAL = ArrayList()
        for (entry in usuariosShare.entries) {
            val jsonUsuario = entry.value.toString()
            val user = Gson().fromJson(jsonUsuario, User::class.java)
            view.usersAL.add(user)
        }

        if(view.usersAL.isNotEmpty()){
            view.usuario= view.usersAL[0]
        }
    }

    private fun saveUsuario (u:User) {
        val edit = view.userShare.edit()
        edit.putString(u.nick, Gson().toJson(u))
        edit.apply()
    }

    fun registerUser(profileUpdates: UserProfileChangeRequest) {
        val user = FirebaseAuth.getInstance().currentUser
        db = FirebaseFirestore.getInstance()
        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                view!!.usuario= User(user.displayName.toString())
                db.collection("users").document(view!!.usuario!!.nick).set(view!!.usuario!!)
                saveUsuario(view!!.usuario!!)
                updateHeader(view!!.usuario!!)
                Log.d("TAG", "User profile updated.")
            }
        }
    }

    fun getUser(){
        val user = FirebaseAuth.getInstance().currentUser
        db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(user!!.displayName.toString())
        docRef.get().addOnSuccessListener { d ->
            if (d != null) {
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

                view.usuario=User(nick = nick,champfav = champfav, positionfav = posicionfav, fstart = finicio, state = estado)
                saveUsuario(view!!.usuario!!)
                updateHeader(view!!.usuario!!)
            }
        }
    }

    private fun updateHeader(u:User) {

        val navigationView = view.requireActivity().findViewById<View>(R.id.nav_view) as NavigationView
        val miivavatar = navigationView.getHeaderView(0).ivavatar
        val mitvavatar = navigationView.getHeaderView(0).tvavatar
        if(view!!.usuario!!.champfav=="noone"){
            miivavatar.setImageResource(R.drawable.noone)
        }else{

            var rq = Volley.newRequestQueue(view.requireContext())
            val imageRequest = ImageRequest("https://opgg-static.akamaized.net/images/lol/champion/${view!!.usuario!!.champfav}.png?image=q_auto,w_140&v=1585730185",
                { response ->
                    miivavatar.setImageBitmap(response)
                }, 0, 0, null, null,
                { error ->
                    // Manejar el error aquí
                }
            )
            rq.add(imageRequest)
            //Picasso.get().load("https://opgg-static.akamaized.net/images/lol/champion/${usuario!!.champfav}.png?image=q_auto,w_140&v=1585730185").into( miivavatar)
        }
        mitvavatar.text = u.nick
    }
}