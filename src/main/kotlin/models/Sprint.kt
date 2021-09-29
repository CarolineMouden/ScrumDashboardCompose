package fr.gmo.scrumboard.model

import kotlinx.serialization.Serializable
import org.kodein.db.model.Id

@Serializable
data class Sprint (
    @Id var id: String?=null,
    var name:String = "",
    var details: SprintDetail = SprintDetail(),
    var isDone: Boolean = false,
    var precedentSprintName:String? = null,
    var members:ArrayList<String> = arrayListOf(),
    var items:ArrayList<SprintItem> = arrayListOf(),
    var days:ArrayList<String> = arrayListOf()
){
    fun updateDetails(options: Options) {
        var charge = 0.0
        var comp = 0.0
        var donePoint = 0.0
        items.forEach {
            charge = charge + it.charge
            comp = comp + it.point
            it.updateDone(options)
            if (it.isDone) {
                donePoint = donePoint + it.point
            }
        }

        details.totalCharge = charge
        details.totalComplexity = comp
        details.donePoints = donePoint
    }

    fun updateTicket(item: SprintItem, oldId:String) : ArrayList<SprintItem> {
        var idsItem = items.filter {it.id.equals(oldId)}
        if (idsItem.isEmpty()){
            items.add(item)
        } else {
            idsItem.forEach {items.set(items.indexOf(it), item) }
        }
        return items
    }

    fun deleteTicket(item: SprintItem) : ArrayList<SprintItem> {
        val idsItem = items.filter { it -> it.id.equals(item.id)}
        items.removeAll(idsItem)
        return items
    }

}
@Serializable
data class SprintDetail(var totalCapacity:Double = 0.0,
                        var totalCharge: Double = 0.0,
                        var totalComplexity:Double = 0.0,
                        var ratio:Double = 1.0,
                        var donePoints:Double = 0.0,
                        var absence:Double = 0.0,
                        var realCapacity: Double = 0.0,
                        var estimatedVelocity: Double = 0.0,
) {
    init {
        realCapacity = totalCapacity - totalCharge
        estimatedVelocity = realCapacity * ratio
    }
}
@Serializable
data class SprintItem(var id:String,
                      var title: String,
                      var type: String,
                      var point: Double = 0.0,
                      var charge: Double = 0.0,
                      var tasks: Int = 0,
                      var isDone : Boolean = false,
                      var history: MutableMap<String, SprintItemHistory> = HashMap()
) {
    fun updateDone(options: Options) {
        val doneStatusList = options.sprintItemStatusList.filter { stat -> stat.isDone }.map { it.name }
        isDone = history.filter {doneStatusList.contains(it.value.status)}.isNotEmpty()
    }
}
@Serializable
data class SprintItemHistory(var status: String, var tasks: Int)

