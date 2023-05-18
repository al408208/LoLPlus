package net.uji.lolplus.views

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_profile.*

import net.uji.lolplus.R
import net.uji.lolplus.model.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var userShare: SharedPreferences
    private lateinit var usersAL: ArrayList<User>
    private lateinit var user: User
    private lateinit var db: FirebaseFirestore
    private var positions = arrayListOf ("ADC","MID","TOP","JUN","SUPP")
    private lateinit var pselected:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        val fab= requireActivity().findViewById(R.id.fab) as FloatingActionButton
        fab.hide()
        userShare = requireActivity().getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)
        loadUser()
        cargarDatos()
        btnsaveprofile.setOnClickListener{saveProfile()}
        btnfecha.setOnClickListener { clickDatePicker()  }
    }

    private fun cargarDatos(){
        tvusername.text= user.nick
        tvchampfavprofile.text=user.champfav
        profilestate.setText(user.state)
        tvprofiledate.text= user.fstart
        var inicio=0
        if(user.positionfav!=""){
            for (i in 0 until positions.size) {
                if (positions[i]== user.positionfav){//cojo la posicion del concepto que voy a editar
                    inicio=i
                    pselected=positions[i]//lo guardo
                }
            }
            positions.removeAt(inicio)
            positions.add(0,pselected)//y meto ese mismo concepto pero el primero
        }else{
            positions=arrayListOf ("","ADC","MID","TOP","JUN","SUPP")
        }
        loadSpinner()
    }

    private fun loadSpinner(){
        val conceptostxt = positions.mapIndexed { index, posicion -> posicion }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, conceptostxt)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerposicion.onItemSelectedListener = this
        spinnerposicion.adapter = adapter
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        pselected = positions[position]
    }

    private fun clickDatePicker() {
        val c = Date()
        var sdf = SimpleDateFormat("dd")
        val dia = sdf.format(c).toInt()
        sdf = SimpleDateFormat("MM")
        val mes = sdf.format(c).toInt()
        sdf = SimpleDateFormat("yyyy")
        val anio = sdf.format(c).toInt()
        val tpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{ view, y, m, d ->
            tvprofiledate.text = "$d-${m+1}-$y"
        },anio,mes-1,dia)

        tpd.show()

    }

    private fun saveProfile(){
        if(profilestate.text.toString().length>150){
            Toast.makeText(context,"Your tate has ${profilestate.text.toString().length} characters", Toast.LENGTH_LONG).show()
        }else{
            user.state=profilestate.text.toString()
            user.fstart=tvprofiledate.text.toString()
            user.positionfav=pselected
            //usuario= Usuario(usuario.nick,usuario.champfav,"","",etestadoperfil.text.toString())
            saveUser(user)
            db.collection("users").document(user.nick)
                .update(mapOf(
                    "estado" to profilestate.text.toString(),
                    "finicio" to user.fstart,
                    "posicionfav" to user.positionfav
                ))
            Toast.makeText(context,"Profile updated successfully", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadUser () {
        val usersShare = userShare.all
        usersAL = ArrayList()
        for (entry in usersShare.entries) {
            val jsonUser = entry.value.toString()
            val user = Gson().fromJson(jsonUser, User::class.java)
            usersAL.add(user)
        }
        user= usersAL[0]
    }
    private fun saveUser (u:User) {
        val edit = userShare.edit()
        edit.putString(u.nick, Gson().toJson(u))
        edit.apply()
    }

}
