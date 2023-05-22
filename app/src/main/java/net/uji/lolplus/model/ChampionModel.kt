package net.uji.lolplus.model

import android.content.Context
import android.media.MediaPlayer
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import net.uji.lolplus.views.FragmentChampion

class ChampionModel(private val view: FragmentChampion)  {

    private lateinit var sound: MediaPlayer
    private var position = 0
    private lateinit var champ: Champ
    private var fame=0
    private lateinit var db: FirebaseFirestore

    fun loadData(champ: Champ) {
        this.champ = champ
    }

    fun loadSound(champ: Champ) {
        var id= view.requireContext().resources?.getIdentifier("${champ.name.toLowerCase()}","raw", view.requireContext().packageName)
        sound= MediaPlayer.create(view.requireContext(), id!!)
    }

    fun getData() {
        db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result
                    if (documents != null) {
                        documentToList(task.result!!)
                        view.showChampionData(champ,fame)
                    }
                } else {
                    view.showError("Error getting documents.")
                }
            }
    }

    private fun documentToList(documents: QuerySnapshot) {
        documents.forEach { d ->
            val champfav = d["champfav"] as String
            if (champfav == champ.name) {
                fame++
            }
        }
    }

    fun onSoundButtonClicked() {
        view.playSound(sound)
    }

    fun onNextPictureButtonClicked(): String {
        position++
        if (position == 3) {
            position = 0
        }
        return "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${champ.name}_$position.jpg"

    }

    fun onPreviousPictureButtonClicked(): String {
        position--
        if (position == -1) {
            position = 2
        }
        return "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${champ.name}_$position.jpg"
    }
}
