package net.uji.lolplus.model

import java.io.Serializable

data class User (
    var nick: String,
    var champfav: String="noone",
    var positionfav: String="",
    var fstart: String="",
    var state: String=""
)

data class Champ (
    var name: String="",
    var history: String="",
    var role: String="",
    var position: String="",
    var difficulty: String=""
):Serializable

data class Skill (
    var key: String,
    var name: String,
    var description: String,
    var img: String
)

data class Comment (
    var ncoment: Int,
    var user: User,
    var date: String,
    var comment: String
)