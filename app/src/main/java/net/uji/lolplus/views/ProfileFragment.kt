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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_profile.*

import net.uji.lolplus.R
import net.uji.lolplus.model.User
import net.uji.lolplus.presenter.ProfilePresenter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var userShare: SharedPreferences
    lateinit var usersAL: ArrayList<User>
    lateinit var user: User
    private var positions = arrayListOf ("ADC","MID","TOP","JUN","SUPP")
    private lateinit var pselected:String
    private lateinit var presenter: ProfilePresenter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter= ProfilePresenter(this)
        val fab= requireActivity().findViewById(R.id.fab) as FloatingActionButton
        fab.hide()
        userShare = requireActivity().getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)
        presenter.loadUser()
        loadData()
        btnsaveprofile.setOnClickListener{presenter.save()}
        btnfecha.setOnClickListener { clickDatePicker()  }
    }

    private fun loadData(){//just fill the data
        tvusername.text= user.nick
        tvchampfavprofile.text=user.champfav
        profilestate.setText(user.state)
        tvprofiledate.text= user.fstart
        var inicio=0
        if(user.positionfav!=""){
            for (i in 0 until positions.size) {
                if (positions[i]== user.positionfav){//I take the position of the concept that I am going to edit
                    inicio=i
                    pselected=positions[i]//lo guardo
                }
            }
            positions.removeAt(inicio)
            positions.add(0,pselected)//and I put that same concept but the first
        }else{
            positions=arrayListOf ("","ADC","MID","TOP","JUN","SUPP")
        }
        val adapter=presenter.loadSpinner()
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

    fun saveU(){
        user.state = profilestate.text.toString()
        user.fstart = tvprofiledate.text.toString()
        user.positionfav = pselected
        //usuario= Usuario(usuario.nick,usuario.champfav,"","",etestadoperfil.text.toString())
    }




}
