package net.uji.lolplus.presenter

import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.fragment_user.*
import net.uji.lolplus.model.UserModel
import net.uji.lolplus.views.UserFragment

class UserPresenter(private val view: UserFragment) {

    private val model: UserModel = UserModel(view)
    fun loadUser() {
        model.loadUser()
    }

    fun regUser() {
        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(view.edUser.text.toString()).build()
        model.registerUser(profileUpdates)
    }

    fun logUser() {
        model.getUser()
    }


}