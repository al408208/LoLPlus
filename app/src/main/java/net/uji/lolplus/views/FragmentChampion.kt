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
import kotlinx.android.synthetic.main.fragment_champ.*

import net.uji.lolplus.R
import net.uji.lolplus.model.Champ
import net.uji.lolplus.presenter.ChampPresenter

class FragmentChampion : Fragment() {

    private lateinit var rq: RequestQueue
    private lateinit var presenter: ChampPresenter

    private lateinit var champ: Champ
    //private lateinit var sound: MediaPlayer
    private var position = 0
    private lateinit var img: ImageView
    private lateinit var btnSound: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_champ, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val datos = arguments
        champ = datos!!.getSerializable("champ") as Champ
        btnSound = view.findViewById(R.id.btnsound) as Button
        img = view.findViewById(R.id.ivbigpicture)

        btnSound.setOnClickListener { presenter.playSound() }
        view.findViewById<ImageButton>(R.id.btnplus).setOnClickListener { presenter.next() }
        view.findViewById<ImageButton>(R.id.btnless).setOnClickListener { presenter.previous() }

        // Crear instancia del presentador
        presenter = ChampPresenter(this)
        presenter.loadDatas(champ)
        //presenter.getData()
    }
    fun showChampionData(championData: Champ,fame:Int) {
        tvnombredetail.text = championData.name
        tvhsitory.text = championData.history
        tvposicion.text = championData.position
        rq = Volley.newRequestQueue(context)
        val imageRequest = ImageRequest("https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${championData.name}_${position}.jpg",
            { response ->
                img.setImageBitmap(response)
            }, 0, 0, null, null,
            { error ->
                // Manejar el error aquí
            }
        )
        rq.add(imageRequest)
        tvposicion.text=championData.position
        val id= context?.resources?.getIdentifier("dif${championData.difficulty}","drawable", requireContext().packageName)
        ivdificultad.setImageResource(id!!)
        tvfame.text=fame.toString()
    }
    fun showError(message: String) {
        Log.w("david", "Error: $message")
    }

    fun playSound(sound:MediaPlayer) {
        sound.seekTo(0)
        sound.start()
    }

    fun showPicture(imageUrl: String) {
        loadImage(imageUrl)
    }

    private fun loadImage(imageUrl: String) {
        rq = Volley.newRequestQueue(context)
        val imageRequest = ImageRequest(imageUrl,
            { response ->
                img.setImageBitmap(response)
            }, 0, 0, null, null,
            { error ->
                // Manejar el error aquí
            }
        )
        rq.add(imageRequest)
    }
}
