package net.uji.lolplus.presenter

import net.uji.lolplus.model.ChampionsModel
import net.uji.lolplus.views.ChampionsFragment

class ChampionsPresenter(private val view: ChampionsFragment) {

    private val model: ChampionsModel = ChampionsModel(view)

    fun loadDatas() {
        model.getData()
    }


    fun filterChampions(query: String) {

        view.showChampions(model.filter(query))
    }

    fun orderChampions() {
        view.showChampions(model.orderChampions())
    }
}
