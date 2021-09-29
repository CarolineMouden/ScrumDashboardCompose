package fr.gmo.scrumboard.services

import fr.gmo.scrumboard.model.*
import org.kodein.db.DB
import org.kodein.db.getById
import org.kodein.db.useModels
import java.util.*
import kotlin.collections.ArrayList

interface SprintAction {
    fun changeScreen(screen: String)
    fun get(screen: String): Sprint
    fun getId(name: String): String
    fun update(sprint: Sprint)
    fun delete(sprint: Sprint)
    fun lastxSprint(it: String, total: Int): ArrayList<Sprint>
}

class SprintService(val db: DB, val onUpdate:(List<Sprint>) -> Unit) {
    init {
        onUpdate(db.find(Sprint::class).all().useModels() {it.toList()})
        db.on(Sprint::class).register {
            didDeleteIt {
                onUpdate(db.find(Sprint::class).all().useModels() {it.toList()})
            }
            didPut {
                onUpdate(db.find(Sprint::class).all().useModels() {it.toList()})
            }
        }
    }

    fun getSprint(id: String?): Sprint {
        if (id == null) {
            return Sprint()
        } else {
            return db.getById(id)?: Sprint()
        }
    }

    fun getSprintIdFromName(name: String): String {
        val allSprint = db.find(Sprint::class).all().useModels() {it.toList()}
        val sprint =  allSprint.filter { name.equals(it.name) }
        return if (sprint.isEmpty()) "WELCOME" else sprint.first().id!!
    }

    fun lastxSprint(sprintName: String, x:Int):ArrayList<Sprint> {
        val allSprint = db.find(Sprint::class).all().useModels() {it.toList()}
        val listSprint = ArrayList<Sprint>()
        var n = sprintName
        for (i in 0..x) {
            val sprint =  allSprint.filter { n.equals(it.name) }.first()
            listSprint.add(sprint)
            if (!sprint.precedentSprintName.isNullOrEmpty()) {
                n = sprint.precedentSprintName!!
            } else if (i < x) {
                break
            }
        }
        return listSprint
    }

    fun updateItem(sprint: Sprint, onUpdate: (Sprint) -> Unit) {
        if (sprint.id == null) {
            sprint.id = UUID.randomUUID().toString()
        }
        db.put(sprint)
        onUpdate(sprint)
    }

    fun deleteItem(sprint: Sprint, onDelete: () -> Unit) {
        sprint.id?.let {
            db.delete(Sprint::class, db.keyById(Sprint::class, it))
            onDelete()
        }
    }

}

