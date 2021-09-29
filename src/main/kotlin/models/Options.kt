package fr.gmo.scrumboard.model

import kotlinx.serialization.Serializable
import org.kodein.db.model.Id

@Serializable
data class Options(@Id var id: String = "OPTIONS",
                   val sprintTypeList:List<String> = arrayListOf("US", "TS", "SPIKE", "RETRO", "ANO", "DEBT", "AUTRE"),
                   val sprintItemStatusList: ArrayList<ItemStatus> = arrayListOf(),
                   val sprintItemStatusListStr:ArrayList<String> = arrayListOf()
) {
    init {
        if (sprintItemStatusList.isEmpty()) {
            sprintItemStatusList.addAll(arrayListOf(none, backlog, ongoing, review, validation, done))
        }
        sprintItemStatusListStr.addAll(sprintItemStatusList.map { it.name })
    }
}

@Serializable
data class ItemStatus(var name:String, var colorComp:Long,  var colorGrap:String, var isDefault:Boolean = false,
                      var isDone:Boolean = false, var isStarted:Boolean=false, var withTask:Boolean=false)

val none = ItemStatus("NONE", 0xFFFFFFFF,"0xFFFFFF", isDefault = true)
val backlog = ItemStatus("BACKLOG", 0xFFB3E5FC,"0xB3E5FC")
val ongoing = ItemStatus("ONGOING", 0xFFFFEB3B, "0xFFEB3B", isStarted = true, withTask = true)
val review = ItemStatus("REVIEW", 0xFFFF5722, "0xFF5722", isStarted = true)
val validation = ItemStatus("VALIDATION",0xFFBA68C8, "0xBA68C8", isStarted = true)
val done = ItemStatus("DONE", 0xFF4CAF50, "0x4CAF50", isDone = true)