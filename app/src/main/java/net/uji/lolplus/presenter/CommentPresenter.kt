package net.uji.lolplus.presenter

import net.uji.lolplus.model.Champ
import net.uji.lolplus.model.Comment
import net.uji.lolplus.model.CommentModel
import net.uji.lolplus.views.FragmentComments

class CommentPresenter(private val view: FragmentComments) {


    private val model: CommentModel = CommentModel(view)

    fun showDialog(champ: Champ, comentario: Comment) {
        model.showD(champ,comentario)
    }

    fun loadComments(champ: Champ) {
        model.loadC(champ)
    }
    fun orderC(){
        view.showComments(model.orderComments())
    }

    fun loadUser() {
        model.loadUser()
    }

    fun sendComment(champ: Champ) {
        model.sendC(champ)
    }
}