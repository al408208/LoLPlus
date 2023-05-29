package net.uji.lolplus.presenter

import android.widget.ArrayAdapter
import net.uji.lolplus.model.ProfileModel
import net.uji.lolplus.views.ProfileFragment

class ProfilePresenter(private val view: ProfileFragment) {

    private val model: ProfileModel = ProfileModel(view)
    fun loadUser() {
        model.loadUser()
    }

    fun save() {
        model.saveProfile()
    }

    fun loadSpinner(): ArrayAdapter<String> {
        return model.loadSpinner()
    }

}