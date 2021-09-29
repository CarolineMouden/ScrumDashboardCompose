package services

import fr.gmo.scrumboard.model.Team
import org.kodein.db.DB
import org.kodein.db.deleteById
import org.kodein.db.getById
import org.kodein.db.useModels
import java.util.*
import kotlin.collections.ArrayList

interface TeamAction {
    fun update(team: Team)
    fun delete(team: Team)
}

class TeamService(val db: DB, val onUpdate:(List<Team>) -> Unit) {
    init {
        onUpdate(db.find(Team::class).all().useModels() {it.toList()})
        db.on(Team::class).register {
            didDeleteIt {
                onUpdate(db.find(Team::class).all().useModels() {it.toList()})
            }
            didPut {
                onUpdate(db.find(Team::class).all().useModels() {it.toList()})
            }
        }
    }

    fun updateItem(item: Team, onUpdate : () -> Unit) {
        if (item.id == null) {
            item.id = UUID.randomUUID().toString()
        }
        db.put(item)
        onUpdate()
    }

    fun deleteItem(item: Team, onDelete : () -> Unit) {
        item.id?.let {
            db.delete(Team::class, db.keyById(Team::class, it))
            onDelete()
        }
    }
}