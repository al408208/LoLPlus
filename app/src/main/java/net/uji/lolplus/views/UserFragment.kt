package net.uji.lolplus.views

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.android.synthetic.main.row_skills.view.*
import net.uji.lolplus.R
import net.uji.lolplus.model.PictureModel
import net.uji.lolplus.model.User
import net.uji.lolplus.presenter.UserPresenter


class UserFragment : Fragment() {

    lateinit var userShare: SharedPreferences
    lateinit var usersAL: ArrayList<User>
    var usuario: User?=null
    var user: FirebaseUser?=null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var presenter: UserPresenter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        presenter= UserPresenter(this)
        db = FirebaseFirestore.getInstance()
        val fab= requireActivity().findViewById(R.id.fab) as FloatingActionButton
        fab.hide()
        userShare = requireActivity().getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)

        btnreg.setOnClickListener{ v->onClickUser(v)}
        btnlog.setOnClickListener{ v->onClickUser(v)}
    }

    private fun onClickUser(v: View) {
        val btn = v as Button
        presenter.loadUser()
        btnlog.isEnabled = false
        btnreg.isEnabled = false
        if(usuario==null){
            if (edUser.text.toString().isEmpty() || edPass.text.toString().isEmpty()){
                Toast.makeText(context, "Required fields", Toast.LENGTH_LONG).show()
                return
            }
            val email=edUser.text.toString()+"@gmail.com"
            val password=edPass.text.toString()
            if(btn.id== R.id.btnlog) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        user = auth.currentUser
                        presenter.logUser()
                        Toast.makeText(context,"Logged in successfully", Toast.LENGTH_LONG).show()

                        edPass.setText("")
                        edUser.setText("")
                    } else {
                        val toast: Toast =Toast.makeText(context, "LOG IN ERROR",Toast.LENGTH_SHORT)
                        toast.view?.background?.setColorFilter(Color.parseColor("#C32020") , PorterDuff.Mode.SRC_IN)
                        toast.show()
                    }
                }
            }else if(btn.id== R.id.btnreg){
                if(edUser.length()>15){
                    Toast.makeText(context,"The Nick can't be that long", Toast.LENGTH_LONG).show()
                }else if(edPass.length()<6){
                    Toast.makeText(context,"The password must be at least 6 characters", Toast.LENGTH_LONG).show()
                }else{
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            user = auth.currentUser
                            presenter.regUser()
                            Toast.makeText(context,"Successfully registered", Toast.LENGTH_LONG).show()

                            edPass.setText("")
                            edUser.setText("")
                        } else {
                            val toast: Toast =Toast.makeText(context, "REGISTRATION ERROR",Toast.LENGTH_SHORT)
                            toast.view?.background?.setColorFilter(Color.parseColor("#C32020"), PorterDuff.Mode.SRC_IN)
                            toast.show()
                        }
                    }
                }
            }
        }else{
            Toast.makeText(context,"Sing out first", Toast.LENGTH_LONG).show()
        }
        btnlog.isEnabled = true
        btnreg.isEnabled = true
    }


}
