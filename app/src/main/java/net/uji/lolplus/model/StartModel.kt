package net.uji.lolplus.model

class StartModel {

    fun chooseVideo(videos: ArrayList<String>): String {
        val n= (Math.random()*5).toInt()
        val videoId = videos[n]
        return videoId
    }
    fun onLinkClicked(): String {
        val url = "https://euw.leagueoflegends.com/es-es/"
        return url
    }
}