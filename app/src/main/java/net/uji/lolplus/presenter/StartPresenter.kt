package net.uji.lolplus.presenter

import net.uji.lolplus.model.StartModel
import net.uji.lolplus.views.StartFragment

class StartPresenter(private val view: StartFragment) {


    private val model: StartModel = StartModel()
    init {
        view.showVideo(model.chooseVideo(view.videos))
    }
    fun link() {
        val url=model.onLinkClicked()
        view.openLink(url)
    }

}