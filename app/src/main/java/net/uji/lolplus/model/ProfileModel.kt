package net.uji.lolplus.model

import android.R
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_profile.*
import net.uji.lolplus.views.ProfileFragment

class ProfileModel(private val view: ProfileFragment) {


    private lateinit var db: FirebaseFirestore
    private var positions = arrayListOf ("ADC","MID","TOP","JUN","SUPP")//spinner array

    fun saveProfile(){

        db = FirebaseFirestore.getInstance()
        if(view.profilestate.text.toString().length>150){
            Toast.makeText(view.context,"Your tate has ${view.profilestate.text.toString().length} characters", Toast.LENGTH_LONG).show()
        }else{
            view.saveU()
            //usuario= Usuario(usuario.nick,usuario.champfav,"","",etestadoperfil.text.toString())
            saveUser(view.user)
            db.collection("users").document(view.user.nick)
                .update(mapOf(
                    "state" to view.profilestate.text.toString(),
                    "fstart" to view.user.fstart,
                    "positionfav" to view.user.positionfav
                ))
            Toast.makeText(view.context,"Profile updated successfully", Toast.LENGTH_LONG).show()
        }
    }

    fun loadUser () {
        val usersShare = view.userShare.all
        view.usersAL = ArrayList()
        for (entry in usersShare.entries) {
            val jsonUser = entry.value.toString()
            val user = Gson().fromJson(jsonUser, User::class.java)
            view.usersAL.add(user)
        }
        view.user= view.usersAL[0]
    }
    fun saveUser (u:User) {
        val edit = view.userShare.edit()
        edit.putString(u.nick, Gson().toJson(u))
        edit.apply()
    }

    fun loadSpinner(): ArrayAdapter<String> {
        val conceptostxt = positions.mapIndexed { index, posicion -> posicion }
        val adapter = ArrayAdapter(view.requireContext(), R.layout.simple_spinner_item, conceptostxt)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        return adapter
    }

}