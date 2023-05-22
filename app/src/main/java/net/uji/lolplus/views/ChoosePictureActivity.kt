package net.uji.lolplus.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.size
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_choose_picture.*
import net.uji.lolplus.R
import net.uji.lolplus.model.Champ
import net.uji.lolplus.model.PictureModel
import net.uji.lolplus.model.User

class ChoosePictureActivity : AppCompatActivity(), View.OnClickListener {

    private var ivmatriz = Array(10) { arrayOfNulls<ImageView>(5)}
    private lateinit var champions: List<Champ>
    lateinit var userShare: SharedPreferences
    lateinit var usersAL: ArrayList<User>
    lateinit var user: User
    private lateinit var model: PictureModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_picture)
        model = PictureModel(this)
        title="CHOOSE YOUR ICON"
        userShare = getSharedPreferences("usuarioShare", Context.MODE_PRIVATE)
        model.getUsers()
        model.getData()
    }


    fun  makePictures(champions: List<Champ>) {
        this.champions=champions
        var cont = 0
        for (i in 0 until lv.size){
            val lh = lv.getChildAt(i) as LinearLayout
            for (j in 0 until lh.size) {
                ivmatriz[i][j] = lh.getChildAt(j) as ImageView
                ivmatriz[i][j]!!.tag = this.champions[cont]

                var rq = Volley.newRequestQueue(this)
                val imageRequest = ImageRequest("https://opgg-static.akamaized.net/images/lol/champion/${this.champions[cont].name}.png?image=q_auto,w_140&v=1585730185",
                    { response ->
                        ivmatriz[i][j]?.setImageBitmap(response)
                    }, 0, 0, null, null,
                    { error ->
                        // Manejar el error aquí
                    }
                )
                rq.add(imageRequest)
                //Picasso.get().load("https://opgg-static.akamaized.net/images/lol/champion/${champions[cont].name}.png?image=q_auto,w_140&v=1585730185").into( ivmatriz[i][j])

                ivmatriz[i][j]!!.setOnClickListener(this)
                cont++
            }
        }
    }

    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onClick(v: View?) {
        val imagenPulsada = v as ImageView
        val camp = imagenPulsada.tag as Champ
        user.champfav= camp.name
        model.saveUser(user,camp)

        val myintent = Intent(this, MainActivity::class.java)
        startActivity(myintent)
    }


}
