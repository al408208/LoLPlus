package net.uji.lolplus.presenter

import android.util.Log
import net.uji.lolplus.model.Champ
import net.uji.lolplus.model.ChampionModel
import net.uji.lolplus.views.FragmentChampion
import net.uji.lolplus.views.FragmentSkills

class ChampPresenter(private val view: FragmentChampion)  {

    private val model:ChampionModel= ChampionModel(view)

    fun playSound(){
        model.onSoundButtonClicked()
    }
    fun previous(){
        view.showPicture(model.onPreviousPictureButtonClicked())
    }
    fun next(){
        view.showPicture(model.onNextPictureButtonClicked())
    }

    fun loadDatas(champ: Champ) {
        model.getData()
        model.loadData(champ)
        model.loadSound(champ)
    }

}