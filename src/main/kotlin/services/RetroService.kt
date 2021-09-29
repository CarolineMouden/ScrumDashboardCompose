package services

import fr.gmo.scrumboard.model.ActionRetro
import fr.gmo.scrumboard.model.ActionRetroType
import fr.gmo.scrumboard.model.Team
import org.kodein.db.DB
import org.kodein.db.getById
import org.kodein.db.useModels
import java.util.*
import kotlin.collections.ArrayList


interface RetroAction {
    fun changeScreen(screen: String)
    fun get(screen: String): ActionRetro
    fun update(retro: ActionRetro)
    fun delete(retro: ActionRetro)
}

class RetroService(val db: DB, val onUpdate:(List<ActionRetro>) -> Unit) {
    init {
        onUpdate(db.find(ActionRetro::class).all().useModels() {it.toList()})
        db.on(ActionRetro::class).register {
            didDeleteIt {
                onUpdate(db.find(ActionRetro::class).all().useModels() {it.toList()})
            }
            didPut {
                onUpdate(db.find(ActionRetro::class).all().useModels() {it.toList()})
            }
        }
    }

    fun getAction(id: String?): ActionRetro {
        if (id == null) {
            return ActionRetro()
        } else {
            return db.getById(id)?: ActionRetro()
        }
    }

    fun updateItem(retro: ActionRetro, onUpdate: () -> Unit) {
        if (retro.id == null) {
            retro.id = UUID.randomUUID().toString()
        }
        db.put(retro)
        onUpdate()
    }

    fun deleteItem(retro: ActionRetro, onDelete: () -> Unit) {
        retro.id?.let {
            db.delete(ActionRetro::class, db.keyById(ActionRetro::class, it))
            onDelete()
        }
    }
}