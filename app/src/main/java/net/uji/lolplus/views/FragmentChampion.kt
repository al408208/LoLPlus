package net.uji.lolplus.views

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_champ.*
import kotlinx.android.synthetic.main.row_skills.view.*

import net.uji.lolplus.R
import net.uji.lolplus.model.Champ

class FragmentChampion : Fragment() {
    private lateinit var champ: Champ
    private lateinit var sound: MediaPlayer
    private var position=0
    private var fame=0
    private lateinit var db: FirebaseFirestore
    private lateinit var img : ImageView
    private lateinit var rq: RequestQueue
    private lateinit var btnsound: Button
//kk
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_champ, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = FirebaseFirestore.getInstance()
        val datos = arguments
        champ= datos!!.getSerializable("champ") as Champ
        btnsound = view.findViewById(R.id.btnsound) as Button
        img = view.findViewById(R.id.ivbigpicture) as ImageView
        getData()
        loadData()
        loadSound()
        btnsound.setOnClickListener{onClickSound()}
        btnplus.setOnClickListener{ v->passPicture(v)}
        btnless.setOnClickListener{ v->passPicture(v)}

    }


    private fun loadData(){
        tvnombredetail.text=champ.name
        tvhsitory.text=champ.history
        rq = Volley.newRequestQueue(context)
        val imageRequest = ImageRequest("https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${champ.name}_${position}.jpg",
            { response ->
                img.setImageBitmap(response)
            }, 0, 0, null, null,
            { error ->
                // Manejar el error aquí
            }
        )
        rq.add(imageRequest)
        //Picasso.get().load("https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${champ.name}_${position}.jpg").into( ivbigpicture)
        tvposicion.text=champ.position
        var id= context?.resources?.getIdentifier("dif${champ.difficulty}","drawable", requireContext().packageName)
        ivdificultad.setImageResource(id!!)
    }

    private fun loadSound() {// load sonido
        var id= context?.resources?.getIdentifier("${champ.name.toLowerCase()}","raw", requireContext().packageName)
        sound= MediaPlayer.create(context, id!!)
    }
    private fun onClickSound(){
        sound.seekTo(0)
        sound.start()
    }

    private fun passPicture(v: View) {
        val btn = v as ImageButton

        if(btn.id== R.id.btnplus) {
            position++
            if(position==3){
                position=0
            }
            rq = Volley.newRequestQueue(context)
            val imageRequest = ImageRequest("https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${champ.name}_${position}.jpg",
                { response ->
                    img.setImageBitmap(response)
                }, 0, 0, null, null,
                { error ->
                    // Manejar el error aquí
                }
            )
            rq.add(imageRequest)
            //Picasso.get().load("https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${champ.name}_${position}.jpg").into( ivbigpicture)
        }else{
            position--
            if(position==-1){
                position=2
            }
            rq = Volley.newRequestQueue(context)
            val imageRequest = ImageRequest("https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${champ.name}_${position}.jpg",
                { response ->
                    img.setImageBitmap(response)
                }, 0, 0, null, null,
                { error ->
                    // Manejar el error aquí
                }
            )
            rq.add(imageRequest)
            //Picasso.get().load("https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${champ.name}_${position}.jpg").into( ivbigpicture)
        }
    }

    private fun getData() {
        db.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    documentToList(task.result!!)
                    tvfame.text=fame.toString()
                } else {
                    Log.w("david","Error getting documents.", task.exception)
                }
            }
    }
    private fun documentToList(documents: QuerySnapshot) {
        documents.forEach { d ->
            val champfav = d["champfav"] as String
            if(champfav==champ.name){
                fame++
            }
        }
    }

}
