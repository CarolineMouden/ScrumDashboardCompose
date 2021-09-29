package fr.gmo.scrumboard.model

import kotlinx.serialization.Serializable
import org.kodein.db.model.Id

@Serializable
data class Issue(@Id var id:String?=null,
                 val issue:String = "",
                 var priority: IssuePriority = IssuePriority.NORMAL,
                 var isClosed:Boolean = false,
                 var comment:String = "",
                 var history:ArrayList<IssueStep> = arrayListOf())
@Serializable
data class IssueStep(var date:String="", var action:String="", var actor:String="")

enum class IssuePriority {LOW, NORMAL, HIGH, CRITICAL}