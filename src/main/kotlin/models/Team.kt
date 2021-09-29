package fr.gmo.scrumboard.model

import kotlinx.serialization.Serializable
import org.kodein.db.model.Id

@Serializable
data class Team (@Id var id:String?=null,
                 var name:String="",
                 var email:String = "",
                 var deskPhone:String = "",
                 var cellPhone:String = "",
                 var roles:String = "",
                 var other:String = "",
                 var isActive:Boolean=true)