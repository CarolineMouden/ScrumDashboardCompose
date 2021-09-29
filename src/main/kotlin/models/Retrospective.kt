package fr.gmo.scrumboard.model

import kotlinx.serialization.Serializable
import org.kodein.db.model.Id

@Serializable
data class ActionRetro(@Id var id:String? = null,
                       var issue:String = "",
                       var solution:String = "",
                       var type:ActionRetroType = ActionRetroType.PASSIF,
                       var sprintName:String = "",
                       var actor: String = "",
                       var isDone:Boolean = false,
                       var comment:String = "") {
}

enum class ActionRetroType {PASSIF, PONCTUEL}
